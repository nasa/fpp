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
      for {
        members <- FppBuilder.mapChildrenOfNodeOpt(
          file,
          Some(file.elem),
          nodeGenerator
        )
      }
      yield Line.blankSeparated (FppWriter.componentMember) (members)

  /** Writes a commands file */
  val writeCommandsFile = writeImportedFile(FppBuilder.NodeGenerator.command) _

  /** Writes an events file */
  val writeEventsFile = writeImportedFile(FppBuilder.NodeGenerator.event) _

  /** Writes a ports file */
  val writePortsFile = writeImportedFile(FppBuilder.NodeGenerator.port) _

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

    case class NodeGenerator(childName: String, f: NodeGenerator.Fn)

    case object NodeGenerator {
      type Fn = (XmlFppWriter.File, scala.xml.Node) => Result.Result[Ast.Annotated[Ast.ComponentMember.Node]]
      val include = NodeGenerator("import_dictionary", specInclude _)
      val port = NodeGenerator("port", specPortInstance _)
      val internalPort = NodeGenerator("internal_interface", specInternalPort _)
      val command = NodeGenerator("command", specCommand _)
      val event = NodeGenerator("event", specEvent _)
      val parameter = NodeGenerator("parameter", specParam _)
      val tlmChannel = NodeGenerator("channel", specTlmChannel _)
    }

    /** Member list result */
    type MemListRes = Result.Result[List[Ast.ComponentMember]]

    /** Maps a node generator function onto the children of an XML node */
    def mapChildrenOfNodeOpt(
      file: XmlFppWriter.File,
      nodeOpt: Option[scala.xml.Node],
      nodeGen: NodeGenerator
    ): MemListRes = {
      def createMember(f: NodeGenerator.Fn)(node: scala.xml.Node) = f(file, node) match {
        case Left(error) => Left(error)
        case Right(aNode) => Right(Ast.ComponentMember(aNode))
      }
      nodeOpt.fold(Right(Nil): MemListRes)(node => {
        val children = node \ nodeGen.childName
        Result.map(children.toList, createMember(nodeGen.f))
      })
    }

    /** Extracts component members */
    def componentMemberList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.ComponentMember]] = {
        def mapChildrenOfName(
          args: (String, NodeGenerator)
        ): MemListRes = {
          val (name, nodeGenerator) = args
          for {
            nodeOpt <- file.getSingleChildOpt(file.elem, name)
            result <- mapChildrenOfNodeOpt(file, nodeOpt, nodeGenerator)
          } yield result
        }
        for {
          includes <- mapChildrenOfNodeOpt(
            file,
            Some(file.elem),
            NodeGenerator.include
          )
          lists <- Result.map(
            List(
              ("ports", NodeGenerator.port),
              ("internal_interfaces", NodeGenerator.internalPort),
              ("commands", NodeGenerator.command),
              ("events", NodeGenerator.event),
              ("parameters", NodeGenerator.parameter),
              ("telemetry", NodeGenerator.tlmChannel)
            ),
            mapChildrenOfName(_)
          )
        }
        yield (includes :: lists).flatten
      }

    def specCommand(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specCommand not implemented")))
      }

    def specEvent(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specEvent not implemented")))
      }

    def specInclude(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specInclude not implemented")))
      }

    def specInternalPort(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specInternalPort not implemented")))
      }

    def specParam(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specParam not implemented")))
      }

    def specPortInstance(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specPortInstance not implemented")))
      }

    def specTlmChannel(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specTlmChannel not implemented")))
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
