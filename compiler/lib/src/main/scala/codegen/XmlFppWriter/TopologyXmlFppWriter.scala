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

    /** Translates a component instance specifier */
    def specCompInstanceAnnotated(
      file: XmlFppWriter.File,
      node: scala.xml.Node
    ): Result.Result[Ast.Annotated[Ast.SpecCompInstance]] =
      for (name <- file.getAttribute(node, "name"))
        yield {
          val qid = XmlFppWriter.FppBuilder.translateQualIdent(name)
          (
            Nil,
            Ast.SpecCompInstance(Ast.Visibility.Public, qid),
            Nil
          )
        }

    /** Translates the component instance definitions */
    def defComponentInstanceAnnotatedList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.DefComponentInstance]]] = { 
        val instances = file.elem \ "instance"
        Result.map(instances.toList, defComponentInstanceAnnotated(file, _))
      }

    /** Translates the component instance specifiers */
    def specCompInstanceAnnotatedList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.SpecCompInstance]]] = {
        val instances = file.elem \ "instance"
        Result.map(instances.toList, specCompInstanceAnnotated(file, _))
      }

    /** Translates the connection graph */
    def specConnectionGraphAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.SpecConnectionGraph]] =
        // TODO
        Right((Nil, Ast.SpecConnectionGraph.Direct("XML", Nil), Nil))

    /** Translates the topology */
    def defTopologyAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefTopology]] = 
      for {
        name <- file.getAttribute(file.elem, "name")
        instancesAnnotated <- specCompInstanceAnnotatedList(file)
        graphAnnotated <- specConnectionGraphAnnotated(file)
      }
      yield {
        def member[T]
        (memberNodeConstructor: AstNode[T] => Ast.TopologyMember.Node)
        (ta: Ast.Annotated[T]) = {
          val (a1, t, a2) = ta
          val node = AstNode.create(t)
          val memberNode = memberNodeConstructor(node)
          Ast.TopologyMember(a1, memberNode, a2)
        }
        val instanceMembers = instancesAnnotated.map(
          member(Ast.TopologyMember.SpecCompInstance)
        )
        val graphMember = member(Ast.TopologyMember.SpecConnectionGraph)(
          graphAnnotated
        )
        val members = instanceMembers :+ graphMember
        (Nil, Ast.DefTopology(name, members), Nil)
      }

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        instances <- defComponentInstanceAnnotatedList(file)
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
