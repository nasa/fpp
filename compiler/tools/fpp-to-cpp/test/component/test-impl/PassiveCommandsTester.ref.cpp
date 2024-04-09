// ======================================================================
// \title  PassiveCommandsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveCommands component test harness implementation class
// ======================================================================

#include "PassiveCommandsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveCommandsTester ::
  PassiveCommandsTester() :
    PassiveCommandsGTestBase("PassiveCommandsTester", PassiveCommandsTester::MAX_HISTORY_SIZE),
    component("PassiveCommands")
{
  this->initComponents();
  this->connectPorts();
}

PassiveCommandsTester ::
  ~PassiveCommandsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveCommandsTester ::
  toDo()
{
  // TODO
}
