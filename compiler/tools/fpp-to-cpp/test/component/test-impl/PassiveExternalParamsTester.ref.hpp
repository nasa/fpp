// ======================================================================
// \title  PassiveExternalParamsTester.hpp
// \author [user name]
// \brief  hpp file for PassiveExternalParams component test harness implementation class
// ======================================================================

#ifndef PassiveExternalParamsTester_HPP
#define PassiveExternalParamsTester_HPP

#include "PassiveExternalParamsGTestBase.hpp"
#include "PassiveExternalParams.hpp"

class PassiveExternalParamsTester final :
  public PassiveExternalParamsGTestBase
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

    //! Construct object PassiveExternalParamsTester
    PassiveExternalParamsTester();

    //! Destroy object PassiveExternalParamsTester
    ~PassiveExternalParamsTester();

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
    PassiveExternalParams component;

};

#endif
