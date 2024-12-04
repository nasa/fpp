// ======================================================================
// \title  ActiveTest.cpp
// \author [user name]
// \brief  cpp file for ActiveTest component implementation class
// ======================================================================

#include "ActiveTest.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  ActiveTest ::
    ActiveTest(const char* const compName) :
      ActiveTestComponentBase(compName)
  {

  }

  ActiveTest ::
    ~ActiveTest()
  {

  }

  // ----------------------------------------------------------------------
  // Handler implementations for typed input ports
  // ----------------------------------------------------------------------

  void ActiveTest ::
    noArgsAsync_handler(FwIndexType portNum)
  {
    // TODO
  }

  void ActiveTest ::
    noArgsGuarded_handler(FwIndexType portNum)
  {
    // TODO
  }

  U32 ActiveTest ::
    noArgsReturnGuarded_handler(FwIndexType portNum)
  {
    // TODO return
  }

  U32 ActiveTest ::
    noArgsReturnSync_handler(FwIndexType portNum)
  {
    // TODO return
  }

  void ActiveTest ::
    noArgsSync_handler(FwIndexType portNum)
  {
    // TODO
  }

  void ActiveTest ::
    typedAsync_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  void ActiveTest ::
    typedAsyncAssert_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  void ActiveTest ::
    typedAsyncBlockPriority_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  void ActiveTest ::
    typedAsyncDropPriority_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  void ActiveTest ::
    typedGuarded_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  F32 ActiveTest ::
    typedReturnGuarded_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str2,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO return
  }

  F32 ActiveTest ::
    typedReturnSync_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str2,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO return
  }

  void ActiveTest ::
    typedSync_handler(
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Handler implementations for commands
  // ----------------------------------------------------------------------

  void ActiveTest ::
    CMD_SYNC_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_SYNC_PRIMITIVE_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        U32 u32,
        F32 f32,
        bool b
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_SYNC_STRING_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        const Fw::CmdStringArg& str1,
        const Fw::CmdStringArg& str2
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_SYNC_ENUM_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        E e
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_SYNC_ARRAY_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        A a
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_SYNC_STRUCT_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        S s
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_GUARDED_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_GUARDED_PRIMITIVE_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        U32 u32,
        F32 f32,
        bool b
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_GUARDED_STRING_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        const Fw::CmdStringArg& str1,
        const Fw::CmdStringArg& str2
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_GUARDED_ENUM_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        E e
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_GUARDED_ARRAY_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        A a
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_GUARDED_STRUCT_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        S s
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_ASYNC_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_PRIORITY_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_PARAMS_PRIORITY_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        U32 u32
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_DROP_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  void ActiveTest ::
    CMD_PARAMS_PRIORITY_DROP_cmdHandler(
        FwOpcodeType opCode,
        U32 cmdSeq,
        U32 u32
    )
  {
    // TODO
    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
  }

  // ----------------------------------------------------------------------
  // Handler implementations for user-defined internal interfaces
  // ----------------------------------------------------------------------

  void ActiveTest ::
    internalArray_internalInterfaceHandler(const A& a)
  {
    // TODO
  }

  void ActiveTest ::
    internalEnum_internalInterfaceHandler(const E& e)
  {
    // TODO
  }

  void ActiveTest ::
    internalPrimitive_internalInterfaceHandler(
        U32 u32,
        F32 f32,
        bool b
    )
  {
    // TODO
  }

  void ActiveTest ::
    internalPriorityDrop_internalInterfaceHandler()
  {
    // TODO
  }

  void ActiveTest ::
    internalString_internalInterfaceHandler(
        const Fw::InternalInterfaceString& str1,
        const Fw::InternalInterfaceString& str2
    )
  {
    // TODO
  }

  void ActiveTest ::
    internalStruct_internalInterfaceHandler(const S& s)
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Handler implementations for data products
  // ----------------------------------------------------------------------

  void ActiveTest ::
    dpRecv_Container1_handler(
        DpContainer& container,
        Fw::Success::T status
    )
  {
    // TODO
  }

  void ActiveTest ::
    dpRecv_Container2_handler(
        DpContainer& container,
        Fw::Success::T status
    )
  {
    // TODO
  }

  void ActiveTest ::
    dpRecv_Container3_handler(
        DpContainer& container,
        Fw::Success::T status
    )
  {
    // TODO
  }

  void ActiveTest ::
    dpRecv_Container4_handler(
        DpContainer& container,
        Fw::Success::T status
    )
  {
    // TODO
  }

  void ActiveTest ::
    dpRecv_Container5_handler(
        DpContainer& container,
        Fw::Success::T status
    )
  {
    // TODO
  }

}
