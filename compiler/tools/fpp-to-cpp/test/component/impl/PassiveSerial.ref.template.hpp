// ======================================================================
// \title  PassiveSerial.template.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for PassiveSerial component implementation class
// ======================================================================

#ifndef PassiveSerial_HPP
#define PassiveSerial_HPP

#include "PassiveSerialComponentAc.hpp"

class PassiveSerial :
  public PassiveSerialComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction, initialization, and destruction
    // ----------------------------------------------------------------------

    //! Construct PassiveSerial object
    PassiveSerial(
        const char* const compName //!< The component name
    );

    //! Initialize PassiveSerial object
    void init(
        NATIVE_INT_TYPE instance = 0 //!< The instance number
    );

    //! Destroy PassiveSerial object
    ~PassiveSerial();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for user-defined typed input ports
    // ----------------------------------------------------------------------

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
    // Handler implementations for user-defined serial input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for serialGuarded
    void serialGuarded_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::SerializeBufferBase& buffer //!< The serialization buffer
    );

    //! Handler implementation for serialSync
    void serialSync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::SerializeBufferBase& buffer //!< The serialization buffer
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

};

#endif
