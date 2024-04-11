// ======================================================================
// \title  QueuedCommandsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedCommands component test harness implementation class
// ======================================================================

#include "QueuedCommandsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedCommandsTester ::
  QueuedCommandsTester() :
    QueuedCommandsGTestBase("QueuedCommandsTester", QueuedCommandsTester::MAX_HISTORY_SIZE),
    component("QueuedCommands")
{
  this->initComponents();
  this->connectPorts();
}

QueuedCommandsTester ::
  ~QueuedCommandsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedCommandsTester ::
  toDo()
{
  // TODO
}
