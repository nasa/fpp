// ======================================================================
// \title  ActiveCommands.cpp
// \author [user name]
// \brief  cpp file for ActiveCommands component implementation class
// ======================================================================

#include "ActiveCommands.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

ActiveCommands ::
  ActiveCommands(const char* const compName) :
    ActiveCommandsComponentBase(compName)
{

}

ActiveCommands ::
  ~ActiveCommands()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void ActiveCommands ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveCommands ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 ActiveCommands ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 ActiveCommands ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void ActiveCommands ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveCommands ::
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

void ActiveCommands ::
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

void ActiveCommands ::
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

void ActiveCommands ::
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

void ActiveCommands ::
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

F32 ActiveCommands ::
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

F32 ActiveCommands ::
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

void ActiveCommands ::
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

void ActiveCommands ::
  CMD_SYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
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

void ActiveCommands ::
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

void ActiveCommands ::
  CMD_SYNC_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_SYNC_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_SYNC_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_GUARDED_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
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

void ActiveCommands ::
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

void ActiveCommands ::
  CMD_GUARDED_ENUM_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      E e
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_GUARDED_ARRAY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      A a
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_GUARDED_STRUCT_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      S s
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_ASYNC_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_PARAMS_PRIORITY_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      U32 u32
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_DROP_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveCommands ::
  CMD_PARAMS_PRIORITY_DROP_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq,
      U32 u32
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}
