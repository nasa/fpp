// ======================================================================
// \title  PassiveSerialTester.cpp
// \author [user name]
// \brief  cpp file for PassiveSerial component test harness implementation class
// ======================================================================

#include "PassiveSerialTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveSerialTester ::
  PassiveSerialTester() :
    PassiveSerialGTestBase("PassiveSerialTester", PassiveSerialTester::MAX_HISTORY_SIZE),
    component("PassiveSerial")
{
  this->initComponents();
  this->connectPorts();
}

PassiveSerialTester ::
  ~PassiveSerialTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveSerialTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void PassiveSerialTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 PassiveSerialTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  return 0;
}

void PassiveSerialTester ::
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

F32 PassiveSerialTester ::
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

// ----------------------------------------------------------------------
// Handlers for serial from ports
// ----------------------------------------------------------------------

void PassiveSerialTester ::
  from_serialOut_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}
