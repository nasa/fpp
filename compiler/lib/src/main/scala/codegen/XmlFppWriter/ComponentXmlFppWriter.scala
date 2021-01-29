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
    (nodeGenerator: FppBuilder.NodeGenerator)
    (file: XmlFppWriter.File): XmlFppWriter.Result =
      for (members <- FppBuilder.mapChildren(file, nodeGenerator))
        yield Line.blankSeparated (FppWriter.componentMember) (members)

  /** Writes a commands file */
  val writeCommandsFile = writeImportedFile(FppBuilder.NodeGenerator.Command) _

  /** Writes a params file */
  val writeParamsFile = writeImportedFile(FppBuilder.NodeGenerator.Param) _

  /** Writes a ports file */
  val writePortsFile = writeImportedFile(FppBuilder.NodeGenerator.Port) _

  /** Writes a tlm channels file */
  val writeTlmChannelsFile = writeImportedFile(FppBuilder.NodeGenerator.TlmChannel) _

  /** Writes an events file */
  val writeEventsFile = writeImportedFile(FppBuilder.NodeGenerator.Event) _

  /** Writes an internal ports file */
  val writeInternalPortsFile = writeImportedFile(FppBuilder.NodeGenerator.InternalPort) _

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

    trait NodeGenerator {

      val xmlName: String

      def generate(file: XmlFppWriter.File, node: scala.xml.Node):
        Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] =
          Left(file.error(XmlError.SemanticError(_, s"$xmlName not implemented")))

      final def createMember(file: XmlFppWriter.File, node: scala.xml.Node) = 
        generate(file, node) match {
          case Left(error) => Left(error)
          case Right(aNode) => Right(Ast.ComponentMember(aNode))
        }

    }

    def translateQueueFullOpt(file: XmlFppWriter.File, xmlQueueFullOpt: Option[String]):
      Result.Result[Option[AstNode[Ast.QueueFull]]] = for {
        queueFullOpt <- xmlQueueFullOpt match {
          case Some("assert") => Right(Some(Ast.QueueFull.Assert))
          case Some("block") => Right(Some(Ast.QueueFull.Block))
          case Some("drop") => Right(Some(Ast.QueueFull.Drop))
          case Some(xmlQueueFull) => Left(file.semanticError(s"invalid queue full behavior $xmlQueueFull"))
          case None => Right(None)
        }
      } yield queueFullOpt.map(AstNode.create(_))

    case object NodeGenerator {

      case object Include extends NodeGenerator {
        
        val xmlName = "import_dictionary"

        override def generate(file: XmlFppWriter.File, xmlNode: scala.xml.Node) =
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

      case object Port extends NodeGenerator {

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
            General(kind, name, size, port, priority, queueFull)
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

        override def generate(file: XmlFppWriter.File, xmlNode: scala.xml.Node) = {
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

      case object InternalPort extends NodeGenerator {

        val xmlName = "internal_interface"

        // TODO: generate

      }

      case object Command extends NodeGenerator {

        val xmlName = "command"

        // TODO: generate

      }

      case object Event extends NodeGenerator {

        val xmlName = "event"

        // TODO: generate

      }

      case object Param extends NodeGenerator {

        val xmlName = "paramter"

        // TODO: generate

      }

      case object TlmChannel extends NodeGenerator {

        val xmlName = "channel"

        // TODO: generate

      }

    }

    /** Member list result */
    type MemListRes = Result.Result[List[Ast.ComponentMember]]

    /** Maps a node generator onto children at the top level */
    def mapChildren(file: XmlFppWriter.File, nodeGenerator: NodeGenerator) =
      mapChildrenOfNodeOpt(file, Some(file.elem), nodeGenerator)

    /** Maps a node generator onto the children of an XML node */
    def mapChildrenOfNodeOpt(
      file: XmlFppWriter.File,
      nodeOpt: Option[scala.xml.Node],
      nodeGenerator: NodeGenerator
    ): MemListRes =
      nodeOpt.fold(Right(Nil): MemListRes)(node => {
        val children = node \ nodeGenerator.xmlName
        Result.map(children.toList, nodeGenerator.createMember(file, _))
      })

    /** Extracts component members */
    def componentMemberList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.ComponentMember]] = {
        def mapChildrenOfName(args: (String, NodeGenerator)): MemListRes = {
          val (name, nodeGenerator) = args
          for {
            nodeOpt <- file.getSingleChildOpt(file.elem, name)
            result <- mapChildrenOfNodeOpt(file, nodeOpt, nodeGenerator)
          } yield result
        }
        for {
          includes <- mapChildren(file, NodeGenerator.Include)
          lists <- Result.map(
            List(
              ("ports", NodeGenerator.Port),
              ("internal_interfaces", NodeGenerator.InternalPort),
              ("commands", NodeGenerator.Command),
              ("events", NodeGenerator.Event),
              ("parameters", NodeGenerator.Param),
              ("telemetry", NodeGenerator.TlmChannel)
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
        case _ => Left(file.error(
          XmlError.SemanticError(_, s"invalid component kind $xmlKind")
        ))
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
