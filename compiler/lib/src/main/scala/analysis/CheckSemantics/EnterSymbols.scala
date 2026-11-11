package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter symbols into their scopes */
object EnterSymbols
  extends EnterModuleSymbols
  with EnumAnalyzer
  with InterfaceAnalyzer
  with ComponentAnalyzer
  with StateMachineAnalyzer
  with TopologyAnalyzer
{

  override def defAbsTypeAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.AbsType(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defAliasTypeAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.AliasType(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defArrayAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Array(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defComponentAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val parentSymbol = a.parentSymbol
    val symbol = Symbol.Component(aNode)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.Component)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.StateMachine)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val a1 = a.copy(nestedScope = nestedScope1, parentSymbol = Some(symbol))
        super.defComponentAnnotatedNode(a1, aNode)
      }
    }
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      val a1 = a.copy(
        nestedScope = a.nestedScope.pop,
        parentSymbol = parentSymbol,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  override def defComponentInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.ComponentInstance(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.PortInterfaceInstance)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defConstantAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Constant(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defEnumAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val parentSymbol = a.parentSymbol
    val symbol = Symbol.Enum(aNode)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val a1 = a.copy(nestedScope = nestedScope1, parentSymbol = Some(symbol))
        super.defEnumAnnotatedNode(a1, aNode)
      }
    }
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      val a1 = a.copy(
        nestedScope = a.nestedScope.pop,
        parentSymbol = parentSymbol,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  override def defEnumConstantAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.EnumConstant(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defInterfaceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefInterface]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Interface(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.PortInterface)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Template(aNode)
    val nestedScope = a.nestedScope

    for (nestedScope <- nestedScope.put(NameGroup.Template)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = Right(a)

  override def defPortAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Port(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Port)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defStateMachineAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = { 
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val parentSymbol = a.parentSymbol
    val symbol = Symbol.StateMachine(aNode)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.StateMachine)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val a1 = a.copy(nestedScope = nestedScope1, parentSymbol = Some(symbol))
        super.defStateMachineAnnotatedNode(a1, aNode)
      }
    }
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      val a1 = a.copy(
        nestedScope = a.nestedScope.pop,
        parentSymbol = parentSymbol,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  override def defStructAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Struct(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defSystemAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.System(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.System)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val name = data.name
    val symbol = Symbol.Topology(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.PortInterfaceInstance)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

}
