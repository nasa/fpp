// ======================================================================
// \title  QueuedCommands.cpp
// \author [user name]
// \brief  cpp file for QueuedCommands component implementation class
// ======================================================================

#include "QueuedCommands.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedCommands ::
  QueuedCommands(const char* const compName) :
    QueuedCommandsComponentBase(compName)
{

}

QueuedCommands ::
  ~QueuedCommands()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedCommands ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedCommands ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedCommands ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedCommands ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedCommands ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedCommands ::
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

void QueuedCommands ::
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

void QueuedCommands ::
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

void QueuedCommands ::
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

void QueuedCommands ::
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

F32 QueuedCommands ::
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

F32 QueuedCommands ::
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

void QueuedCommands ::
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

void QueuedCommands ::
  CMD_SYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
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

void QueuedCommands ::
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

void QueuedCommands ::
  CMD_SYNC_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_SYNC_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_SYNC_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_GUARDED_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
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

void QueuedCommands ::
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

void QueuedCommands ::
  CMD_GUARDED_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_GUARDED_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_GUARDED_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_ASYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_PARAMS_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      U32 u32
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_DROP_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedCommands ::
  CMD_PARAMS_PRIORITY_DROP_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      U32 u32
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}
