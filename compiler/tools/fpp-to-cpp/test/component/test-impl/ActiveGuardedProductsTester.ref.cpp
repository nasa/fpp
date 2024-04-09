// ======================================================================
// \title  ActiveGuardedProductsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveGuardedProducts component test harness implementation class
// ======================================================================

#include "ActiveGuardedProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveGuardedProductsTester ::
  ActiveGuardedProductsTester() :
    ActiveGuardedProductsGTestBase("ActiveGuardedProductsTester", ActiveGuardedProductsTester::MAX_HISTORY_SIZE),
    component("ActiveGuardedProducts")
{
  this->initComponents();
  this->connectPorts();
}

ActiveGuardedProductsTester ::
  ~ActiveGuardedProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveGuardedProductsTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void ActiveGuardedProductsTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 ActiveGuardedProductsTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  return 0;
}

void ActiveGuardedProductsTester ::
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

F32 ActiveGuardedProductsTester ::
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
