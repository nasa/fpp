package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML component as FPP source */
object ComponentXmlFppWriter extends LineUtils {

  /** Writes a component file */
  def writeComponentFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMemberList <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMemberList)

  /** Writes an imported file */
  private def writeImportedFile
    (memberGenerator: FppBuilder.MemberGenerator)
    (file: XmlFppWriter.File): XmlFppWriter.Result =
      for (members <- FppBuilder.mapChildren(file, memberGenerator))
        yield Line.blankSeparated (FppWriter.componentMember) (members)

  /** Writes a commands file */
  val writeCommandsFile = writeImportedFile(FppBuilder.MemberGenerator.Command) _

  /** Writes a params file */
  val writeParamsFile = writeImportedFile(FppBuilder.MemberGenerator.Param) _

  /** Writes a ports file */
  val writePortsFile = writeImportedFile(FppBuilder.MemberGenerator.Port) _

  /** Writes a tlm channels file */
  val writeTlmChannelsFile = writeImportedFile(FppBuilder.MemberGenerator.TlmChannel) _

  /** Writes an events file */
  val writeEventsFile = writeImportedFile(FppBuilder.MemberGenerator.Event) _

  /** Writes an internal ports file */
  val writeInternalPortsFile = writeImportedFile(FppBuilder.MemberGenerator.InternalPort) _

  /** Builds FPP for translating Component XML */
  private object FppBuilder {

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        component <- defComponentAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        Nil,
        Nil,
        component,
        Ast.TUMember.DefComponent(_),
        Ast.ModuleMember.DefComponent(_),
        file
      )

    trait MemberGenerator {

      val xmlName: String

      def generateMemberNode(file: XmlFppWriter.File, node: scala.xml.Node):
        Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] =
          Left(file.semanticError(s"$xmlName not implemented"))

      def generateMemberNodes(file: XmlFppWriter.File, node: scala.xml.Node):
        Result.Result[List[Ast.Annotated[Ast.ComponentMember.Node]]] =
          for (node <- generateMemberNode(file, node))
            yield List(node)

      final def generateMembers(file: XmlFppWriter.File, node: scala.xml.Node) = 
        generateMemberNodes(file, node) match {
          case Left(error) => Left(error)
          case Right(aNodes) => Right(aNodes.map(Ast.ComponentMember(_)))
        }

    }

    def translateQueueFullOpt(file: XmlFppWriter.File, xmlQueueFullOpt: Option[String]):
      Result.Result[Option[Ast.QueueFull]] =
        xmlQueueFullOpt match {
          case Some("assert") => Right(Some(Ast.QueueFull.Assert))
          case Some("block") => Right(Some(Ast.QueueFull.Block))
          case Some("drop") => Right(Some(Ast.QueueFull.Drop))
          case Some(xmlQueueFull) => Left(file.semanticError(s"invalid queue full behavior $xmlQueueFull"))
          case None => Right(None)
        }

    case object MemberGenerator {

      case object Include extends MemberGenerator {
        
        val xmlName = "import_dictionary"

        override def generateMemberNode(file: XmlFppWriter.File, xmlNode: scala.xml.Node) =
          for {
            child <- file.getUniqueChild(xmlNode)
          }
          yield {
            val path = child.toString
            val fileName = path.split("/").toList.reverse.head
            val fileNameNode = AstNode.create(fileName)
            val specIncludeNode = AstNode.create(Ast.SpecInclude(fileNameNode))
            val memberNode = Ast.ComponentMember.SpecInclude(specIncludeNode)
            val annotation = if (fileName == path) Nil else {
              val s = s"original path was $path"
              List(XmlFppWriter.constructNote(s))
            }
            (annotation, memberNode, Nil)
          }

      }

      case object Port extends MemberGenerator {

        val xmlName = "port"

        def general(file: XmlFppWriter.File, xmlNode: scala.xml.Node) = {
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
            queueFull <- {
              val xmlQueueFullOpt = XmlFppWriter.getAttributeOpt(xmlNode, "full")
              translateQueueFullOpt(file, xmlQueueFullOpt)
            }
          }
          yield {
            val size = XmlFppWriter.getAttributeOpt(xmlNode, "max_number").map(
              text => AstNode.create(Ast.ExprLiteralInt(text))
            )
            val port = xmlPort match {
              case "Serial" => None
              case _ => Some(XmlFppWriter.FppBuilder.translateQualIdent(xmlPort))
            }
            val priority = XmlFppWriter.getAttributeOpt(xmlNode, "priority").map(
              text => AstNode.create(Ast.ExprLiteralInt(text))
            )
            General(kind, name, size, port, priority, queueFull.map(AstNode.create(_)))
          }
        }

        def special(file: XmlFppWriter.File, xmlNode: scala.xml.Node, role: String) = {
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
            enums <- FormalParamsXmlFppWriter.defEnumAnnotatedList(file, xmlNode)
            comment <- file.getComment(xmlNode)
            name <- file.getAttribute(xmlNode, "name")
            params <- FormalParamsXmlFppWriter.formalParamList(file, xmlNode)
            queueFull <- {
              val xmlQueueFullOpt = XmlFppWriter.getAttributeOpt(xmlNode, "full")
              translateQueueFullOpt(file, xmlQueueFullOpt)
            }
          }
          yield {
            val annotatedEnumMemberNodes = enums.map(aNode => (
              aNode._1,
              Ast.ComponentMember.DefEnum(AstNode.create(aNode._2)),
              aNode._3
            ))
            val annotatedPortMemberNodes = {
              val priority = XmlFppWriter.getAttributeOpt(xmlNode, "priority").map(
                text => AstNode.create(Ast.ExprLiteralInt(text))
              )
              val internalPort = Ast.SpecInternalPort(name, params, priority, queueFull)
              val node = AstNode.create(internalPort)
              val memberNode = Ast.ComponentMember.SpecInternalPort(node)
              List((comment, memberNode, Nil))
            }
            annotatedEnumMemberNodes ++ annotatedPortMemberNodes
          }

      }

      case object Command extends MemberGenerator {

        val xmlName = "command"

        // TODO: generate

      }

      case object Event extends MemberGenerator {

        val xmlName = "event"

        // TODO: generate

      }

      case object Param extends MemberGenerator {

        val xmlName = "paramter"

        // TODO: generate

      }

      case object TlmChannel extends MemberGenerator {

        val xmlName = "channel"

        // TODO: generate

      }

    }

    /** Member list result */
    type MemListRes = Result.Result[List[Ast.ComponentMember]]

    /** Member list list result */
    type MemListListRes = Result.Result[List[List[Ast.ComponentMember]]]

    /** Maps a node generator onto children at the top level */
    def mapChildren(file: XmlFppWriter.File, memberGenerator: MemberGenerator) =
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
            mapChildrenOfName(_)
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
