// ======================================================================
// \title  ActiveSerial.cpp
// \author [user name]
// \brief  cpp file for ActiveSerial component implementation class
// ======================================================================

#include "ActiveSerial.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

ActiveSerial ::
  ActiveSerial(const char* const compName) :
    ActiveSerialComponentBase(compName)
{

}

ActiveSerial ::
  ~ActiveSerial()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void ActiveSerial ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveSerial ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 ActiveSerial ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 ActiveSerial ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void ActiveSerial ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveSerial ::
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

void ActiveSerial ::
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

void ActiveSerial ::
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

void ActiveSerial ::
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

void ActiveSerial ::
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

F32 ActiveSerial ::
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

F32 ActiveSerial ::
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

void ActiveSerial ::
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
// Handler implementations for serial input ports
// ----------------------------------------------------------------------

void ActiveSerial ::
  serialAsync_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void ActiveSerial ::
  serialAsyncAssert_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void ActiveSerial ::
  serialAsyncBlockPriority_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void ActiveSerial ::
  serialAsyncDropPriority_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void ActiveSerial ::
  serialGuarded_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void ActiveSerial ::
  serialSync_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

// ----------------------------------------------------------------------
// Handler implementations for commands
// ----------------------------------------------------------------------

void ActiveSerial ::
  CMD_SYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
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

void ActiveSerial ::
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

void ActiveSerial ::
  CMD_SYNC_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_SYNC_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_SYNC_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_GUARDED_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
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

void ActiveSerial ::
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

void ActiveSerial ::
  CMD_GUARDED_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_GUARDED_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_GUARDED_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_ASYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_PARAMS_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      U32 u32
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
  CMD_DROP_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveSerial ::
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

void ActiveSerial ::
  internalArray_internalInterfaceHandler(const A& a)
{
  // TODO
}

void ActiveSerial ::
  internalEnum_internalInterfaceHandler(const E& e)
{
  // TODO
}

void ActiveSerial ::
  internalPrimitive_internalInterfaceHandler(
      U32 u32,
      F32 f32,
      bool b
  )
{
  // TODO
}

void ActiveSerial ::
  internalPriorityDrop_internalInterfaceHandler()
{
  // TODO
}

void ActiveSerial ::
  internalString_internalInterfaceHandler(
      const Fw::InternalInterfaceString& str1,
      const Fw::InternalInterfaceString& str2
  )
{
  // TODO
}

void ActiveSerial ::
  internalStruct_internalInterfaceHandler(const S& s)
{
  // TODO
}
