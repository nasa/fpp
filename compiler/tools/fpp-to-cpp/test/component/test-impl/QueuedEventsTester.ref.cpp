// ======================================================================
// \title  QueuedEventsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedEvents component test harness implementation class
// ======================================================================

#include "QueuedEventsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedEventsTester ::
  QueuedEventsTester() :
    QueuedEventsGTestBase("QueuedEventsTester", QueuedEventsTester::MAX_HISTORY_SIZE),
    component("QueuedEvents")
{
  this->initComponents();
  this->connectPorts();
}

QueuedEventsTester ::
  ~QueuedEventsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedEventsTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void QueuedEventsTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedEventsTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedEventsTester ::
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

F32 QueuedEventsTester ::
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
