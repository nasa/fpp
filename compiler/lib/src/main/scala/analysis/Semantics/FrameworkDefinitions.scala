package fpp.compiler.analysis

import fpp.compiler.ast._


case class FrameworkDefinitions(
  constants: FrameworkDefinitions.Constants = FrameworkDefinitions.Constants(),
  types: FrameworkDefinitions.Types = FrameworkDefinitions.Types()
)

case object FrameworkDefinitions {

  case class Constants(
    fwAssertCountMax: Option[Symbol.Constant] = None,
    fwCmdArgBufferMaxSize: Option[Symbol.Constant] = None,
    fwCmdStringMaxSize: Option[Symbol.Constant] = None,
    fwComBufferMaxSize: Option[Symbol.Constant] = None,
    fwContextDontCare: Option[Symbol.Constant] = None,
    fwDpCfgContainerDataSize: Option[Symbol.Constant] = None,
    fwFileBufferMaxSize: Option[Symbol.Constant] = None,
    fwFixedLengthStringSize: Option[Symbol.Constant] = None,
    fwInternalInterfaceStringMaxSize: Option[Symbol.Constant] = None,
    fwLogBufferMaxSize: Option[Symbol.Constant] = None,
    fwLogStringMaxSize: Option[Symbol.Constant] = None,
    fwLogTextBufferSize: Option[Symbol.Constant] = None,
    fwObjSimpleRegBuffSize: Option[Symbol.Constant] = None,
    fwObjSimpleRegEntries: Option[Symbol.Constant] = None,
    fwParamBufferMaxSize: Option[Symbol.Constant] = None,
    fwParamStringMaxSize: Option[Symbol.Constant] = None,
    fwQueueNameBufferSize: Option[Symbol.Constant] = None,
    fwQueueSimpleQueueEntries: Option[Symbol.Constant] = None,
    fwSerializeFalseValue: Option[Symbol.Constant] = None,
    fwSerializeTrueValue: Option[Symbol.Constant] = None,
    fwSmSignalBufferMaxSize: Option[Symbol.Constant] = None,
    fwStatementArgBufferMaxSize: Option[Symbol.Constant] = None,
    fwTaskNameBufferSize: Option[Symbol.Constant] = None,
    fwTlmBufferMaxSize: Option[Symbol.Constant] = None,
    fwTlmStringMaxSize: Option[Symbol.Constant] = None,
  )

  case class Types(
    fwAssertArgType: Option[TypeSymbol] = None,
    fwChanIdType: Option[TypeSymbol] = None,
    fwDpCfgProcType: Option[TypeSymbol] = None,
    fwDpIdType: Option[TypeSymbol] = None,
    fwDpPriorityType: Option[TypeSymbol] = None,
    fwDpState: Option[TypeSymbol] = None,
    fwEnumStoreType: Option[TypeSymbol] = None,
    fwEventIdType: Option[TypeSymbol] = None,
    fwIndexType: Option[TypeSymbol] = None,
    fwOpcodeType: Option[TypeSymbol] = None,
    fwPacketDescriptorType: Option[TypeSymbol] = None,
    fwPrmIdType: Option[TypeSymbol] = None,
    fwQueuePriorityType: Option[TypeSymbol] = None,
    fwSignedSizeType: Option[TypeSymbol] = None,
    fwSizeStoreType: Option[TypeSymbol] = None,
    fwSizeType: Option[TypeSymbol] = None,
    fwTaskPriorityType: Option[TypeSymbol] = None,
    fwTimeBaseStoreType: Option[TypeSymbol] = None,
    fwTimeContextStoreType: Option[TypeSymbol] = None,
    fwTlmPacketizeIdType: Option[TypeSymbol] = None,
    fwTraceIdType: Option[TypeSymbol] = None,
  )

}
