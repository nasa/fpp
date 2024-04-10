// ======================================================================
// \title  ActiveCommandsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveCommands component test harness implementation class
// ======================================================================

#include "ActiveCommandsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveCommandsTester ::
  ActiveCommandsTester() :
    ActiveCommandsGTestBase("ActiveCommandsTester", ActiveCommandsTester::MAX_HISTORY_SIZE),
    component("ActiveCommands")
{
  this->initComponents();
  this->connectPorts();
}

ActiveCommandsTester ::
  ~ActiveCommandsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveCommandsTester ::
  toDo()
{
  // TODO
}
