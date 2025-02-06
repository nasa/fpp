// ======================================================================
// \title  PassiveGetProducts.cpp
// \author [user name]
// \brief  cpp file for PassiveGetProducts component implementation class
// ======================================================================

#include "PassiveGetProducts.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

PassiveGetProducts ::
  PassiveGetProducts(const char* const compName) :
    PassiveGetProductsComponentBase(compName)
{

}

PassiveGetProducts ::
  ~PassiveGetProducts()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void PassiveGetProducts ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 PassiveGetProducts ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 PassiveGetProducts ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveGetProducts ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveGetProducts ::
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

F32 PassiveGetProducts ::
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

F32 PassiveGetProducts ::
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

void PassiveGetProducts ::
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
