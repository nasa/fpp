type BaseIdType = U32
type FwChanIdType = BaseIdType
type FwDpIdType = BaseIdType
type FwDpPriorityType = BaseIdType
type FwEventIdType = BaseIdType
type FwOpcodeType = BaseIdType
type FwPacketDescriptorType = BaseIdType
type FwTlmPacketizeIdType = U16
type FwSizeType = BaseIdType
type FwSizeStoreType = U16

@ The type used to serialize a time base value
type FwTimeBaseStoreType = U16

@ The type used to serialize a time context value
type FwTimeContextStoreType = U8

module Fw {
    enum DpState: U8 {
        @ The untransmitted state
        UNTRANSMITTED
        @ The partially transmitted state
        @ A data product is in this state from the start of transmission
        @ until transmission is complete.
        PARTIAL
        @ The transmitted state
        TRANSMITTED
    } default UNTRANSMITTED

    module DpCfg {
        @ The container user data size
        constant CONTAINER_USER_DATA_SIZE = 32

        @ A bit mask for selecting the type of processing to perform on
        @ a container before writing it to disk.
        enum ProcType: U8 {
            @ Processing type 0
            PROC_TYPE_ZERO = 0x01
            @ Processing type 1
            PROC_TYPE_ONE = 0x02
            @ Processing type 2
            PROC_TYPE_TWO = 0x04
        }
    }
}

constant FW_OBJ_SIMPLE_REG_BUFF_SIZE = 255
constant FW_QUEUE_NAME_BUFFER_SIZE = 80
constant FW_TASK_NAME_BUFFER_SIZE = 80
constant FW_COM_BUFFER_MAX_SIZE = 512
constant FW_SM_SIGNAL_BUFFER_MAX_SIZE = 128
constant FW_CMD_ARG_BUFFER_MAX_SIZE = FW_COM_BUFFER_MAX_SIZE - 4 - 2
constant FW_CMD_STRING_MAX_SIZE = 40
constant FW_LOG_BUFFER_MAX_SIZE = FW_COM_BUFFER_MAX_SIZE - 4 - 2
constant FW_LOG_STRING_MAX_SIZE = 200
constant FW_TLM_BUFFER_MAX_SIZE = FW_COM_BUFFER_MAX_SIZE - 4 - 2
constant FW_STATEMENT_ARG_BUFFER_MAX_SIZE = FW_CMD_ARG_BUFFER_MAX_SIZE
constant FW_TLM_STRING_MAX_SIZE = 40
constant FW_PARAM_BUFFER_MAX_SIZE = FW_COM_BUFFER_MAX_SIZE - 4 - 2
constant FW_PARAM_STRING_MAX_SIZE = 40
constant FW_FILE_BUFFER_MAX_SIZE = FW_COM_BUFFER_MAX_SIZE
constant FW_INTERNAL_INTERFACE_STRING_MAX_SIZE = 256
constant FW_LOG_TEXT_BUFFER_SIZE = 256
constant FW_FIXED_LENGTH_STRING_SIZE = 256
constant FW_OBJ_SIMPLE_REG_ENTRIES = 500
constant FW_QUEUE_SIMPLE_QUEUE_ENTRIES = 100
constant FW_ASSERT_COUNT_MAX = 10
constant FW_CONTEXT_DONT_CARE = 0xFF
dictionary constant FW_SERIALIZE_TRUE_VALUE = 0xFF
dictionary constant FW_SERIALIZE_FALSE_VALUE = 0x00
