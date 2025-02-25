// ======================================================================
// \title  ActiveSerialTester.hpp
// \author [user name]
// \brief  hpp file for ActiveSerial component test harness implementation class
// ======================================================================

#ifndef ActiveSerialTester_HPP
#define ActiveSerialTester_HPP

#include "ActiveSerialGTestBase.hpp"
#include "ActiveSerial.hpp"

class ActiveSerialTester final :
  public ActiveSerialGTestBase
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    // Maximum size of histories storing events, telemetry, and port outputs
    static const FwSizeType MAX_HISTORY_SIZE = 10;

    // Instance ID supplied to the component instance under test
    static const FwEnumStoreType TEST_INSTANCE_ID = 0;

    // Queue depth supplied to the component instance under test
    static const FwSizeType TEST_INSTANCE_QUEUE_DEPTH = 10;

  public:

    // ----------------------------------------------------------------------
    // Construction and destruction
    // ----------------------------------------------------------------------

    //! Construct object ActiveSerialTester
    ActiveSerialTester();

    //! Destroy object ActiveSerialTester
    ~ActiveSerialTester();

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
    ActiveSerial component;

};

#endif
