// ======================================================================
// \title  PassiveGetProductPortsOnlyTester.hpp
// \author [user name]
// \brief  hpp file for PassiveGetProductPortsOnly component test harness implementation class
// ======================================================================

#ifndef PassiveGetProductPortsOnlyTester_HPP
#define PassiveGetProductPortsOnlyTester_HPP

#include "PassiveGetProductPortsOnlyGTestBase.hpp"
#include "PassiveGetProductPortsOnly.hpp"

class PassiveGetProductPortsOnlyTester final :
  public PassiveGetProductPortsOnlyGTestBase
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

    //! Construct object PassiveGetProductPortsOnlyTester
    PassiveGetProductPortsOnlyTester();

    //! Destroy object PassiveGetProductPortsOnlyTester
    ~PassiveGetProductPortsOnlyTester();

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
    PassiveGetProductPortsOnly component;

};

#endif
