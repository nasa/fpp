// ======================================================================
// \title  QueuedExternalParams.cpp
// \author [user name]
// \brief  cpp file for QueuedExternalParams component implementation class
// ======================================================================

#include "QueuedExternalParams.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedExternalParams ::
  QueuedExternalParams(const char* const compName)
{
  // TODO Initialize the ParamExternalDelegate
  // The register function can be called directly here:
  // E.G. this->registerExternalParameters(SomeParamExternalDelegateChild());
  // Or you can call the register function in a public setup method
  // that is called when setting up the component instance.
}

QueuedExternalParams ::
  ~QueuedExternalParams()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedExternalParams ::
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

Fw::String QueuedExternalParams ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedExternalParams ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedExternalParams ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedExternalParams ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedExternalParams ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

Fw::String QueuedExternalParams ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedExternalParams ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedExternalParams ::
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

AliasPrim2 QueuedExternalParams ::
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

Fw::String QueuedExternalParams ::
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

void QueuedExternalParams ::
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

void QueuedExternalParams ::
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

void QueuedExternalParams ::
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

void QueuedExternalParams ::
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

void QueuedExternalParams ::
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

F32 QueuedExternalParams ::
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

F32 QueuedExternalParams ::
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

void QueuedExternalParams ::
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
