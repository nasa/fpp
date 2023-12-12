// ======================================================================
// \title  EmptyTester.cpp
// \author [user name]
// \brief  cpp file for Empty component test harness implementation class
// ======================================================================

#include "EmptyTester.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

EmptyTester ::
  EmptyTester() :
    EmptyGTestBase("EmptyTester", EmptyTester::MAX_HISTORY_SIZE),
    component("Empty")
{
  this->initComponents();
  this->connectPorts();
}

EmptyTester ::
  ~EmptyTester()
{

}

// ----------------------------------------------------------------------
// Tests
// ----------------------------------------------------------------------

void EmptyTester ::
  toDo()
{
  // TODO
}
