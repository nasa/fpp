// ======================================================================
// \title  PassiveTelemetryTester.cpp
// \author [user name]
// \brief  cpp file for PassiveTelemetry component test harness implementation class
// ======================================================================

#include "PassiveTelemetryTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveTelemetryTester ::
  PassiveTelemetryTester() :
    PassiveTelemetryGTestBase("PassiveTelemetryTester", PassiveTelemetryTester::MAX_HISTORY_SIZE),
    component("PassiveTelemetry")
{
  this->initComponents();
  this->connectPorts();
}

PassiveTelemetryTester ::
  ~PassiveTelemetryTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveTelemetryTester ::
  toDo()
{
  // TODO
}
