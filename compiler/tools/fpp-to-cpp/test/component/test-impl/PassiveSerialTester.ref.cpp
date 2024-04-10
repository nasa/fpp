// ======================================================================
// \title  PassiveSerialTester.cpp
// \author [user name]
// \brief  cpp file for PassiveSerial component test harness implementation class
// ======================================================================

#include "PassiveSerialTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveSerialTester ::
  PassiveSerialTester() :
    PassiveSerialGTestBase("PassiveSerialTester", PassiveSerialTester::MAX_HISTORY_SIZE),
    component("PassiveSerial")
{
  this->initComponents();
  this->connectPorts();
}

PassiveSerialTester ::
  ~PassiveSerialTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveSerialTester ::
  toDo()
{
  // TODO
}
