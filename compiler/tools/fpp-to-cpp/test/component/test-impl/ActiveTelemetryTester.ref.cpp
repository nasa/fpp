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

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void ActiveTelemetryTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 ActiveTelemetryTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  return 0;
}

void ActiveTelemetryTester ::
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

F32 ActiveTelemetryTester ::
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
