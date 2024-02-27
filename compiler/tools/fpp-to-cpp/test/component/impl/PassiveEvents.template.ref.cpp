// ======================================================================
// \title  PassiveEvents.cpp
// \author [user name]
// \brief  cpp file for PassiveEvents component implementation class
// ======================================================================

#include "FpConfig.hpp"
#include "PassiveEvents.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
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
// Handler implementations for user-defined typed input ports
// ----------------------------------------------------------------------

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

void PassiveEvents ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveEvents ::
  typedGuarded_handler(
      FwIndexType portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Ports::TypedPortStrings::StringSize80& str1,
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
      const Ports::TypedReturnPortStrings::StringSize80& str2,
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
      const Ports::TypedReturnPortStrings::StringSize80& str2,
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
      const Ports::TypedPortStrings::StringSize80& str1,
      const E& e,
      const A& a,
      const S& s
  )
{
  // TODO
}
