// ======================================================================
// \title  QueuedParamsTester.hpp
// \author [user name]
// \brief  hpp file for QueuedParams component test harness implementation class
// ======================================================================

#ifndef QueuedParamsTester_HPP
#define QueuedParamsTester_HPP

#include "QueuedParamsGTestBase.hpp"
#include "QueuedParams.hpp"

class QueuedParamsTester final :
  public QueuedParamsGTestBase
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

    //! Construct object QueuedParamsTester
    QueuedParamsTester();

    //! Destroy object QueuedParamsTester
    ~QueuedParamsTester();

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
    QueuedParams component;

};

#endif
