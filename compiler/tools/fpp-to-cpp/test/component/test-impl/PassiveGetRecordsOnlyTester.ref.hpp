// ======================================================================
// \title  PassiveGetRecordsOnlyTester.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for PassiveGetRecordsOnly component test harness implementation class
// ======================================================================

#ifndef PassiveGetRecordsOnlyTester_HPP
#define PassiveGetRecordsOnlyTester_HPP

#include "PassiveGetRecordsOnlyGTestBase.hpp"
#include "PassiveGetRecordsOnly.hpp"

class PassiveGetRecordsOnlyTester :
  public PassiveGetRecordsOnlyGTestBase
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    // Maximum size of histories storing events, telemetry, and port outputs
    static const NATIVE_INT_TYPE MAX_HISTORY_SIZE = 10;

    // Instance ID supplied to the component instance under test
    static const NATIVE_INT_TYPE TEST_INSTANCE_ID = 0;

  public:

    // ----------------------------------------------------------------------
    // Construction and destruction
    // ----------------------------------------------------------------------

    //! Construct object PassiveGetRecordsOnlyTester
    PassiveGetRecordsOnlyTester();

    //! Destroy object PassiveGetRecordsOnlyTester
    ~PassiveGetRecordsOnlyTester();

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
    PassiveGetRecordsOnly component;

};

#endif