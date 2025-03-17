// ======================================================================
// \title  QueuedTelemetry.cpp
// \author [user name]
// \brief  cpp file for QueuedTelemetry component implementation class
// ======================================================================

#include "QueuedTelemetry.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedTelemetry ::
  QueuedTelemetry(const char* const compName) :
    QueuedTelemetryComponentBase(compName)
{

}

QueuedTelemetry ::
  ~QueuedTelemetry()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedTelemetry ::
  aliasTypedAsync_handler(
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
  // TODO
}

Fw::String QueuedTelemetry ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedTelemetry ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedTelemetry ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedTelemetry ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedTelemetry ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

Fw::String QueuedTelemetry ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedTelemetry ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedTelemetry ::
  typedAliasGuarded_handler(
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
  // TODO
}

F32 QueuedTelemetry ::
  typedAliasReturnSync_handler(
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

Fw::String QueuedTelemetry ::
  typedAliasStringReturnSync_handler(
      FwIndexType portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Fw::StringBase& str2,
      const E& e,
      const A& a,
      const StructWithAlias& s
  )
{
  // TODO return
}

void QueuedTelemetry ::
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

void QueuedTelemetry ::
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

void QueuedTelemetry ::
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

void QueuedTelemetry ::
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

void QueuedTelemetry ::
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

F32 QueuedTelemetry ::
  typedReturnGuarded_handler(
      FwIndexType portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Fw::StringBase& str2,
      const E& e,
      const AA& a,
      const S& s
  )
{
  // TODO return
}

F32 QueuedTelemetry ::
  typedReturnSync_handler(
      FwIndexType portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Fw::StringBase& str2,
      const E& e,
      const AA& a,
      const S& s
  )
{
  // TODO return
}

void QueuedTelemetry ::
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
