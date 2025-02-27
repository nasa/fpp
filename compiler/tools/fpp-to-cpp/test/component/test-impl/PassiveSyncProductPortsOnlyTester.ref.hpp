// ======================================================================
// \title  PassiveSyncProductPortsOnlyTester.hpp
// \author [user name]
// \brief  hpp file for PassiveSyncProductPortsOnly component test harness implementation class
// ======================================================================

#ifndef PassiveSyncProductPortsOnlyTester_HPP
#define PassiveSyncProductPortsOnlyTester_HPP

#include "PassiveSyncProductPortsOnlyGTestBase.hpp"
#include "PassiveSyncProductPortsOnly.hpp"

class PassiveSyncProductPortsOnlyTester final :
  public PassiveSyncProductPortsOnlyGTestBase
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

    //! Construct object PassiveSyncProductPortsOnlyTester
    PassiveSyncProductPortsOnlyTester();

    //! Destroy object PassiveSyncProductPortsOnlyTester
    ~PassiveSyncProductPortsOnlyTester();

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
    PassiveSyncProductPortsOnly component;

};

#endif
