// ======================================================================
// \title  ActiveGuardedProducts.cpp
// \author [user name]
// \brief  cpp file for ActiveGuardedProducts component implementation class
// ======================================================================

#include "ActiveGuardedProducts.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

ActiveGuardedProducts ::
  ActiveGuardedProducts(const char* const compName) :
    ActiveGuardedProductsComponentBase(compName)
{

}

ActiveGuardedProducts ::
  ~ActiveGuardedProducts()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void ActiveGuardedProducts ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveGuardedProducts ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 ActiveGuardedProducts ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 ActiveGuardedProducts ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void ActiveGuardedProducts ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveGuardedProducts ::
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

void ActiveGuardedProducts ::
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

void ActiveGuardedProducts ::
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

void ActiveGuardedProducts ::
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

void ActiveGuardedProducts ::
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

F32 ActiveGuardedProducts ::
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

F32 ActiveGuardedProducts ::
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

void ActiveGuardedProducts ::
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

// ----------------------------------------------------------------------
// Handler implementations for data products
// ----------------------------------------------------------------------

void ActiveGuardedProducts ::
  dpRecv_Container1_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void ActiveGuardedProducts ::
  dpRecv_Container2_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void ActiveGuardedProducts ::
  dpRecv_Container3_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void ActiveGuardedProducts ::
  dpRecv_Container4_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void ActiveGuardedProducts ::
  dpRecv_Container5_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}
