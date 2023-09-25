package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Lower a special data product port to a form that the XML autocoder
 *  can understand. */
case class DpPortXmlLowering(
  s: XmlWriterState,
  name: String,
  special: PortInstance.Special
) {

  val aNode = special.aNode

  val specifier = special.specifier

  def lower: Option[PortInstance.General] =
    for (generalSpec <- lowerAstSpec) yield {
      val generalNode = AstNode.create(generalSpec, aNode._2.id)
      val generalANode = (aNode._1, generalNode, aNode._3)
      val generalKind = lowerKind.get
      val size = 1
      val ty = lowerType.get
      PortInstance.General(
        generalANode,
        generalSpec,
        generalKind,
        size,
        ty
      )
    }

  /** Lower the AST kind */
  private def lowerAstKind: Option[Ast.SpecPortInstance.GeneralKind] =
    (specifier.inputKind, specifier.kind) match {
      case (_, Ast.SpecPortInstance.ProductGet) =>
        Some(Ast.SpecPortInstance.Output)
      case (_, Ast.SpecPortInstance.ProductRequest) =>
        Some(Ast.SpecPortInstance.Output)
      case (Some(Ast.SpecPortInstance.Async), Ast.SpecPortInstance.ProductRecv) =>
        Some(Ast.SpecPortInstance.AsyncInput)
      case (Some(Ast.SpecPortInstance.Guarded), Ast.SpecPortInstance.ProductRecv) =>
        Some(Ast.SpecPortInstance.GuardedInput)
      case (Some(Ast.SpecPortInstance.Sync), Ast.SpecPortInstance.ProductRecv) =>
        Some(Ast.SpecPortInstance.SyncInput)
      case (_, Ast.SpecPortInstance.ProductSend) =>
        Some(Ast.SpecPortInstance.Output)
      case _ => None
    }

  /** Lower the AST specifier */
  private def lowerAstSpec: Option[Ast.SpecPortInstance.General] =
    for (kind <- lowerAstKind) yield Ast.SpecPortInstance.General(
      kind,
      name,
      None,
      None,
      specifier.priority,
      specifier.queueFull
    )

  /** Lower the semantic kind */
  private def lowerKind: Option[PortInstance.General.Kind] =
    (specifier.inputKind, specifier.kind) match {
      case (_, Ast.SpecPortInstance.ProductGet) =>
        Some(PortInstance.General.Kind.Output)
      case (_, Ast.SpecPortInstance.ProductRequest) =>
        Some(PortInstance.General.Kind.Output)
      case (Some(Ast.SpecPortInstance.Async), Ast.SpecPortInstance.ProductRecv) =>
        Some(PortInstance.General.Kind.AsyncInput(
          special.priority,
          special.queueFull.get
        ))
      case (Some(Ast.SpecPortInstance.Guarded), Ast.SpecPortInstance.ProductRecv) =>
        Some(PortInstance.General.Kind.GuardedInput)
      case (Some(Ast.SpecPortInstance.Sync), Ast.SpecPortInstance.ProductRecv) =>
        Some(PortInstance.General.Kind.SyncInput)
      case (_, Ast.SpecPortInstance.ProductSend) =>
        Some(PortInstance.General.Kind.Output)
      case _ => None
    }

  /** Lower the port type */
  private def lowerType: Option[PortInstance.Type] =
    s.a.useDefMap.get(aNode._2.id) match {
      case Some(s: Symbol.Port) => Some(PortInstance.Type.DefPort(s))
      case _ => None
    }

}
