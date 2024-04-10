// ======================================================================
// \title  ActiveSerialTester.cpp
// \author [user name]
// \brief  cpp file for ActiveSerial component test harness implementation class
// ======================================================================

#include "ActiveSerialTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveSerialTester ::
  ActiveSerialTester() :
    ActiveSerialGTestBase("ActiveSerialTester", ActiveSerialTester::MAX_HISTORY_SIZE),
    component("ActiveSerial")
{
  this->initComponents();
  this->connectPorts();
}

ActiveSerialTester ::
  ~ActiveSerialTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveSerialTester ::
  toDo()
{
  // TODO
}
