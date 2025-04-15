// ======================================================================
// \title  QueuedParams.cpp
// \author [user name]
// \brief  cpp file for QueuedParams component implementation class
// ======================================================================

#include "QueuedParams.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedParams ::
  QueuedParams(const char* const compName) :
    QueuedParamsComponentBase(compName)
{

}

QueuedParams ::
  ~QueuedParams()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedParams ::
  aliasTypedAsync_handler(
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

Fw::String QueuedParams ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedParams ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedParams ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedParams ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedParams ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

Fw::String QueuedParams ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedParams ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedParams ::
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

AliasPrim2 QueuedParams ::
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

Fw::String QueuedParams ::
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

void QueuedParams ::
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

void QueuedParams ::
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

void QueuedParams ::
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

void QueuedParams ::
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

void QueuedParams ::
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

F32 QueuedParams ::
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

F32 QueuedParams ::
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

void QueuedParams ::
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
