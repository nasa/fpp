// ======================================================================
// \title  ActiveSyncProductsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveSyncProducts component test harness implementation class
// ======================================================================

#include "ActiveSyncProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveSyncProductsTester ::
  ActiveSyncProductsTester() :
    ActiveSyncProductsGTestBase("ActiveSyncProductsTester", ActiveSyncProductsTester::MAX_HISTORY_SIZE),
    component("ActiveSyncProducts")
{
  this->initComponents();
  this->connectPorts();
}

ActiveSyncProductsTester ::
  ~ActiveSyncProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveSyncProductsTester ::
  toDo()
{
  // TODO
}
