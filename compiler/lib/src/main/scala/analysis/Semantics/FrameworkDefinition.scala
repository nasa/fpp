package fpp.compiler.analysis

import fpp.compiler.ast._

case class FrameworkConstants(
  containerDataSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,    
  fwObjSimpleRegBuffSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwQueueNameBufferSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwTaskNameBufferSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwComBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwSmSignalBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwCmdArgBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwCmdStringMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwLogBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwLogStringMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwTlmBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwStatementArgBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwTlmStringMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwParamBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwParamStringMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwFileBufferMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwInternalInterfaceStringMaxSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwLogTextBufferSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwFixedLengthStringSize: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwObjSimpleRegEntries: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwQueueSimpleQueueEntries: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwAssertCountMax: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwContextDontCare: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwSerializeTrueValue: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None,
  fwSerializeFalseValue: Option[Ast.Annotated[AstNode[Ast.DefConstant]]] = None
)

case class FrameworkTypes(
  procType: Option[Ast.Annotated[AstNode[Ast.DefEnum]]] = None,
  dpState: Option[Ast.Annotated[AstNode[Ast.DefEnum]]] = None,
  fwAssertArgType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwChanIdType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwDpIdType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwDpPriorityType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwEnumStoreType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwEventIdType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwIndexType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwOpcodeType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwPacketDescriptorType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwPrmIdType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwQueuePriorityType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwSignedSizeType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwSizeStoreType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwSizeType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwTaskPriorityType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwTimeBaseStoreType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwTimeContextStoreType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwTlmPacketizeIdType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None,
  fwTraceIdType: Option[Ast.Annotated[AstNode[Ast.DefAliasType]]] = None
)


case class FrameworkDefinitions(
  constants: FrameworkConstants = FrameworkConstants(),
  types: FrameworkTypes = FrameworkTypes()
)
