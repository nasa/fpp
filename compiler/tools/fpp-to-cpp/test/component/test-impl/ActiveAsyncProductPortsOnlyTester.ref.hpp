// ======================================================================
// \title  ActiveAsyncProductPortsOnlyTester.hpp
// \author [user name]
// \brief  hpp file for ActiveAsyncProductPortsOnly component test harness implementation class
// ======================================================================

#ifndef ActiveAsyncProductPortsOnlyTester_HPP
#define ActiveAsyncProductPortsOnlyTester_HPP

#include "ActiveAsyncProductPortsOnlyGTestBase.hpp"
#include "ActiveAsyncProductPortsOnly.hpp"

class ActiveAsyncProductPortsOnlyTester :
  public ActiveAsyncProductPortsOnlyGTestBase
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

    //! Construct object ActiveAsyncProductPortsOnlyTester
    ActiveAsyncProductPortsOnlyTester();

    //! Destroy object ActiveAsyncProductPortsOnlyTester
    ~ActiveAsyncProductPortsOnlyTester();

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
    ActiveAsyncProductPortsOnly component;

};

#endif
