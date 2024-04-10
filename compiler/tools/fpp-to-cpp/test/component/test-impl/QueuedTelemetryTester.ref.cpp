// ======================================================================
// \title  QueuedTelemetryTester.cpp
// \author [user name]
// \brief  cpp file for QueuedTelemetry component test harness implementation class
// ======================================================================

#include "QueuedTelemetryTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedTelemetryTester ::
  QueuedTelemetryTester() :
    QueuedTelemetryGTestBase("QueuedTelemetryTester", QueuedTelemetryTester::MAX_HISTORY_SIZE),
    component("QueuedTelemetry")
{
  this->initComponents();
  this->connectPorts();
}

QueuedTelemetryTester ::
  ~QueuedTelemetryTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedTelemetryTester ::
  toDo()
{
  // TODO
}
