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
    val data = aNode._2.data
    for {
      opcodeOpt <- a.getIntValueOpt(data.opcode)
      command <- Commands.fromSpecCommand(a, aNode)
      component <- a.component.get.addCommand(opcodeOpt, command)
    }
    yield a.copy(component = Some(component))
  }

  override def specEventAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ) = {
    val data = aNode._2.data
    for {
      idOpt <- a.getIntValueOpt(data.id)
      event <- Events.fromSpecEvent(a, aNode)
      component <- a.component.get.addEvent(idOpt, event)
    }
    yield a.copy(component = Some(component))
  }

  override def specInternalPortAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]
  ) = {
    val data = aNode._2.data
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
    val data = aNode._2.data
    val component = a.component.get
    for {
      idOpt <- a.getIntValueOpt(data.id)
      param_defaultOpcode <- Params.fromSpecParam(a, aNode, component.defaultOpcode)
      component <- {
        val (param, defaultOpcode) = param_defaultOpcode
        val paramType = a.typeMap(data.typeName.id)
        component.addParam(idOpt, param, paramType, defaultOpcode)
      }
    }
    yield a.copy(component = Some(component))
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
    val data = aNode._2.data
    for {
      idOpt <- a.getIntValueOpt(data.id)
      tlmChannel <- TlmChannels.fromSpecTlmChannel(a, aNode)
      component <- a.component.get.addTlmChannel(idOpt, tlmChannel)
    }
    yield a.copy(component = Some(component))
  }

}
