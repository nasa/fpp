// ======================================================================
// \title  PassiveGuardedProductsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveGuardedProducts component test harness implementation class
// ======================================================================

#include "PassiveGuardedProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveGuardedProductsTester ::
  PassiveGuardedProductsTester() :
    PassiveGuardedProductsGTestBase("PassiveGuardedProductsTester", PassiveGuardedProductsTester::MAX_HISTORY_SIZE),
    component("PassiveGuardedProducts")
{
  this->initComponents();
  this->connectPorts();
}

PassiveGuardedProductsTester ::
  ~PassiveGuardedProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveGuardedProductsTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void PassiveGuardedProductsTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 PassiveGuardedProductsTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  // TODO: Return a value
}

void PassiveGuardedProductsTester ::
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

F32 PassiveGuardedProductsTester ::
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
  // TODO: Return a value
}
