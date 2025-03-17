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

AliasString PassiveGetProducts ::
  noArgsAliasStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

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

Fw::String PassiveGetProducts ::
  noArgsStringReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}

void PassiveGetProducts ::
  noArgsSync_handler(FwIndexType portNum)
{
  // TODO
}

void PassiveGetProducts ::
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

AliasPrim2 PassiveGetProducts ::
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

AliasString PassiveGetProducts ::
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
      const AA& a,
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
      const AA& a,
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
