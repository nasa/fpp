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

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        instances <- defComponentInstanceAnnotatedList(file)
        top <- defTopologyAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        instances,
        Ast.TUMember.DefComponentInstance.apply,
        Ast.ModuleMember.DefComponentInstance.apply,
        top,
        Ast.TUMember.DefTopology.apply,
        Ast.ModuleMember.DefTopology.apply,
        file,
      )

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
            None,
            None,
            None,
            Nil
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

    type Endpoint = (
      AstNode[Ast.PortInstanceIdentifier],
      Option[AstNode[Ast.Expr]]
    )

    /** Translates a connection endpoint */
    def endpoint(
      file: XmlFppWriter.File,
      node: scala.xml.Node
    ): Result.Result[Endpoint] =
      for {
        xmlInstance <- file.getAttribute(node, "component")
        port <- file.getAttribute(node, "port")
        portNumber <- file.getAttribute(node, "num")
      }
      yield {
        val pii = Ast.PortInstanceIdentifier(
          XmlFppWriter.FppBuilder.translateQualIdent(xmlInstance),
          AstNode.create(port)
        )
        val e = Ast.ExprLiteralInt(portNumber)
        (AstNode.create(pii), Some(AstNode.create(e)))
      }

    /** Translates a connection */
    def connection(
      file: XmlFppWriter.File,
      node: scala.xml.Node
    ): Result.Result[Ast.SpecConnectionGraph.Connection] =
      for {
        xmlSource <- file.getSingleChild(node, "source")
        from <- endpoint(file, xmlSource)
        xmlTarget <- file.getSingleChild(node, "target")
        to <- endpoint(file, xmlTarget)
      }
      yield {
        Ast.SpecConnectionGraph.Connection(
          from._1,
          from._2,
          to._1,
          to._2
        )
      }

    /** Translates the connections */
    def connectionList(file: XmlFppWriter.File):
      Result.Result[List[Ast.SpecConnectionGraph.Connection]] = { 
        val connections = file.elem \ "connection"
        Result.map(connections.toList, connection(file, _))
      }

    /** Translates the connection graph */
    def specConnectionGraphAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.SpecConnectionGraph]] =
        for (connections <- connectionList(file))
          yield (
            Nil,
            Ast.SpecConnectionGraph.Direct("XML", connections),
            Nil
          )

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
          member(Ast.TopologyMember.SpecCompInstance.apply)
        )
        val graphMember = member(Ast.TopologyMember.SpecConnectionGraph.apply)(
          graphAnnotated
        )
        val members = instanceMembers :+ graphMember
        (Nil, Ast.DefTopology(name, members), Nil)
      }

  }

}
