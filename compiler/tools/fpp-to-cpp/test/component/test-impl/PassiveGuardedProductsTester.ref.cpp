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
  from_noArgsOut_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

U32 PassiveGuardedProductsTester ::
  from_noArgsReturnOut_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}

void PassiveGuardedProductsTester ::
  from_typedOut_handler(
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

F32 PassiveGuardedProductsTester ::
  from_typedReturnOut_handler(
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
