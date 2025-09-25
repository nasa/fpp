// ======================================================================
// \title  ActiveOverflow.hpp
// \author [user name]
// \brief  hpp file for ActiveOverflow component implementation class
// ======================================================================

#ifndef ActiveOverflow_HPP
#define ActiveOverflow_HPP

#include "ActiveOverflowComponentAc.hpp"

class ActiveOverflow final :
  public ActiveOverflowComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveOverflow object
    ActiveOverflow(
        const char* const compName //!< The component name
    );

    //! Destroy ActiveOverflow object
    ~ActiveOverflow();

  private:

    // ----------------------------------------------------------------------
    // Handler implementations for typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for assertAsync
    //!
    //! A port with assert behavior
    void assertAsync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for blockAsync
    //!
    //! A port with block behavior
    void blockAsync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for dropAsync
    //!
    //! A port with drop behavior
    void dropAsync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for hookAsync
    //!
    //! A port with hook behavior
    void hookAsync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

  private:

    // ----------------------------------------------------------------------
    // Handler implementations for serial input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for serialAsyncHook
    //!
    //! A serial async input port with overflow hook
    void serialAsyncHook_handler(
        FwIndexType portNum, //!< The port number
        Fw::SerializeBufferBase& buffer //!< The serialization buffer
    ) override;

  private:

    // ----------------------------------------------------------------------
    // Handler implementations for commands
    // ----------------------------------------------------------------------

    //! Handler implementation for command CMD_HOOK
    //!
    //! A command with queue full 'hook' behavior
    void CMD_HOOK_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    ) override;

    //! Handler implementation for command CMD_PARAMS_PRIORITY_HOOK
    //!
    //! A command with params, priority, and queue full 'hook' behavior
    void CMD_PARAMS_PRIORITY_HOOK_cmdHandler(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        U32 u32
    ) override;

  private:

    // ----------------------------------------------------------------------
    // Handler implementations for user-defined internal interfaces
    // ----------------------------------------------------------------------

    //! Handler implementation for internalHookDrop
    //!
    //! An internal port with hook queue full behavior
    void internalHookDrop_internalInterfaceHandler() override;

  private:

    // ----------------------------------------------------------------------
    // Overflow hook implementations for typed input ports
    // ----------------------------------------------------------------------

    //! Overflow hook implementation for hookAsync
    void hookAsync_overflowHook(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

  private:

    // ----------------------------------------------------------------------
    // Overflow hook implementations for serial input ports
    // ----------------------------------------------------------------------

    //! Overflow hook implementation for serialAsyncHook
    void serialAsyncHook_overflowHook(
        FwIndexType portNum, //!< The port number
        Fw::SerializeBufferBase& buffer //!< The serialization buffer
    ) override;

  private:

    // ----------------------------------------------------------------------
    // Overflow hook implementations for special input ports
    // ----------------------------------------------------------------------

    //! Overflow hook implementation for productRecvInHook
    void productRecvInHook_overflowHook(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer, //!< The buffer
        const Fw::Success& status //!< The status
    ) override;

  private:

    // ----------------------------------------------------------------------
    // Overflow hook implementations for internal ports
    // ----------------------------------------------------------------------

    //! Overflow hook implementation for internalHookDrop
    void internalHookDrop_overflowHook() override;

  private:

    // ----------------------------------------------------------------------
    // Overflow hook implementations for commands
    // ----------------------------------------------------------------------

    //! Overflow hook implementation for CMD_HOOK
    void CMD_HOOK_cmdOverflowHook(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    ) override;

    //! Overflow hook implementation for CMD_PARAMS_PRIORITY_HOOK
    void CMD_PARAMS_PRIORITY_HOOK_cmdOverflowHook(
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq //!< The command sequence number
    ) override;

};

#endif
