// ======================================================================
// \title  ActiveGetProductsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveGetProducts component test harness implementation class
// ======================================================================

#include "ActiveGetProductsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveGetProductsTester ::
  ActiveGetProductsTester() :
    ActiveGetProductsGTestBase("ActiveGetProductsTester", ActiveGetProductsTester::MAX_HISTORY_SIZE),
    component("ActiveGetProducts")
{
  this->initComponents();
  this->connectPorts();
}

ActiveGetProductsTester ::
  ~ActiveGetProductsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveGetProductsTester ::
  toDo()
{
  // TODO
}
