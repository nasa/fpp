// ======================================================================
// \title  ActiveEventsTester.hpp
// \author [user name]
// \brief  hpp file for ActiveEvents component test harness implementation class
// ======================================================================

#ifndef ActiveEventsTester_HPP
#define ActiveEventsTester_HPP

#include "ActiveEventsGTestBase.hpp"
#include "ActiveEvents.hpp"

class ActiveEventsTester final :
  public ActiveEventsGTestBase
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

    //! Construct object ActiveEventsTester
    ActiveEventsTester();

    //! Destroy object ActiveEventsTester
    ~ActiveEventsTester();

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
    ActiveEvents component;

};

#endif
