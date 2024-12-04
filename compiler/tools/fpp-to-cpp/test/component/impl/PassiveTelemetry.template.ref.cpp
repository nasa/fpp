// ======================================================================
// \title  PassiveTelemetry.cpp
// \author [user name]
// \brief  cpp file for PassiveTelemetry component implementation class
// ======================================================================

#include "PassiveTelemetry.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

PassiveTelemetry ::
  PassiveTelemetry(const char* const compName) :
    PassiveTelemetryComponentBase(compName)
{

}

PassiveTelemetry ::
  ~PassiveTelemetry()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void PassiveTelemetry ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 PassiveTelemetry ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 PassiveTelemetry ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveTelemetry ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveTelemetry ::
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

F32 PassiveTelemetry ::
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

F32 PassiveTelemetry ::
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

void PassiveTelemetry ::
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
