// ======================================================================
// \title  PassiveSerialTester.hpp
// \author [user name]
// \brief  hpp file for PassiveSerial component test harness implementation class
// ======================================================================

#ifndef PassiveSerialTester_HPP
#define PassiveSerialTester_HPP

#include "PassiveSerialGTestBase.hpp"
#include "PassiveSerial.hpp"

class PassiveSerialTester final :
  public PassiveSerialGTestBase
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

    //! Construct object PassiveSerialTester
    PassiveSerialTester();

    //! Destroy object PassiveSerialTester
    ~PassiveSerialTester();

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
    PassiveSerial component;

};

#endif
