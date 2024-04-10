// ======================================================================
// \title  PassiveSyncProductsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveSyncProducts component test harness implementation class
// ======================================================================

#include "PassiveSyncProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveSyncProductsTester ::
  PassiveSyncProductsTester() :
    PassiveSyncProductsGTestBase("PassiveSyncProductsTester", PassiveSyncProductsTester::MAX_HISTORY_SIZE),
    component("PassiveSyncProducts")
{
  this->initComponents();
  this->connectPorts();
}

PassiveSyncProductsTester ::
  ~PassiveSyncProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveSyncProductsTester ::
  toDo()
{
  // TODO
}
