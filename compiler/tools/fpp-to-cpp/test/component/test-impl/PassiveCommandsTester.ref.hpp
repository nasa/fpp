// ======================================================================
// \title  PassiveCommandsTester.hpp
// \author [user name]
// \brief  hpp file for PassiveCommands component test harness implementation class
// ======================================================================

#ifndef PassiveCommandsTester_HPP
#define PassiveCommandsTester_HPP

#include "PassiveCommandsGTestBase.hpp"
#include "PassiveCommands.hpp"

class PassiveCommandsTester final :
  public PassiveCommandsGTestBase
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

    //! Construct object PassiveCommandsTester
    PassiveCommandsTester();

    //! Destroy object PassiveCommandsTester
    ~PassiveCommandsTester();

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
    PassiveCommands component;

};

#endif
