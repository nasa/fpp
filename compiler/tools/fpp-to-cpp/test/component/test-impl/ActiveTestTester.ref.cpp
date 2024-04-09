// ======================================================================
// \title  ActiveTestTester.cpp
// \author [user name]
// \brief  cpp file for ActiveTest component test harness implementation class
// ======================================================================

#include "ActiveTestTester.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Construction and destruction
  // ----------------------------------------------------------------------

  ActiveTestTester ::
    ActiveTestTester() :
      ActiveTestGTestBase("ActiveTestTester", ActiveTestTester::MAX_HISTORY_SIZE),
      component("ActiveTest")
  {
    this->initComponents();
    this->connectPorts();
  }

  ActiveTestTester ::
    ~ActiveTestTester()
  {

  }

  // ----------------------------------------------------------------------
  // Tests
  // ----------------------------------------------------------------------

  void ActiveTestTester ::
    toDo()
  {
    // TODO
  }

}
