// ======================================================================
// \title  PassiveSyncProductPortsOnlyTester.cpp
// \author [user name]
// \brief  cpp file for PassiveSyncProductPortsOnly component test harness implementation class
// ======================================================================

#include "PassiveSyncProductPortsOnlyTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveSyncProductPortsOnlyTester ::
  PassiveSyncProductPortsOnlyTester() :
    PassiveSyncProductPortsOnlyGTestBase("PassiveSyncProductPortsOnlyTester", PassiveSyncProductPortsOnlyTester::MAX_HISTORY_SIZE),
    component("PassiveSyncProductPortsOnly")
{
  this->initComponents();
  this->connectPorts();
}

PassiveSyncProductPortsOnlyTester ::
  ~PassiveSyncProductPortsOnlyTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveSyncProductPortsOnlyTester ::
  toDo()
{
  // TODO
}
