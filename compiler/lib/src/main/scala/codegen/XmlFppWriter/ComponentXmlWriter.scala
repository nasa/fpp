package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML component as FPP source */
object ComponentXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  /** Builds FPP for translating Serializable XML */
  private object FppBuilder {

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        enums <- Right(Nil)
        component <- defComponentAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        Nil,
        enums,
        component,
        Ast.TUMember.DefComponent(_),
        Ast.ModuleMember.DefComponent(_),
        file
      )

    /** Extracts component members */
    def componentMemberList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.ComponentMember]] =
      Right(Nil)

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
