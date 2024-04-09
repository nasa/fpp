// ======================================================================
// \title  QueuedSerialTester.cpp
// \author [user name]
// \brief  cpp file for QueuedSerial component test harness implementation class
// ======================================================================

#include "QueuedSerialTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedSerialTester ::
  QueuedSerialTester() :
    QueuedSerialGTestBase("QueuedSerialTester", QueuedSerialTester::MAX_HISTORY_SIZE),
    component("QueuedSerial")
{
  this->initComponents();
  this->connectPorts();
}

QueuedSerialTester ::
  ~QueuedSerialTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedSerialTester ::
  toDo()
{
  // TODO
}
