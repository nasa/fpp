// ======================================================================
// \title  QueuedGetProductsTester.hpp
// \author [user name]
// \brief  hpp file for QueuedGetProducts component test harness implementation class
// ======================================================================

#ifndef QueuedGetProductsTester_HPP
#define QueuedGetProductsTester_HPP

#include "QueuedGetProductsGTestBase.hpp"
#include "QueuedGetProducts.hpp"

class QueuedGetProductsTester final :
  public QueuedGetProductsGTestBase
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

    //! Construct object QueuedGetProductsTester
    QueuedGetProductsTester();

    //! Destroy object QueuedGetProductsTester
    ~QueuedGetProductsTester();

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
    QueuedGetProducts component;

};

#endif
