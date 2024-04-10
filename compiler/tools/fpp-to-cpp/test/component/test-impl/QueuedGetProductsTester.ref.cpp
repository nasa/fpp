// ======================================================================
// \title  QueuedGetProductsTester.cpp
// \author [user name]
// \brief  cpp file for QueuedGetProducts component test harness implementation class
// ======================================================================

#include "QueuedGetProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedGetProductsTester ::
  QueuedGetProductsTester() :
    QueuedGetProductsGTestBase("QueuedGetProductsTester", QueuedGetProductsTester::MAX_HISTORY_SIZE),
    component("QueuedGetProducts")
{
  this->initComponents();
  this->connectPorts();
}

QueuedGetProductsTester ::
  ~QueuedGetProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void QueuedGetProductsTester ::
  toDo()
{
  // TODO
}
