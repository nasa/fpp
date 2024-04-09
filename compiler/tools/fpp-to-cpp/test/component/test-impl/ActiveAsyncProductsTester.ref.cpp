// ======================================================================
// \title  ActiveAsyncProductsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveAsyncProducts component test harness implementation class
// ======================================================================

#include "ActiveAsyncProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveAsyncProductsTester ::
  ActiveAsyncProductsTester() :
    ActiveAsyncProductsGTestBase("ActiveAsyncProductsTester", ActiveAsyncProductsTester::MAX_HISTORY_SIZE),
    component("ActiveAsyncProducts")
{
  this->initComponents();
  this->connectPorts();
}

ActiveAsyncProductsTester ::
  ~ActiveAsyncProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveAsyncProductsTester ::
  toDo()
{
  // TODO
}
