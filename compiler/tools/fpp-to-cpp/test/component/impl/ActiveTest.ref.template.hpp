// ======================================================================
// \title  ActiveTest.template.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for ActiveTest component implementation class
// ======================================================================

#ifndef ActiveTest_HPP
#define ActiveTest_HPP

#include "ActiveTestComponentAc.hpp"

class ActiveTest :
  public ActiveTestComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction, initialization, and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveTest object
    ActiveTest(
        const char* const compName //!< The component name
    );

    //! Initialize ActiveTest object
    void init(
        NATIVE_INT_TYPE queueDepth, //!< The queue depth
        NATIVE_INT_TYPE instance = 0 //!< The instance number
    );

    //! Destroy ActiveTest object
    ~ActiveTest();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for user-defined typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for noArgsAsync
    void noArgsAsync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsGuarded
    void noArgsGuarded_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsReturnGuarded
    U32 noArgsReturnGuarded_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsReturnSync
    U32 noArgsReturnSync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsSync
    void noArgsSync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for typedAsync
    void typedAsync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedAsyncAssert
    void typedAsyncAssert_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedAsyncBlockPriority
    void typedAsyncBlockPriority_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedAsyncDropPriority
    void typedAsyncDropPriority_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedGuarded
    void typedGuarded_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedReturnGuarded
    F32 typedReturnGuarded_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedReturnPortStrings::StringSize80& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedReturnSync
    F32 typedReturnSync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedReturnPortStrings::StringSize80& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedSync
    void typedSync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for commands
    // ----------------------------------------------------------------------

    //! Handler implementation for command CMD_SYNC
    void CMD_SYNC_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    );

    //! Handler implementation for command CMD_SYNC_PRIMITIVE
    void CMD_SYNC_PRIMITIVE_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b //!< A boolean
    );

    //! Handler implementation for command CMD_SYNC_STRING
    void CMD_SYNC_STRING_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        const Fw::CmdStringArg& str1, //!< A string
        const Fw::CmdStringArg& str2 //!< Another string
    );

    //! Handler implementation for command CMD_SYNC_ENUM
    void CMD_SYNC_ENUM_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        E e //!< An enum
    );

    //! Handler implementation for command CMD_SYNC_ARRAY
    void CMD_SYNC_ARRAY_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        A a //!< An array
    );

    //! Handler implementation for command CMD_SYNC_STRUCT
    void CMD_SYNC_STRUCT_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        S s //!< A struct
    );

    //! Handler implementation for command CMD_GUARDED
    void CMD_GUARDED_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    );

    //! Handler implementation for command CMD_GUARDED_PRIMITIVE
    void CMD_GUARDED_PRIMITIVE_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b //!< A boolean
    );

    //! Handler implementation for command CMD_GUARDED_STRING
    void CMD_GUARDED_STRING_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        const Fw::CmdStringArg& str1, //!< A string
        const Fw::CmdStringArg& str2 //!< Another string
    );

    //! Handler implementation for command CMD_GUARDED_ENUM
    void CMD_GUARDED_ENUM_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        E e //!< An enum
    );

    //! Handler implementation for command CMD_GUARDED_ARRAY
    void CMD_GUARDED_ARRAY_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        A a //!< An array
    );

    //! Handler implementation for command CMD_GUARDED_STRUCT
    void CMD_GUARDED_STRUCT_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        S s //!< A struct
    );

    //! Handler implementation for command CMD_ASYNC
    void CMD_ASYNC_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    );

    //! Handler implementation for command CMD_PRIORITY
    void CMD_PRIORITY_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    );

    //! Handler implementation for command CMD_PARAMS_PRIORITY
    void CMD_PARAMS_PRIORITY_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        U32 u32
    );

    //! Handler implementation for command CMD_DROP
    void CMD_DROP_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    );

    //! Handler implementation for command CMD_PARAMS_PRIORITY_DROP
    void CMD_PARAMS_PRIORITY_DROP_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        U32 u32
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for user-defined internal interfaces
    // ----------------------------------------------------------------------

    //! Handler implementation for internalArray
    void internalArray_internalInterfaceHandler(
        const A& a //!< An array
    );

    //! Handler implementation for internalEnum
    void internalEnum_internalInterfaceHandler(
        const E& e //!< An enum
    );

    //! Handler implementation for internalPrimitive
    void internalPrimitive_internalInterfaceHandler(
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b //!< A boolean
    );

    //! Handler implementation for internalPriorityDrop
    void internalPriorityDrop_internalInterfaceHandler();

    //! Handler implementation for internalString
    void internalString_internalInterfaceHandler(
        const Fw::InternalInterfaceString& str1, //!< A string
        const Fw::InternalInterfaceString& str2 //!< Another string
    );

    //! Handler implementation for internalStruct
    void internalStruct_internalInterfaceHandler(
        const S& s //!< A struct
    );

};

#endif
