package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML component as FPP source */
object ComponentXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

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

    /** Extracts component members */
    def componentMemberList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.ComponentMember]] = {
        def mapChildren(
          args: (
            String,
            String,
            scala.xml.Node => Result.Result[Ast.Annotated[Ast.ComponentMember.Node]]
          )
        ): Result.Result[List[Ast.ComponentMember]] = {
          val (parentName, childName, f) = args
          def f1(node: scala.xml.Node) = f(node) match {
            case Left(error) => Left(error)
            case Right(aNode) => Right(Ast.ComponentMember(aNode))
          }
          for {
            parentOpt <- file.getSingleChildOpt(file.elem, parentName)
            result <- parentOpt.fold(Right(Nil): Result.Result[List[Ast.ComponentMember]])(parent => {
              val children = parent \ childName
              Result.map(children.toList, f1)
            })
          } yield result
        }
        for {
          list <- Result.map(
            List(
              ("ports", "port", specPortInstance(file, _)),
              ("internal_interfaces", "internal_interface", specInternalPort(file, _)),
              ("commands", "command", specCommand(file, _)),
              ("events", "event", specEvent(file, _)),
              ("parameters", "parameter", specParam(file, _)),
              ("telemetry", "channel", specTlmChannel(file, _)),
            ),
            mapChildren(_)
          )
        }
        yield list.flatten
      }

    def specCommand(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.SpecCommand]] = {
        Left(file.error(XmlError.SemanticError(_, "specCommand not implemented")))
      }

    def specEvent(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.SpecEvent]] = {
        Left(file.error(XmlError.SemanticError(_, "specEvent not implemented")))
      }

    def specInternalPort(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.SpecInternalPort]] = {
        Left(file.error(XmlError.SemanticError(_, "specInternalPort not implemented")))
      }

    def specParam(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.SpecParam]] = {
        Left(file.error(XmlError.SemanticError(_, "specParam not implemented")))
      }

    def specPortInstance(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.Node]] = {
        Left(file.error(XmlError.SemanticError(_, "specPortInstance not implemented")))
      }

    def specTlmChannel(file: XmlFppWriter.File, node: scala.xml.Node):
      Result.Result[Ast.Annotated[Ast.ComponentMember.SpecTlmChannel]] = {
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
