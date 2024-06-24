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
      c <- a.component.get.complete
    }
    yield {
      val symbol = Symbol.Component(aNode)
      a.copy(componentMap = a.componentMap + (symbol -> c))
    }
  }

  override def specCommandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ) = {
    val data = aNode._2.data
    for {
      opcodeOpt <- a.getNonnegativeBigIntValueOpt(data.opcode)
      command <- Command.fromSpecCommand(a, aNode)
      component <- a.component.get.addCommand(opcodeOpt, command)
    }
    yield a.copy(component = Some(component))
  }

  override def specContainerAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecContainer]]
  ) = {
    val data = aNode._2.data
    for {
      idOpt <- a.getNonnegativeBigIntValueOpt(data.id)
      container <- Container.fromSpecContainer(a, aNode)
      component <- a.component.get.addContainer(idOpt, container)
    }
    yield a.copy(component = Some(component))
  }

  override def specEventAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ) = {
    val data = aNode._2.data
    for {
      idOpt <- a.getNonnegativeBigIntValueOpt(data.id)
      event <- Event.fromSpecEvent(a, aNode)
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
      instance <- PortInstance.fromSpecInternalPort(a, aNode)
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
      idOpt <- a.getNonnegativeBigIntValueOpt(data.id)
      param_defaultOpcode <- Param.fromSpecParam(a, aNode, component.defaultOpcode.toInt)
      component <- {
        val (param, defaultOpcode) = param_defaultOpcode
        component.copy(defaultOpcode = defaultOpcode).addParam(idOpt, param)
      }
    }
    yield a.copy(component = Some(component))
  }

  override def specPortInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    for {
      instance <- PortInstance.fromSpecPortInstance(a, aNode)
      component <- a.component.get.addPortInstance(instance)
    }
    yield a.copy(component = Some(component))
  }

  override def specPortMatchingAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortMatching]]
  ) = {
    val component = a.component.get
    val list = aNode :: component.specPortMatchingList
    val component1 = component.copy(specPortMatchingList = list)
    Right(a.copy(component = Some(component1)))
  }

  override def specRecordAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecRecord]]
  ) = {
    val data = aNode._2.data
    val record = Record.fromSpecRecord(a, aNode)
    for {
      idOpt <- a.getNonnegativeBigIntValueOpt(data.id)
      component <- a.component.get.addRecord(idOpt, record)
    }
    yield a.copy(component = Some(component))
  }

  override def specStateMachineInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ): Result.Result[Analysis] = {
    for {
      stateMachineInstance <- StateMachineInstance.fromSpecStateMachine(a, aNode)
      component <- a.component.get.addStateMachineInstance(stateMachineInstance)
    } yield a.copy(component = Some(component))
  }


  override def specTlmChannelAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ) = {
    val data = aNode._2.data
    for {
      idOpt <- a.getNonnegativeBigIntValueOpt(data.id)
      tlmChannel <- TlmChannel.fromSpecTlmChannel(a, aNode)
      component <- a.component.get.addTlmChannel(idOpt, tlmChannel)
    }
    yield a.copy(component = Some(component))
  }

}
