// ======================================================================
// \title  PassiveTestTester.hpp
// \author [user name]
// \brief  hpp file for PassiveTest component test harness implementation class
// ======================================================================

#ifndef PassiveTestTester_HPP
#define PassiveTestTester_HPP

#include "PassiveTestGTestBase.hpp"
#include "PassiveTest.hpp"

class PassiveTestTester final :
  public PassiveTestGTestBase
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    // Maximum size of histories storing events, telemetry, and port outputs
    static const FwSizeType MAX_HISTORY_SIZE = 10;

    // Instance ID supplied to the component instance under test
    static const FwEnumStoreType TEST_INSTANCE_ID = 0;

  public:

    // ----------------------------------------------------------------------
    // Construction and destruction
    // ----------------------------------------------------------------------

    //! Construct object PassiveTestTester
    PassiveTestTester();

    //! Destroy object PassiveTestTester
    ~PassiveTestTester();

  public:

    // ----------------------------------------------------------------------
    // Tests
    // ----------------------------------------------------------------------

    //! To do
    void toDo();

  private:

    // ----------------------------------------------------------------------
    // Helper functions
    // ----------------------------------------------------------------------

    //! Connect ports
    void connectPorts();

    //! Initialize components
    void initComponents();

  private:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    //! The component under test
    PassiveTest component;

};

#endif
