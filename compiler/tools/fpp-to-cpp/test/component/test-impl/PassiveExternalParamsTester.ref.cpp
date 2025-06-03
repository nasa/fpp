// ======================================================================
// \title  PassiveExternalParamsTester.cpp
// \author [user name]
// \brief  cpp file for PassiveExternalParams component test harness implementation class
// ======================================================================

#include "PassiveExternalParamsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

PassiveExternalParamsTester ::
  PassiveExternalParamsTester() :
    PassiveExternalParamsGTestBase("PassiveExternalParamsTester", PassiveExternalParamsTester::MAX_HISTORY_SIZE),
    component("PassiveExternalParams")
{
  this->initComponents();
  this->connectPorts();
  this->component.registerExternalParameters(&this->paramTesterDelegate);
}

PassiveExternalParamsTester ::
  ~PassiveExternalParamsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void PassiveExternalParamsTester ::
  toDo()
{
  // TODO
}
