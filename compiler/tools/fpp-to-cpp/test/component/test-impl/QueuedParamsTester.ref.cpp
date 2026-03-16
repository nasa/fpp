// ======================================================================
// \title  QueuedParamsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedParams component test harness implementation class
// ======================================================================

#include "QueuedParamsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedParamsTester ::
  QueuedParamsTester() :
    QueuedParamsGTestBase("QueuedParamsTester", QueuedParamsTester::MAX_HISTORY_SIZE),
    component("QueuedParams")
{
  this->initComponents();
  this->connectPorts();
}

QueuedParamsTester ::
  ~QueuedParamsTester()
{
  this->component.deinit();
}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedParamsTester ::
  toDo()
{
  // TODO
}
