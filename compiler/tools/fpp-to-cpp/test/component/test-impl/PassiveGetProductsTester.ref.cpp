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
