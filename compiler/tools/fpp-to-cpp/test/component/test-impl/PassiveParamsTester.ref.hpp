// ======================================================================
// \title  PassiveParamsTester.hpp
// \author [user name]
// \brief  hpp file for PassiveParams component test harness implementation class
// ======================================================================

#ifndef PassiveParamsTester_HPP
#define PassiveParamsTester_HPP

#include "PassiveParamsGTestBase.hpp"
#include "PassiveParams.hpp"

class PassiveParamsTester final :
  public PassiveParamsGTestBase
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

    //! Construct object PassiveParamsTester
    PassiveParamsTester();

    //! Destroy object PassiveParamsTester
    ~PassiveParamsTester();

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
    PassiveParams component;

};

#endif
