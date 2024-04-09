// ======================================================================
// \title  ActiveEventsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveEvents component test harness implementation class
// ======================================================================

#include "ActiveEventsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveEventsTester ::
  ActiveEventsTester() :
    ActiveEventsGTestBase("ActiveEventsTester", ActiveEventsTester::MAX_HISTORY_SIZE),
    component("ActiveEvents")
{
  this->initComponents();
  this->connectPorts();
}

ActiveEventsTester ::
  ~ActiveEventsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveEventsTester ::
  toDo()
{
  // TODO
}
