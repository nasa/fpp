// ======================================================================
// \title  ActiveExternalParamsTester.cpp
// \author [user name]
// \brief  cpp file for ActiveExternalParams component test harness implementation class
// ======================================================================

#include "ActiveExternalParamsTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveExternalParamsTester ::
  ActiveExternalParamsTester() :
    ActiveExternalParamsGTestBase("ActiveExternalParamsTester", ActiveExternalParamsTester::MAX_HISTORY_SIZE),
    component("ActiveExternalParams")
{
  this->initComponents();
  this->connectPorts();
  this->component.registerExternalParameters(&this->paramTesterDelegate);
}

ActiveExternalParamsTester ::
  ~ActiveExternalParamsTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveExternalParamsTester ::
  toDo()
{
  // TODO
}
