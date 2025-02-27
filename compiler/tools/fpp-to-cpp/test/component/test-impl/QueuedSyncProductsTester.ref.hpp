// ======================================================================
// \title  QueuedSyncProductsTester.hpp
// \author [user name]
// \brief  hpp file for QueuedSyncProducts component test harness implementation class
// ======================================================================

#ifndef QueuedSyncProductsTester_HPP
#define QueuedSyncProductsTester_HPP

#include "QueuedSyncProductsGTestBase.hpp"
#include "QueuedSyncProducts.hpp"

class QueuedSyncProductsTester final :
  public QueuedSyncProductsGTestBase
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

    //! Construct object QueuedSyncProductsTester
    QueuedSyncProductsTester();

    //! Destroy object QueuedSyncProductsTester
    ~QueuedSyncProductsTester();

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
    QueuedSyncProducts component;

};

#endif
