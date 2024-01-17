// ======================================================================
// \title  ActiveAsyncProductPortsOnlyTester.cpp
// \author [user name]
// \brief  cpp file for ActiveAsyncProductPortsOnly component test harness implementation class
// ======================================================================

#include "ActiveAsyncProductPortsOnlyTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveAsyncProductPortsOnlyTester ::
  ActiveAsyncProductPortsOnlyTester() :
    ActiveAsyncProductPortsOnlyGTestBase("ActiveAsyncProductPortsOnlyTester", ActiveAsyncProductPortsOnlyTester::MAX_HISTORY_SIZE),
    component("ActiveAsyncProductPortsOnly")
{
  this->initComponents();
  this->connectPorts();
}

ActiveAsyncProductPortsOnlyTester ::
  ~ActiveAsyncProductPortsOnlyTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveAsyncProductPortsOnlyTester ::
  toDo()
{
  // TODO
}
