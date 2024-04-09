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
