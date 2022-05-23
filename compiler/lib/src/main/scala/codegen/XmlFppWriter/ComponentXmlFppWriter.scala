package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._
import scala.xml.Node

/** Writes out an F Prime XML component as FPP source */
object ComponentXmlFppWriter extends LineUtils {

  /** Writes a component file */
  def writeComponentFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMember(file))
      yield FppWriter.tuMember(tuMember)

  /** Writes an imported file */
  private def writeImportedFile
    (memberGenerator: FppBuilder.MemberGenerator)
    (file: XmlFppWriter.File): XmlFppWriter.Result =
      for (members <- FppBuilder.mapChildren(file, memberGenerator))
        yield Line.blankSeparated (FppWriter.componentMember) (members)

  /** Writes a commands file */
  val writeCommandsFile: XmlFppWriter.File => XmlFppWriter.Result = writeImportedFile(FppBuilder.MemberGenerator.Command) _

  /** Writes a params file */
  val writeParamsFile: XmlFppWriter.File => XmlFppWriter.Result = writeImportedFile(FppBuilder.MemberGenerator.Param) _

  /** Writes a ports file */
  val writePortsFile: XmlFppWriter.File => XmlFppWriter.Result = writeImportedFile(FppBuilder.MemberGenerator.Port) _

  /** Writes a tlm channels file */
  val writeTlmChannelsFile: XmlFppWriter.File => XmlFppWriter.Result = writeImportedFile(FppBuilder.MemberGenerator.TlmChannel) _

  /** Writes an events file */
  val writeEventsFile: XmlFppWriter.File => XmlFppWriter.Result = writeImportedFile(FppBuilder.MemberGenerator.Event) _

  /** Writes an internal ports file */
  val writeInternalPortsFile: XmlFppWriter.File => XmlFppWriter.Result = writeImportedFile(FppBuilder.MemberGenerator.InternalPort) _

  /** Builds FPP for translating Component XML */
  private object FppBuilder {

    /** Generates the list of TU members */
    def tuMember(file: XmlFppWriter.File): Result.Result[Ast.TUMember] =
      for {
        component <- defComponentAnnotated(file)
      }
      yield XmlFppWriter.tuMember(
        component,
        Ast.TUMember.DefComponent.apply,
        Ast.ModuleMember.DefComponent.apply,
        file
      )

    /** Component member generator */
    trait MemberGenerator {

      val xmlName: String

      /** Generates a member node */
      def generateMemberNode(file: XmlFppWriter.File, node: scala.xml.Node):
        Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] =
          Left(file.semanticError(s"$xmlName not implemented"))

      /** Generates a list of member nodes */
      def generateMemberNodes(file: XmlFppWriter.File, node: scala.xml.Node):
        Result.Result[List[Ast.Annotated[Ast.ComponentMember.Node]]] =
          for (node <- generateMemberNode(file, node))
            yield List(node)

      /** Generates a list of members */
      final def generateMembers(file: XmlFppWriter.File, node: scala.xml.Node): Result.Result[List[Ast.ComponentMember]] =
        generateMemberNodes(file, node) match {
          case Left(error) => Left(error)
          case Right(aNodes) => Right(aNodes.map(Ast.ComponentMember(_)))
        }

    }

    /** Constructs an enum component member */
    def constructEnumMember(enumAnnotated: Ast.Annotated[Ast.DefEnum]):
      Ast.Annotated[Ast.ComponentMember.DefEnum] = {
        val (pre, e, post) = enumAnnotated
        (pre, Ast.ComponentMember.DefEnum(AstNode.create(e)), post)
      }

    /** Extracts enum definitions in argument types */
    def translateArgEnums(file: XmlFppWriter.File, xmlNode: scala.xml.Node):
      Result.Result[List[Ast.Annotated[Ast.ComponentMember.DefEnum]]] =
        for (enums <- FormalParamsXmlFppWriter.defEnumAnnotatedList(file, xmlNode))
        yield enums.map(constructEnumMember)

    /** Translates an optional integer attribute */
    def translateIntegerOpt(xmlNode: scala.xml.Node, name: String): Option[AstNode[Ast.ExprLiteralInt]] =
      XmlFppWriter.getAttributeOpt(xmlNode, name).map(
          text => AstNode.create(Ast.ExprLiteralInt(text))
      )

    /** Translates an optional queue full attribute */
    def translateQueueFullOpt(file: XmlFppWriter.File, xmlNode: scala.xml.Node):
      Result.Result[Option[Ast.QueueFull]] = {
        val xmlQueueFullOpt = XmlFppWriter.getAttributeOpt(xmlNode, "full")
        xmlQueueFullOpt match {
          case Some("assert") => Right(Some(Ast.QueueFull.Assert))
          case Some("block") => Right(Some(Ast.QueueFull.Block))
          case Some("drop") => Right(Some(Ast.QueueFull.Drop))
          case Some(xmlQueueFull) =>
            Left(file.semanticError(s"invalid queue full behavior $xmlQueueFull"))
          case None => Right(None)
        }
      }

    /** Translates an optional queue full attribute as an AST node */
    def translateQueueFullNodeOpt(file: XmlFppWriter.File, xmlNode: scala.xml.Node):
      Result.Result[Option[AstNode[Ast.QueueFull]]] =
        for (qfo <- translateQueueFullOpt(file, xmlNode))
          yield qfo.map(AstNode.create(_))

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File): Node => Result.Result[Ast.TypeName] = 
      file.translateType(node => file.getAttribute(node, "data_type")) _

    case object MemberGenerator {

      case object Include extends MemberGenerator {
        
        val xmlName = "import_dictionary"

        override def generateMemberNode(file: XmlFppWriter.File, xmlNode: scala.xml.Node) =
          for {
            child <- file.getUniqueChild(xmlNode)
          }
          yield {
            val path = child.toString
            val fppPath = path.replaceAll("\\.xml$", ".fppi")
            val fileName = fppPath.split("/").toList.reverse.head
            val fileNameNode = AstNode.create(fileName)
            val specIncludeNode = AstNode.create(Ast.SpecInclude(fileNameNode))
            val memberNode = Ast.ComponentMember.SpecInclude(specIncludeNode)
            val annotation = if (fileName == fppPath) Nil else {
              val s = s"original path was $path"
              List(XmlFppWriter.constructNote(s))
            }
            (annotation, memberNode, Nil)
          }

      }

      case object Port extends MemberGenerator {

        val xmlName = "port"

        def general(file: XmlFppWriter.File, xmlNode: scala.xml.Node): Result.Result[Ast.SpecPortInstance.General] = {
          import Ast.SpecPortInstance._
          for {
            xmlKind <- file.getAttribute(xmlNode, "kind")
            kind <- xmlKind match {
              case "async_input" => Right(AsyncInput)
              case "guarded_input" => Right(GuardedInput)
              case "output" => Right(Output)
              case "sync_input" => Right(SyncInput)
              case _ => Left(file.semanticError(s"invalid port kind $xmlKind"))
            }
            name <- file.getAttribute(xmlNode, "name")
            xmlPort <- file.getAttribute(xmlNode, "data_type")
            queueFull <- translateQueueFullOpt(file, xmlNode)
          }
          yield {
            val size = XmlFppWriter.getAttributeOpt(xmlNode, "max_number").map(
              text => AstNode.create(Ast.ExprLiteralInt(text))
            )
            val port = xmlPort match {
              case "Serial" => None
              case _ => Some(XmlFppWriter.FppBuilder.translateQualIdent(xmlPort))
            }
            val priority = translateIntegerOpt(xmlNode, "priority")
            General(kind, name, size, port, priority, queueFull.map(AstNode.create(_)))
          }
        }

        def special(file: XmlFppWriter.File, xmlNode: scala.xml.Node, role: String): Result.Result[Ast.SpecPortInstance.Special] = {
          import Ast.SpecPortInstance._
          for {
            kind <- role match {
              case "Cmd" => Right(CommandRecv)
              case "CmdRegistration" => Right(CommandReg)
              case "CmdResponse" => Right(CommandResp)
              case "LogEvent" => Right(Ast.SpecPortInstance.Event)
              case "ParamGet" => Right(ParamGet)
              case "ParamSet" => Right(ParamSet)
              case "Telemetry" => Right(Telemetry)
              case "LogTextEvent" => Right(TextEvent)
              case "TimeGet" => Right(TimeGet)
              case _ => Left(file.semanticError(s"invalid role $role"))
            }
            name <- file.getAttribute(xmlNode, "name")
          }
          yield Special(kind, name)
        }

        override def generateMemberNode(file: XmlFppWriter.File, xmlNode: scala.xml.Node) = {
          for {
            comment <- file.getComment(xmlNode)
            member <- XmlFppWriter.getAttributeOpt(xmlNode, "role") match {
              case Some(role) => special(file, xmlNode, role)
              case None => general(file, xmlNode)
            }
          }
          yield {
            val node = AstNode.create(member)
            val memberNode = Ast.ComponentMember.SpecPortInstance(node)
            (comment, memberNode, Nil)
          }
        }

      }

      case object InternalPort extends MemberGenerator {

        val xmlName = "internal_interface"

        override def generateMemberNodes(file: XmlFppWriter.File, xmlNode: scala.xml.Node) =
          for {
            annotatedEnumMemberNodes <- translateArgEnums(file, xmlNode)
            comment <- file.getComment(xmlNode)
            name <- file.getAttribute(xmlNode, "name")
            params <- FormalParamsXmlFppWriter.formalParamList(file, xmlNode)
            queueFull <- translateQueueFullOpt(file, xmlNode)
          }
          yield {
            val annotatedPortMemberNode = {
              val priority = translateIntegerOpt(xmlNode, "priority")
              val internalPort = Ast.SpecInternalPort(name, params, priority, queueFull)
              val node = AstNode.create(internalPort)
              val memberNode = Ast.ComponentMember.SpecInternalPort(node)
              (comment, memberNode, Nil)
            }
            annotatedEnumMemberNodes :+ annotatedPortMemberNode
          }

      }

      case object Command extends MemberGenerator {

        val xmlName = "command"

        override def generateMemberNodes(file: XmlFppWriter.File, xmlNode: scala.xml.Node) =
          for {
            xmlKind <- file.getAttribute(xmlNode, "kind")
            kind <- xmlKind match {
              case "async" => Right(Ast.SpecCommand.Async)
              case "guarded" => Right(Ast.SpecCommand.Guarded)
              case "sync" => Right(Ast.SpecCommand.Sync)
              case _ => Left(file.semanticError(s"invalid command kind $xmlKind"))
            }
            annotatedEnumMemberNodes <- translateArgEnums(file, xmlNode)
            comment <- file.getComment(xmlNode)
            name <- file.getAttribute(xmlNode, "mnemonic")
            params <- FormalParamsXmlFppWriter.formalParamList(file, xmlNode)
            queueFull <- translateQueueFullNodeOpt(file, xmlNode)
          }
          yield {
            val annotatedCommandMemberNode = {
              val priority = translateIntegerOpt(xmlNode, "priority")
              val opcode = translateIntegerOpt(xmlNode, "opcode")
              val command = Ast.SpecCommand(
                kind,
                name,
                params,
                opcode,
                priority,
                queueFull
              )
              val node = AstNode.create(command)
              val memberNode = Ast.ComponentMember.SpecCommand(node)
              (comment, memberNode, Nil)
            }
            annotatedEnumMemberNodes :+ annotatedCommandMemberNode
          }

      }

      case object Event extends MemberGenerator {

        val xmlName = "event"

        override def generateMemberNodes(file: XmlFppWriter.File, xmlNode: scala.xml.Node) =
          for {
            annotatedEnumMemberNodes <- translateArgEnums(file, xmlNode)
            comment <- file.getComment(xmlNode)
            name <- file.getAttribute(xmlNode, "name")
            params <- FormalParamsXmlFppWriter.formalParamList(file, xmlNode)
            xmlSeverity <- file.getAttribute(xmlNode, "severity")
            severity <- xmlSeverity match {
              case "ACTIVITY_HI" => Right(Ast.SpecEvent.ActivityHigh)
              case "ACTIVITY_LO" => Right(Ast.SpecEvent.ActivityLow)
              case "COMMAND" => Right(Ast.SpecEvent.Command)
              case "DIAGNOSTIC" => Right(Ast.SpecEvent.Diagnostic)
              case "FATAL" => Right(Ast.SpecEvent.Fatal)
              case "WARNING_HI" => Right(Ast.SpecEvent.WarningHigh)
              case "WARNING_LO" => Right(Ast.SpecEvent.WarningLow)
              case _ => Left(file.semanticError(s"invalid severity $xmlSeverity"))
            }
            xmlFormat <- file.getAttribute(xmlNode, "format_string")
          }
          yield {
            val annotatedEventMemberNode = {
              val id = translateIntegerOpt(xmlNode, "id")
              val (formatOpt, note) = 
                XmlFppWriter.FppBuilder.translateFormatOpt(Some(xmlFormat))
              val format = formatOpt match {
                case Some(format) => format
                case None => "{}"
              }
              val throttle = translateIntegerOpt(xmlNode, "throttle")
              val event = Ast.SpecEvent(
                name,
                params,
                severity,
                id,
                AstNode.create(format),
                throttle
              )
              val node = AstNode.create(event)
              val memberNode = Ast.ComponentMember.SpecEvent(node)
              (note ++ comment, memberNode, Nil)
            }
            annotatedEnumMemberNodes :+ annotatedEventMemberNode
          }

      }

      case object Param extends MemberGenerator {

        val xmlName = "parameter"

        override def generateMemberNodes(file: XmlFppWriter.File, xmlNode: scala.xml.Node) = {
          for {
            enumAnnotatedOpt <- XmlFppWriter.FppBuilder.InlineEnumBuilder.defEnumAnnotatedOpt(file)(xmlNode)
            comment <- file.getComment(xmlNode)
            name <- file.getAttribute(xmlNode, "name")
            typeName <- translateType(file)(xmlNode)
          }
          yield {
            val id = translateIntegerOpt(xmlNode, "id")
            val xmlDefaultOpt = XmlFppWriter.getAttributeOpt(xmlNode, "default")
            val defaultOpt = xmlDefaultOpt.flatMap(
              s => XmlFppWriter.FppBuilder.translateValue(s, typeName)
            )
            val defaultNote = (xmlDefaultOpt, defaultOpt) match {
              case (Some(xmlDefault), None) => 
                val s = s"could not translate default value $xmlDefault"
                List(XmlFppWriter.constructNote(s))
              case _ => Nil
            }
            val setOpcode = translateIntegerOpt(xmlNode, "set_opcode")
            val saveOpcode = translateIntegerOpt(xmlNode, "save_opcode")
            val paramMemberNode = {
              val param = Ast.SpecParam(
                name,
                AstNode.create(typeName),
                defaultOpt,
                id,
                setOpcode,
                saveOpcode
              )
              val node = AstNode.create(param)
              val memberNode = Ast.ComponentMember.SpecParam(node)
              (defaultNote ++ comment, memberNode, Nil)
            }
            enumAnnotatedOpt match {
              case Some(enumAnnotated) => 
                val enumMemberNode = constructEnumMember(enumAnnotated)
                List(enumMemberNode, paramMemberNode)
              case None => List(paramMemberNode)
            }
          }
        }

      }

      case object TlmChannel extends MemberGenerator {

        val xmlName = "channel"

        override def generateMemberNodes(file: XmlFppWriter.File, xmlNode: scala.xml.Node) = {
          type Limits = List[Ast.SpecTlmChannel.Limit]
          def translateLimits(
            direction: String,
            typeName: Ast.TypeName,
            channel: String
          ): Result.Result[Limits] = {
            import Ast.SpecTlmChannel._
            val pairs = List(
              (Red, "red"),
              (Orange, "orange"),
              (Yellow, "yellow")
            ).map(pair => {
              val (kind, name) = pair
              val xmlName = s"${direction}_$name"
              XmlFppWriter.getAttributeOpt(xmlNode, xmlName).map((kind, _))
            }).filter(_.isDefined).map(_.get)
            Result.foldLeft (pairs) (Nil: Limits) ((result, pair) => {
              val (kind, xmlValue) = pair
              XmlFppWriter.FppBuilder.translateValue(xmlValue, typeName) match {
                case Some(exprNode) =>
                  val kindNode = AstNode.create(kind)
                  Right(result :+ (kindNode, exprNode))
                case None =>
                  Left(file.semanticError(s"non-numeric type in limit for channel $channel"))
              }
            })
          }
          for {
            enumAnnotatedOpt <- XmlFppWriter.FppBuilder.InlineEnumBuilder.defEnumAnnotatedOpt(file)(xmlNode)
            name <- file.getAttribute(xmlNode, "name")
            comment <- file.getComment(xmlNode)
            typeName <- translateType(file)(xmlNode)
            update <- XmlFppWriter.getAttributeOpt(xmlNode, "update") match {
              case None => Right(None)
              case Some("always") => Right(Some(Ast.SpecTlmChannel.Always))
              case Some("on_change") => Right(Some(Ast.SpecTlmChannel.OnChange))
              case Some(xmlUpdate) => Left(file.semanticError(s"invalid update specifier $xmlUpdate"))
            }
            lowLimits <- translateLimits("low", typeName, name)
            highLimits <- translateLimits("high", typeName, name)
          }
          yield {
            val id = translateIntegerOpt(xmlNode, "id")
            val xmlFormat = XmlFppWriter.getAttributeOpt(xmlNode, "format_string")
            val (format, formatNote) = 
              XmlFppWriter.FppBuilder.translateFormatOpt(xmlFormat)
            val channel = Ast.SpecTlmChannel(
              name,
              AstNode.create(typeName),
              id,
              update,
              format.map(AstNode.create(_)),
              lowLimits,
              highLimits
            )
            val node = AstNode.create(channel)
            val memberNode = Ast.ComponentMember.SpecTlmChannel(node)
            val tlmChannelMemberNode = (formatNote ++ comment, memberNode, Nil)
            enumAnnotatedOpt match {
              case Some(enumAnnotated) => 
                val enumMemberNode = constructEnumMember(enumAnnotated)
                List(enumMemberNode, tlmChannelMemberNode)
              case None => List(tlmChannelMemberNode)
            }
          }
        }

      }

    }

    /** Member list result */
    type MemListRes = Result.Result[List[Ast.ComponentMember]]

    /** Member list list result */
    type MemListListRes = Result.Result[List[List[Ast.ComponentMember]]]

    /** Maps a node generator onto children at the top level */
    def mapChildren(file: XmlFppWriter.File, memberGenerator: MemberGenerator): MemListRes =
      mapChildrenOfNodeOpt(file, Some(file.elem), memberGenerator)

    /** Maps a node generator onto the children of an XML node */
    def mapChildrenOfNodeOpt(
      file: XmlFppWriter.File,
      nodeOpt: Option[scala.xml.Node],
      memberGenerator: MemberGenerator
    ): MemListRes =
      for {
        list <- nodeOpt.fold(Right(Nil): MemListListRes)(node => {
          val children = node \ memberGenerator.xmlName
          Result.map(children.toList, memberGenerator.generateMembers(file, _))
        })
      } yield list.flatten

    /** Extracts component members */
    def componentMemberList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.ComponentMember]] = {
        def mapChildrenOfName(args: (String, MemberGenerator)): MemListRes = {
          val (name, memberGenerator) = args
          for {
            nodeOpt <- file.getSingleChildOpt(file.elem, name)
            result <- mapChildrenOfNodeOpt(file, nodeOpt, memberGenerator)
          } yield result
        }
        for {
          includes <- mapChildren(file, MemberGenerator.Include)
          lists <- Result.map(
            List(
              ("ports", MemberGenerator.Port),
              ("internal_interfaces", MemberGenerator.InternalPort),
              ("commands", MemberGenerator.Command),
              ("events", MemberGenerator.Event),
              ("parameters", MemberGenerator.Param),
              ("telemetry", MemberGenerator.TlmChannel)
            ),
            mapChildrenOfName _
          )
        }
        yield (includes :: lists).flatten
      }

    /** Translates a component kind */
    def translateKind(file: XmlFppWriter.File, xmlKind: String):
      Result.Result[Ast.ComponentKind] =
      xmlKind match {
        case "active" => Right(Ast.ComponentKind.Active)
        case "passive" => Right(Ast.ComponentKind.Passive)
        case "queued" => Right(Ast.ComponentKind.Queued)
        case _ => Left(file.semanticError(s"invalid component kind $xmlKind"))
      }

    /** Translates the component */
    def defComponentAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefComponent]] =
      for {
        comment <- file.getComment(file.elem)
        xmlKind <- file.getAttribute(file.elem, "kind")
        kind <- translateKind(file, xmlKind)
        name <- file.getAttribute(file.elem, "name")
        members <- componentMemberList(file)
      }
      yield (comment, Ast.DefComponent(kind, name, members), Nil)

  }

}
