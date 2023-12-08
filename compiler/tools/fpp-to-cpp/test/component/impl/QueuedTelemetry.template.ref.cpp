// ======================================================================
// \title  QueuedTelemetry.cpp
// \author [user name]
// \brief  cpp file for QueuedTelemetry component implementation class
// ======================================================================

#include "FpConfig.hpp"
#include "impl/QueuedTelemetry.hpp"

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
// Handler implementations for user-defined typed input ports
// ----------------------------------------------------------------------

void QueuedTelemetry ::
  noArgsAsync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

void QueuedTelemetry ::
  noArgsGuarded_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

U32 QueuedTelemetry ::
  noArgsReturnGuarded_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}

U32 QueuedTelemetry ::
  noArgsReturnSync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}

void QueuedTelemetry ::
  noArgsSync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

void QueuedTelemetry ::
  typedAsync_handler(
      NATIVE_INT_TYPE portNum,
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

void QueuedTelemetry ::
  typedAsyncAssert_handler(
      NATIVE_INT_TYPE portNum,
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

void QueuedTelemetry ::
  typedAsyncBlockPriority_handler(
      NATIVE_INT_TYPE portNum,
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

void QueuedTelemetry ::
  typedAsyncDropPriority_handler(
      NATIVE_INT_TYPE portNum,
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

void QueuedTelemetry ::
  typedGuarded_handler(
      NATIVE_INT_TYPE portNum,
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

F32 QueuedTelemetry ::
  typedReturnGuarded_handler(
      NATIVE_INT_TYPE portNum,
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

F32 QueuedTelemetry ::
  typedReturnSync_handler(
      NATIVE_INT_TYPE portNum,
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

void QueuedTelemetry ::
  typedSync_handler(
      NATIVE_INT_TYPE portNum,
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
