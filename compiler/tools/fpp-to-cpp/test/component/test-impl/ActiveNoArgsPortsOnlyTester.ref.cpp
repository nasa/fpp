// ======================================================================
// \title  ActiveNoArgsPortsOnlyTester.cpp
// \author [user name]
// \brief  cpp file for ActiveNoArgsPortsOnly component test harness implementation class
// ======================================================================

#include "ActiveNoArgsPortsOnlyTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveNoArgsPortsOnlyTester ::
  ActiveNoArgsPortsOnlyTester() :
    ActiveNoArgsPortsOnlyGTestBase("ActiveNoArgsPortsOnlyTester", ActiveNoArgsPortsOnlyTester::MAX_HISTORY_SIZE),
    component("ActiveNoArgsPortsOnly")
{
  this->initComponents();
  this->connectPorts();
}

ActiveNoArgsPortsOnlyTester ::
  ~ActiveNoArgsPortsOnlyTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveNoArgsPortsOnlyTester ::
  toDo()
{
  // TODO
}
