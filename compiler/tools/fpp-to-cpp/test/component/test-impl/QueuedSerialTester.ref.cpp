// ======================================================================
// \title  QueuedSerialTester.cpp
// \author [user name]
// \brief  cpp file for QueuedSerial component test harness implementation class
// ======================================================================

#include "QueuedSerialTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedSerialTester ::
  QueuedSerialTester() :
    QueuedSerialGTestBase("QueuedSerialTester", QueuedSerialTester::MAX_HISTORY_SIZE),
    component("QueuedSerial")
{
  this->initComponents();
  this->connectPorts();
}

QueuedSerialTester ::
  ~QueuedSerialTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedSerialTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void QueuedSerialTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedSerialTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedSerialTester ::
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
  // TODO
}

F32 QueuedSerialTester ::
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
  // TODO return
}

// ----------------------------------------------------------------------
// Handlers for serial from ports
// ----------------------------------------------------------------------

void QueuedSerialTester ::
  from_serialOut_handler(
      FwIndexType portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // TODO
}
