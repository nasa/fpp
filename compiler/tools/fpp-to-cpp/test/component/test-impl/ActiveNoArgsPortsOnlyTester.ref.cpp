// ======================================================================
// \title  ActiveNoArgsPortsOnlyTester.cpp
// \author [user name]
// \brief  cpp file for ActiveNoArgsPortsOnly component test harness implementation class
// ======================================================================

#include "ActiveNoArgsPortsOnlyTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

ActiveNoArgsPortsOnlyTester ::
  ActiveNoArgsPortsOnlyTester() :
    ActiveNoArgsPortsOnlyGTestBase("ActiveNoArgsPortsOnlyTester", ActiveNoArgsPortsOnlyTester::MAX_HISTORY_SIZE),
    component("ActiveNoArgsPortsOnly")
{
  this->initComponents();
  this->connectPorts();
}

ActiveNoArgsPortsOnlyTester ::
  ~ActiveNoArgsPortsOnlyTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void ActiveNoArgsPortsOnlyTester ::
  toDo()
{
  // TODO
}

// ----------------------------------------------------------------------
// Handlers for typed from ports
// ----------------------------------------------------------------------

void ActiveNoArgsPortsOnlyTester ::
  from_noArgsOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsOut();
}

U32 ActiveNoArgsPortsOnlyTester ::
  from_noArgsReturnOut_handler(FwIndexType portNum)
{
  this->pushFromPortEntry_noArgsReturnOut();
  // TODO: Return a value
}
