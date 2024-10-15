// ======================================================================
// \title  PassiveSyncProducts.cpp
// \author [user name]
// \brief  cpp file for PassiveSyncProducts component implementation class
// ======================================================================

#include "PassiveSyncProducts.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

PassiveSyncProducts ::
  PassiveSyncProducts(const char* const compName) :
    PassiveSyncProductsComponentBase(compName)
{

}

PassiveSyncProducts ::
  ~PassiveSyncProducts()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void PassiveSyncProducts ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 PassiveSyncProducts ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 PassiveSyncProducts ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveSyncProducts ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveSyncProducts ::
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

F32 PassiveSyncProducts ::
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

F32 PassiveSyncProducts ::
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

void PassiveSyncProducts ::
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

void PassiveSyncProducts ::
  dpRecv_Container1_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveSyncProducts ::
  dpRecv_Container2_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveSyncProducts ::
  dpRecv_Container3_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveSyncProducts ::
  dpRecv_Container4_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}

void PassiveSyncProducts ::
  dpRecv_Container5_handler(
      DpContainer& container,
      Fw::Success::T status
  )
{
  // TODO
}
