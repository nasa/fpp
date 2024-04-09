// ======================================================================
// \title  PassiveTestTester.cpp
// \author [user name]
// \brief  cpp file for PassiveTest component test harness implementation class
// ======================================================================

#include "PassiveTestTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveTestTester ::
  PassiveTestTester() :
    PassiveTestGTestBase("PassiveTestTester", PassiveTestTester::MAX_HISTORY_SIZE),
    component("PassiveTest")
{
  this->initComponents();
  this->connectPorts();
}

PassiveTestTester ::
  ~PassiveTestTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveTestTester ::
  toDo()
{
  // TODO
}
