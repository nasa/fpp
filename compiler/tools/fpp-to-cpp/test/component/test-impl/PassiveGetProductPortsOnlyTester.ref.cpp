// ======================================================================
// \title  PassiveGetProductPortsOnlyTester.cpp
// \author [user name]
// \brief  cpp file for PassiveGetProductPortsOnly component test harness implementation class
// ======================================================================

#include "PassiveGetProductPortsOnlyTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveGetProductPortsOnlyTester ::
  PassiveGetProductPortsOnlyTester() :
    PassiveGetProductPortsOnlyGTestBase("PassiveGetProductPortsOnlyTester", PassiveGetProductPortsOnlyTester::MAX_HISTORY_SIZE),
    component("PassiveGetProductPortsOnly")
{
  this->initComponents();
  this->connectPorts();
}

PassiveGetProductPortsOnlyTester ::
  ~PassiveGetProductPortsOnlyTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveGetProductPortsOnlyTester ::
  toDo()
{
  // TODO
}
