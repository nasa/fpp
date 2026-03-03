constant BUFFER_SIZE = 512
constant FW_ASSERT_COUNT_MAX = 10
constant FW_CMD_ARG_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_CMD_STRING_MAX_SIZE = STRING_SIZE
constant FW_COM_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_CONTEXT_DONT_CARE = 0xFF
constant FW_FILE_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_FIXED_LENGTH_STRING_SIZE = STRING_SIZE
constant FW_INTERNAL_INTERFACE_STRING_MAX_SIZE = STRING_SIZE
constant FW_LOG_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_LOG_STRING_MAX_SIZE = STRING_SIZE
constant FW_LOG_TEXT_BUFFER_SIZE = BUFFER_SIZE
constant FW_OBJ_SIMPLE_REG_BUFF_SIZE = BUFFER_SIZE
constant FW_OBJ_SIMPLE_REG_ENTRIES = 500
constant FW_PARAM_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_PARAM_STRING_MAX_SIZE = STRING_SIZE
constant FW_QUEUE_NAME_BUFFER_SIZE = BUFFER_SIZE
constant FW_QUEUE_SIMPLE_QUEUE_ENTRIES = 100
constant FW_SERIALIZE_FALSE_VALUE = 0x00
constant FW_SERIALIZE_TRUE_VALUE = 0xFF
constant FW_SM_SIGNAL_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_STATEMENT_ARG_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_TASK_NAME_BUFFER_SIZE = BUFFER_SIZE
constant FW_TLM_BUFFER_MAX_SIZE = BUFFER_SIZE
constant FW_TLM_STRING_MAX_SIZE = STRING_SIZE
constant STRING_SIZE = 256

type BaseIdType = U32
type FwChanIdType = BaseIdType
type FwDpIdType = BaseIdType
type FwDpPriorityType = BaseIdType
type FwEventIdType = BaseIdType
type FwOpcodeType = BaseIdType
type FwPacketDescriptorType = BaseIdType
type FwSizeStoreType = U16
type FwSizeType = BaseIdType
type FwTimeBaseStoreType = U16
type FwTimeContextStoreType = U8
type FwTlmPacketizeIdType = U16

module Fw {
    enum DpState: U8 {
        UNTRANSMITTED
    }

    constant DpCfg = {
        # NOTE: The constant Fw.DpCfg.CONTAINER_USER_DATA_SIZE is a member of a
        # constant symbol, not a constant symbol. So we are testing this
        # error case. However, because Fw.DpCfg.ProcType is also a required
        # framework definition, the error that we get is a duplicate definition
        # (constant DpCfg vs. module DpCfg), not an invalid member. Given the
        # current rules for framework types, there seems to be no way to force
        # the "invalid member" error to occur; so this is the best we can do.
        CONTAINER_USER_DATA_SIZE = 1
    }

    module DpCfg {
        enum ProcType: U8 {
            PROC_TYPE_ZERO = 0x01
        }
    }
}

topology T {}
