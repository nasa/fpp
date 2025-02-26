// ======================================================================
// \title  ActiveParamsTester.hpp
// \author [user name]
// \brief  hpp file for ActiveParams component test harness implementation class
// ======================================================================

#ifndef ActiveParamsTester_HPP
#define ActiveParamsTester_HPP

#include "ActiveParamsGTestBase.hpp"
#include "ActiveParams.hpp"

class ActiveParamsTester final :
  public ActiveParamsGTestBase
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

    //! Construct object ActiveParamsTester
    ActiveParamsTester();

    //! Destroy object ActiveParamsTester
    ~ActiveParamsTester();

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
    ActiveParams component;

};

#endif
