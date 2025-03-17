// ======================================================================
// \title  ActiveTelemetry.cpp
// \author [user name]
// \brief  cpp file for ActiveTelemetry component implementation class
// ======================================================================

#include "ActiveTelemetry.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

ActiveTelemetry ::
  ActiveTelemetry(const char* const compName) :
    ActiveTelemetryComponentBase(compName)
{

}

ActiveTelemetry ::
  ~ActiveTelemetry()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void ActiveTelemetry ::
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

Fw::String ActiveTelemetry ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void ActiveTelemetry ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveTelemetry ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 ActiveTelemetry ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 ActiveTelemetry ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

Fw::String ActiveTelemetry ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void ActiveTelemetry ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveTelemetry ::
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

F32 ActiveTelemetry ::
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

Fw::String ActiveTelemetry ::
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

void ActiveTelemetry ::
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

void ActiveTelemetry ::
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

void ActiveTelemetry ::
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

void ActiveTelemetry ::
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

void ActiveTelemetry ::
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

F32 ActiveTelemetry ::
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

F32 ActiveTelemetry ::
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

void ActiveTelemetry ::
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
