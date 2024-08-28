// ======================================================================
// \title  ActiveOverflow.cpp
// \author [user name]
// \brief  cpp file for ActiveOverflow component implementation class
// ======================================================================

#include "ActiveOverflow.hpp"
#include "FpConfig.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

ActiveOverflow ::
  ActiveOverflow(const char* const compName) :
    ActiveOverflowComponentBase(compName)
{

}

ActiveOverflow ::
  ~ActiveOverflow()
{

}

// ----------------------------------------------------------------------
// Handler implementations for user-defined typed input ports
// ----------------------------------------------------------------------

void ActiveOverflow ::
  assertAsync_handler(
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

void ActiveOverflow ::
  blockAsync_handler(
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

void ActiveOverflow ::
  dropAsync_handler(
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

void ActiveOverflow ::
  hookAsync_handler(
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
// Handler implementations for user-defined serial input ports
// ----------------------------------------------------------------------

void ActiveOverflow ::
  serialAsyncHook_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

// ----------------------------------------------------------------------
// Handler implementations for commands
// ----------------------------------------------------------------------

void ActiveOverflow ::
  CMD_HOOK_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void ActiveOverflow ::
  CMD_PARAMS_PRIORITY_HOOK_cmdHandler(
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

void ActiveOverflow ::
  internalHookDrop_internalInterfaceHandler()
{
  // TODO
}

// ----------------------------------------------------------------------
// Overflow hook implementations for 'hook' input ports
// ----------------------------------------------------------------------

void ActiveOverflow ::
  hookAsync_overflowHook(
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

void ActiveOverflow ::
  serialAsyncHook_overflowHook(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void ActiveOverflow ::
  productRecvInHook_overflowHook(
      FwIndexType portNum,
      FwDpIdType id,
      const Fw::Buffer& buffer,
      const Fw::Success& status
  )
{
  // TODO
}

// ----------------------------------------------------------------------
// Overflow hook implementations for internal ports
// ----------------------------------------------------------------------

void ActiveOverflow ::
  internalHookDrop_overflowHook()
{
  // TODO
}

// ----------------------------------------------------------------------
// Overflow hook implementations for commands
// ----------------------------------------------------------------------

void ActiveOverflow ::
  CMD_HOOK_cmdOverflowHook(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
}

void ActiveOverflow ::
  CMD_PARAMS_PRIORITY_HOOK_cmdOverflowHook(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
}
