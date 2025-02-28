// ======================================================================
// \title  PassiveTelemetryTester.hpp
// \author [user name]
// \brief  hpp file for PassiveTelemetry component test harness implementation class
// ======================================================================

#ifndef PassiveTelemetryTester_HPP
#define PassiveTelemetryTester_HPP

#include "PassiveTelemetryGTestBase.hpp"
#include "PassiveTelemetry.hpp"

class PassiveTelemetryTester final :
  public PassiveTelemetryGTestBase
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

    //! Construct object PassiveTelemetryTester
    PassiveTelemetryTester();

    //! Destroy object PassiveTelemetryTester
    ~PassiveTelemetryTester();

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
    PassiveTelemetry component;

};

#endif
