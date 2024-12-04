// ======================================================================
// \title  QueuedSerial.cpp
// \author [user name]
// \brief  cpp file for QueuedSerial component implementation class
// ======================================================================

#include "QueuedSerial.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedSerial ::
  QueuedSerial(const char* const compName) :
    QueuedSerialComponentBase(compName)
{

}

QueuedSerial ::
  ~QueuedSerial()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedSerial ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedSerial ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedSerial ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedSerial ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedSerial ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedSerial ::
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

void QueuedSerial ::
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

void QueuedSerial ::
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

void QueuedSerial ::
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

void QueuedSerial ::
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

F32 QueuedSerial ::
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

F32 QueuedSerial ::
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

void QueuedSerial ::
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

void QueuedSerial ::
  serialAsync_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void QueuedSerial ::
  serialAsyncAssert_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void QueuedSerial ::
  serialAsyncBlockPriority_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void QueuedSerial ::
  serialAsyncDropPriority_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void QueuedSerial ::
  serialGuarded_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void QueuedSerial ::
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

void QueuedSerial ::
  CMD_SYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
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

void QueuedSerial ::
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

void QueuedSerial ::
  CMD_SYNC_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_SYNC_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_SYNC_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_GUARDED_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
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

void QueuedSerial ::
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

void QueuedSerial ::
  CMD_GUARDED_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_GUARDED_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_GUARDED_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_ASYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_PARAMS_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      U32 u32
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
  CMD_DROP_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedSerial ::
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

void QueuedSerial ::
  internalArray_internalInterfaceHandler(const A& a)
{
  // TODO
}

void QueuedSerial ::
  internalEnum_internalInterfaceHandler(const E& e)
{
  // TODO
}

void QueuedSerial ::
  internalPrimitive_internalInterfaceHandler(
      U32 u32,
      F32 f32,
      bool b
  )
{
  // TODO
}

void QueuedSerial ::
  internalPriorityDrop_internalInterfaceHandler()
{
  // TODO
}

void QueuedSerial ::
  internalString_internalInterfaceHandler(
      const Fw::InternalInterfaceString& str1,
      const Fw::InternalInterfaceString& str2
  )
{
  // TODO
}

void QueuedSerial ::
  internalStruct_internalInterfaceHandler(const S& s)
{
  // TODO
}
