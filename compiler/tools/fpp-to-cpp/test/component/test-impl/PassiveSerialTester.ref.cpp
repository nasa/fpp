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
  from_noArgsOut_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

U32 PassiveSerialTester ::
  from_noArgsReturnOut_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}

void PassiveSerialTester ::
  from_typedOut_handler(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Ports::TypedPortStrings::StringSize80& str1,
      const E& e,
      const A& a,
      const S& s
  )
{
  // TODO
}

F32 PassiveSerialTester ::
  from_typedReturnOut_handler(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const Ports::TypedReturnPortStrings::StringSize80& str2,
      const E& e,
      const A& a,
      const S& s
  )
{
  // TODO return
}

// ----------------------------------------------------------------------
// Handlers for serial from ports
// ----------------------------------------------------------------------

void PassiveSerialTester ::
  from_serialOut_handler(
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}
