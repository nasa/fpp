// ======================================================================
// \title  PassiveParams.cpp
// \author [user name]
// \brief  cpp file for PassiveParams component implementation class
// ======================================================================

#include "PassiveParams.hpp"

// ----------------------------------------------------------------------
// Component construction test and destruction
// ----------------------------------------------------------------------

PassiveParams ::
  PassiveParams(const char* const compName)
{
  // TODO Initialize component base class with concrete implementation of ParamExternalDelegate
  PassiveParamsComponentBase(Fw::ParamExternalDelegate(), compName);
}

PassiveParams ::
  ~PassiveParams()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

AliasString PassiveParams ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveParams ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 PassiveParams ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 PassiveParams ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

Fw::String PassiveParams ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveParams ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveParams ::
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

AliasPrim2 PassiveParams ::
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

AliasString PassiveParams ::
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

void PassiveParams ::
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

F32 PassiveParams ::
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

F32 PassiveParams ::
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

void PassiveParams ::
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
