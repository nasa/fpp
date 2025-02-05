// ======================================================================
// \title  QueuedGetProducts.cpp
// \author [user name]
// \brief  cpp file for QueuedGetProducts component implementation class
// ======================================================================

#include "QueuedGetProducts.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedGetProducts ::
  QueuedGetProducts(const char* const compName) :
    QueuedGetProductsComponentBase(compName)
{

}

QueuedGetProducts ::
  ~QueuedGetProducts()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedGetProducts ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedGetProducts ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedGetProducts ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedGetProducts ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedGetProducts ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedGetProducts ::
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

void QueuedGetProducts ::
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

void QueuedGetProducts ::
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

void QueuedGetProducts ::
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

void QueuedGetProducts ::
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

F32 QueuedGetProducts ::
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

F32 QueuedGetProducts ::
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

void QueuedGetProducts ::
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
