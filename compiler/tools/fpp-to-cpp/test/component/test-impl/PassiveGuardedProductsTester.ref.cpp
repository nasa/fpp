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
  this->deinit();
}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveGuardedProductsTester ::
  toDo()
{
  // TODO
}
