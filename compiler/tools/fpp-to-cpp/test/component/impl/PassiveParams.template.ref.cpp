// ======================================================================
// \title  PassiveParams.cpp
// \author [user name]
// \brief  cpp file for PassiveParams component implementation class
// ======================================================================

#include "PassiveParams.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

PassiveParams ::
  PassiveParams(const char* const compName) :
    PassiveParamsComponentBase(compName)
{

}

PassiveParams ::
  ~PassiveParams()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

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

void PassiveParams ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
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
