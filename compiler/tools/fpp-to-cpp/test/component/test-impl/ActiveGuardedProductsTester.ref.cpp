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
