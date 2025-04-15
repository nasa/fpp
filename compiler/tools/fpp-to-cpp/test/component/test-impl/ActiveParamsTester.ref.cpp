// ======================================================================
// \title  ActiveParamsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveParams component test harness implementation class
// ======================================================================

#include "ActiveParamsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveParamsTester ::
  ActiveParamsTester() :
    ActiveParamsGTestBase("ActiveParamsTester", ActiveParamsTester::MAX_HISTORY_SIZE),
    component("ActiveParams")
{
  this->initComponents();
  this->connectPorts();
}

ActiveParamsTester ::
  ~ActiveParamsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveParamsTester ::
  toDo()
{
  // TODO
}
