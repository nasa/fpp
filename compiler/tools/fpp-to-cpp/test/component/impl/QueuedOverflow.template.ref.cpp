// ======================================================================
// \title  QueuedOverflow.cpp
// \author [user name]
// \brief  cpp file for QueuedOverflow component implementation class
// ======================================================================

#include "FpConfig.hpp"
#include "QueuedOverflow.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedOverflow ::
  QueuedOverflow(const char* const compName) :
    QueuedOverflowComponentBase(compName)
{

}

QueuedOverflow ::
  ~QueuedOverflow()
{

}

// ----------------------------------------------------------------------
// Handler implementations for user-defined typed input ports
// ----------------------------------------------------------------------

void QueuedOverflow ::
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

void QueuedOverflow ::
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

void QueuedOverflow ::
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

void QueuedOverflow ::
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

void QueuedOverflow ::
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

void QueuedOverflow ::
  CMD_HOOK_cmdHandler(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
  this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);
}

void QueuedOverflow ::
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

void QueuedOverflow ::
  internalHookDrop_internalInterfaceHandler()
{
  // TODO
}

// ----------------------------------------------------------------------
// Overflow hook implementations for 'hook' input ports
// ----------------------------------------------------------------------

void QueuedOverflow ::
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

void QueuedOverflow ::
  serialAsyncHook_overflowHook(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}

void QueuedOverflow ::
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

void QueuedOverflow ::
  internalHookDrop_overflowHook()
{
  // TODO
}

// ----------------------------------------------------------------------
// Overflow hook implementations for commands
// ----------------------------------------------------------------------

void QueuedOverflow ::
  CMD_HOOK_cmdOverflowHook(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
}

void QueuedOverflow ::
  CMD_PARAMS_PRIORITY_HOOK_cmdOverflowHook(
      FwOpcodeType opCode,
      U32 cmdSeq
  )
{
  // TODO
}
