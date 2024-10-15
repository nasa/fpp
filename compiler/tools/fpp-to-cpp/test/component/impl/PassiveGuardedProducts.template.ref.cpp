// ======================================================================
// \title  PassiveGuardedProducts.cpp
// \author [user name]
// \brief  cpp file for PassiveGuardedProducts component implementation class
// ======================================================================

#include "PassiveGuardedProducts.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

PassiveGuardedProducts ::
  PassiveGuardedProducts(const char* const compName) :
    PassiveGuardedProductsComponentBase(compName)
{

}

PassiveGuardedProducts ::
  ~PassiveGuardedProducts()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void PassiveGuardedProducts ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 PassiveGuardedProducts ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 PassiveGuardedProducts ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveGuardedProducts ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveGuardedProducts ::
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

F32 PassiveGuardedProducts ::
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

F32 PassiveGuardedProducts ::
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

void PassiveGuardedProducts ::
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

void PassiveGuardedProducts ::
  dpRecv_Container1_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveGuardedProducts ::
  dpRecv_Container2_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveGuardedProducts ::
  dpRecv_Container3_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveGuardedProducts ::
  dpRecv_Container4_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveGuardedProducts ::
  dpRecv_Container5_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}
