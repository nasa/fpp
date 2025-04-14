// ======================================================================
// \title  PassiveEvents.cpp
// \author [user name]
// \brief  cpp file for PassiveEvents component implementation class
// ======================================================================

#include "PassiveEvents.hpp"

// ----------------------------------------------------------------------
// Component construction test and destruction
// ----------------------------------------------------------------------

PassiveEvents ::
  PassiveEvents(const char* const compName) :
    PassiveEventsComponentBase(compName)
{

}

PassiveEvents ::
  ~PassiveEvents()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

Fw::String PassiveEvents ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveEvents ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 PassiveEvents ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 PassiveEvents ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

Fw::String PassiveEvents ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveEvents ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveEvents ::
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

AliasPrim2 PassiveEvents ::
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

Fw::String PassiveEvents ::
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

void PassiveEvents ::
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

F32 PassiveEvents ::
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

F32 PassiveEvents ::
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

void PassiveEvents ::
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
