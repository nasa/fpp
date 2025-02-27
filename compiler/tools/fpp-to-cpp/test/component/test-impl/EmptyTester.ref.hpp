// ======================================================================
// \title  EmptyTester.hpp
// \author [user name]
// \brief  hpp file for Empty component test harness implementation class
// ======================================================================

#ifndef EmptyTester_HPP
#define EmptyTester_HPP

#include "EmptyGTestBase.hpp"
#include "Empty.hpp"

class EmptyTester final :
  public EmptyGTestBase
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

    //! Construct object EmptyTester
    EmptyTester();

    //! Destroy object EmptyTester
    ~EmptyTester();

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
    Empty component;

};

#endif
