package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component definitions */
object CheckComponentDefs
  extends Analyzer 
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  override def defComponentAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val a1 = a.copy(component = Some(Component(aNode)))
    for {
      a <- super.defComponentAnnotatedNode(a1, aNode)
      _ <- Components.checkValidity(a, a.component.get)
    }
    yield {
      val symbol = Symbol.Component(aNode)
      a.copy(componentMap = a.componentMap + (symbol -> a.component.get))
    }
  }

  override def specCommandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ) = {
    // TODO: Create a new command cmd
    // TODO: Add cmd to the command map
    default(a)
  }

  override def specEventAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ) = {
    // TODO: Create a new event e
    // TODO: Add e to the event map
    default(a)
  }

  override def specInternalPortAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]
  ) = {
    for {
      instance <- PortInstances.fromSpecInternalPort(a, aNode)
      component <- a.component.get.addPortInstance(instance)
    }
    yield a.copy(component = Some(component))
  }

  override def specParamAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecParam]]
  ) = {
    // TODO: Create a new parameter p
    // TODO: Add p to the parameter map
    default(a)
  }

  override def specPortInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    for {
      instance <- PortInstances.fromSpecPortInstance(a, aNode)
      component <- a.component.get.addPortInstance(instance)
    }
    yield a.copy(component = Some(component))
  }

  override def specTlmChannelAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ) = {
    // TODO: Create a new telemetry channel ch
    // TODO: Add ch to the telemetry map
    default(a)
  }

}
