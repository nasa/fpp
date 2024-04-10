// ======================================================================
// \title  QueuedEventsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedEvents component test harness implementation class
// ======================================================================

#include "QueuedEventsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedEventsTester ::
  QueuedEventsTester() :
    QueuedEventsGTestBase("QueuedEventsTester", QueuedEventsTester::MAX_HISTORY_SIZE),
    component("QueuedEvents")
{
  this->initComponents();
  this->connectPorts();
}

QueuedEventsTester ::
  ~QueuedEventsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedEventsTester ::
  toDo()
{
  // TODO
}
