// ======================================================================
// \title  QueuedTestTester.cpp
// \author [user name]
// \brief  cpp file for QueuedTest component test harness implementation class
// ======================================================================

#include "QueuedTestTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedTestTester ::
  QueuedTestTester() :
    QueuedTestGTestBase("QueuedTestTester", QueuedTestTester::MAX_HISTORY_SIZE),
    component("QueuedTest")
{
  this->initComponents();
  this->connectPorts();
}

QueuedTestTester ::
  ~QueuedTestTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedTestTester ::
  toDo()
{
  // TODO
}
