package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML topology as FPP source */
object TopologyXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  /** Builds FPP for translating topology XML */
  private object FppBuilder {

    /** Translates a component instance definition */
    def defComponentInstanceAnnotated(
      file: XmlFppWriter.File,
      node: scala.xml.Node
    ): Result.Result[Ast.Annotated[Ast.DefComponentInstance]] =
      for {
        name <- file.getAttribute(node, "name")
        componentName <- file.getAttribute(node, "type")
        baseId <- file.getAttribute(node, "base_id")
      }
      yield {
        val componentQid = Ast.QualIdent.fromNodeList(
          (XmlFppWriter.getAttributeNamespace(node) :+ componentName).
            map(AstNode.create(_))
        )
        (
          Nil,
          Ast.DefComponentInstance(
            name,
            AstNode.create(componentQid),
            AstNode.create(Ast.ExprLiteralInt(baseId)),
            None,
            None,
            None,
            None
          ),
          Nil
        )
      }

    /** Translates the component instances definitions */
    def defComponentInstanceList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.DefComponentInstance]]] = { 
        val instances = file.elem \ "instance"
        Result.map(instances.toList, defComponentInstanceAnnotated(file, _))
      }

    /** Translates the topology */
    def defTopologyAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefTopology]] = 
      for {
        name <- file.getAttribute(file.elem, "name")
        members <- Right(Nil) // TODO
      }
      yield (Nil, Ast.DefTopology(name, members), Nil)

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        instances <- defComponentInstanceList(file)
        top <- defTopologyAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        instances,
        Ast.TUMember.DefComponentInstance,
        Ast.ModuleMember.DefComponentInstance,
        top,
        Ast.TUMember.DefTopology,
        Ast.ModuleMember.DefTopology,
        file,
      )

  }

}
