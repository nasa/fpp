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

AliasString PassiveTelemetry ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

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

Fw::String PassiveTelemetry ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveTelemetry ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveTelemetry ::
  typedAliasGuarded_handler(
      FwIndexType portNum,
      AliasPrim1 u32,
      AliasPrim2 f32,
      AliasBool b,
      const Fw::StringBase& str2,
      const AliasEnum& e,
      const AliasArray& a,
      const AliasStruct& s
  )
{
  // TODO
}

AliasPrim2 PassiveTelemetry ::
  typedAliasReturnSync_handler(
      FwIndexType portNum,
      AliasPrim1 u32,
      AliasPrim2 f32,
      AliasBool b,
      const Fw::StringBase& str2,
      const AliasEnum& e,
      const AliasArray& a,
      const AliasStruct& s
  )
{
  // TODO return
}

AliasString PassiveTelemetry ::
  typedAliasStringReturnSync_handler(
      FwIndexType portNum,
      AliasPrim1 u32,
      AliasPrim2 f32,
      AliasBool b,
      const Fw::StringBase& str2,
      const AliasEnum& e,
      const AliasArray& a,
      const AnotherAliasStruct& s
  )
{
  // TODO return
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
      const AA& a,
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
      const AA& a,
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
