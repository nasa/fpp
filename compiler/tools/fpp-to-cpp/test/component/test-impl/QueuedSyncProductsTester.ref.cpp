// ======================================================================
// \title  QueuedSyncProductsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedSyncProducts component test harness implementation class
// ======================================================================

#include "QueuedSyncProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedSyncProductsTester ::
  QueuedSyncProductsTester() :
    QueuedSyncProductsGTestBase("QueuedSyncProductsTester", QueuedSyncProductsTester::MAX_HISTORY_SIZE),
    component("QueuedSyncProducts")
{
  this->initComponents();
  this->connectPorts();
}

QueuedSyncProductsTester ::
  ~QueuedSyncProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedSyncProductsTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void QueuedSyncProductsTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedSyncProductsTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  // TODO return
}

void QueuedSyncProductsTester ::
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

F32 QueuedSyncProductsTester ::
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
