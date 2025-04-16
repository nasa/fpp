// ======================================================================
// \title  QueuedExternalParamsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedExternalParams component test harness implementation class
// ======================================================================

#include "QueuedExternalParamsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedExternalParamsTester ::
  QueuedExternalParamsTester() :
    QueuedExternalParamsGTestBase("QueuedExternalParamsTester", QueuedExternalParamsTester::MAX_HISTORY_SIZE),
    component("QueuedExternalParams")
{
  this->initComponents();
  this->connectPorts();
  this->component.registerExternalParameters(&this->paramTesterDelegate);
}

QueuedExternalParamsTester ::
  ~QueuedExternalParamsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedExternalParamsTester ::
  toDo()
{
  // TODO
}
