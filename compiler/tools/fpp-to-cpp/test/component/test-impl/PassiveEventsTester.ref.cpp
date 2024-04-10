// ======================================================================
// \title  PassiveEventsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveEvents component test harness implementation class
// ======================================================================

#include "PassiveEventsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveEventsTester ::
  PassiveEventsTester() :
    PassiveEventsGTestBase("PassiveEventsTester", PassiveEventsTester::MAX_HISTORY_SIZE),
    component("PassiveEvents")
{
  this->initComponents();
  this->connectPorts();
}

PassiveEventsTester ::
  ~PassiveEventsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveEventsTester ::
  toDo()
{
  // TODO
}
