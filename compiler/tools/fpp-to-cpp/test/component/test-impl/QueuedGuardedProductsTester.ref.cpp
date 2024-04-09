// ======================================================================
// \title  QueuedGuardedProductsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedGuardedProducts component test harness implementation class
// ======================================================================

#include "QueuedGuardedProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedGuardedProductsTester ::
  QueuedGuardedProductsTester() :
    QueuedGuardedProductsGTestBase("QueuedGuardedProductsTester", QueuedGuardedProductsTester::MAX_HISTORY_SIZE),
    component("QueuedGuardedProducts")
{
  this->initComponents();
  this->connectPorts();
}

QueuedGuardedProductsTester ::
  ~QueuedGuardedProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedGuardedProductsTester ::
  toDo()
{
  // TODO
}
