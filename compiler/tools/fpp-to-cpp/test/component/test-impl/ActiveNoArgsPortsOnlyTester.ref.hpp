// ======================================================================
// \title  ActiveNoArgsPortsOnlyTester.hpp
// \author [user name]
// \brief  hpp file for ActiveNoArgsPortsOnly component test harness implementation class
// ======================================================================

#ifndef ActiveNoArgsPortsOnlyTester_HPP
#define ActiveNoArgsPortsOnlyTester_HPP

#include "ActiveNoArgsPortsOnlyGTestBase.hpp"
#include "ActiveNoArgsPortsOnly.hpp"

class ActiveNoArgsPortsOnlyTester :
  public ActiveNoArgsPortsOnlyGTestBase
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    // Maximum size of histories storing events, telemetry, and port outputs
    static const NATIVE_INT_TYPE MAX_HISTORY_SIZE = 10;

    // Instance ID supplied to the component instance under test
    static const NATIVE_INT_TYPE TEST_INSTANCE_ID = 0;

    // Queue depth supplied to the component instance under test
    static const NATIVE_INT_TYPE TEST_INSTANCE_QUEUE_DEPTH = 10;

  public:

    // ----------------------------------------------------------------------
    // Construction and destruction
    // ----------------------------------------------------------------------

    //! Construct object ActiveNoArgsPortsOnlyTester
    ActiveNoArgsPortsOnlyTester();

    //! Destroy object ActiveNoArgsPortsOnlyTester
    ~ActiveNoArgsPortsOnlyTester();

  public:

    // ----------------------------------------------------------------------
    // Tests
    // ----------------------------------------------------------------------

    //! To do
    void toDo();

  private:

    // ----------------------------------------------------------------------
    // Handlers for typed from ports
    // ----------------------------------------------------------------------

    //! Handler implementation for noArgsOut
    void from_noArgsOut_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsReturnOut
    U32 from_noArgsReturnOut_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

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
    ActiveNoArgsPortsOnly component;

};

#endif
