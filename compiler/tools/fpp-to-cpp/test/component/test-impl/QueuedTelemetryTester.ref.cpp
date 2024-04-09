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

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void QueuedTelemetryTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 QueuedTelemetryTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  return 0;
}

void QueuedTelemetryTester ::
  from_typedOut_handler(
      FwIndexType portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Ports::TypedPortStrings::StringSize80& str1,
      const E& e,
      const A& a,
      const S& s
  )
{
  this->pushFromPortEntry_typedOut(u32, f32, b, str1, e, a, s);
}

F32 QueuedTelemetryTester ::
  from_typedReturnOut_handler(
      FwIndexType portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Ports::TypedReturnPortStrings::StringSize80& str2,
      const E& e,
      const A& a,
      const S& s
  )
{
  this->pushFromPortEntry_typedReturnOut(u32, f32, b, str2, e, a, s);
  return 0.0f;
}
