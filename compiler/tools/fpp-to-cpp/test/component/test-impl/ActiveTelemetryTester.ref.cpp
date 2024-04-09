// ======================================================================
// \title  ActiveTelemetryTester.cpp
// \author [user name]
// \brief  cpp file for ActiveTelemetry component test harness implementation class
// ======================================================================

#include "ActiveTelemetryTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveTelemetryTester ::
  ActiveTelemetryTester() :
    ActiveTelemetryGTestBase("ActiveTelemetryTester", ActiveTelemetryTester::MAX_HISTORY_SIZE),
    component("ActiveTelemetry")
{
  this->initComponents();
  this->connectPorts();
}

ActiveTelemetryTester ::
  ~ActiveTelemetryTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveTelemetryTester ::
  toDo()
{
  // TODO
}
