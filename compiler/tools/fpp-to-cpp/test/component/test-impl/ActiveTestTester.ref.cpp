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

  // ----------------------------------------------------------------------
  // Handlers for typed from ports
  // ----------------------------------------------------------------------

  void ActiveTestTester ::
    from_noArgsOut_handler(NATIVE_INT_TYPE portNum)
  {
    // TODO
  }

  U32 ActiveTestTester ::
    from_noArgsReturnOut_handler(NATIVE_INT_TYPE portNum)
  {
    // TODO return
  }

  void ActiveTestTester ::
    from_typedOut_handler(
        NATIVE_INT_TYPE portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Ports::TypedPortStrings::StringSize80& str1,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO
  }

  F32 ActiveTestTester ::
    from_typedReturnOut_handler(
        NATIVE_INT_TYPE portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Ports::TypedReturnPortStrings::StringSize80& str2,
        const E& e,
        const A& a,
        const S& s
    )
  {
    // TODO return
  }

}
