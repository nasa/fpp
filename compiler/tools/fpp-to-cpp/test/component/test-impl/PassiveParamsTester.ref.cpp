// ======================================================================
// \title  PassiveParamsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveParams component test harness implementation class
// ======================================================================

#include "PassiveParamsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveParamsTester ::
  PassiveParamsTester() :
    PassiveParamsGTestBase("PassiveParamsTester", PassiveParamsTester::MAX_HISTORY_SIZE),
    component("PassiveParams")
{
  this->initComponents();
  this->connectPorts();
}

PassiveParamsTester ::
  ~PassiveParamsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveParamsTester ::
  toDo()
{
  // TODO
}
