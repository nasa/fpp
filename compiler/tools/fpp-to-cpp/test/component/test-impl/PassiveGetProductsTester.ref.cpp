// ======================================================================
// \title  PassiveGetProductsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveGetProducts component test harness implementation class
// ======================================================================

#include "PassiveGetProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveGetProductsTester ::
  PassiveGetProductsTester() :
    PassiveGetProductsGTestBase("PassiveGetProductsTester", PassiveGetProductsTester::MAX_HISTORY_SIZE),
    component("PassiveGetProducts")
{
  this->initComponents();
  this->connectPorts();
}

PassiveGetProductsTester ::
  ~PassiveGetProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveGetProductsTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void PassiveGetProductsTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 PassiveGetProductsTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  return 0;
}

void PassiveGetProductsTester ::
  from_typedOut_handler(
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
  this->pushFromPortEntry_typedOut(u32, f32, b, str1, e, a, s);
}

F32 PassiveGetProductsTester ::
  from_typedReturnOut_handler(
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
  this->pushFromPortEntry_typedReturnOut(u32, f32, b, str2, e, a, s);
  return 0.0f;
}
