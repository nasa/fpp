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
