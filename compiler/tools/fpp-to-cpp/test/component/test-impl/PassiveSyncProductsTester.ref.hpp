// ======================================================================
// \title  PassiveSyncProductsTester.hpp
// \author [user name]
// \brief  hpp file for PassiveSyncProducts component test harness implementation class
// ======================================================================

#ifndef PassiveSyncProductsTester_HPP
#define PassiveSyncProductsTester_HPP

#include "PassiveSyncProductsGTestBase.hpp"
#include "PassiveSyncProducts.hpp"

class PassiveSyncProductsTester final :
  public PassiveSyncProductsGTestBase
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

    //! Construct object PassiveSyncProductsTester
    PassiveSyncProductsTester();

    //! Destroy object PassiveSyncProductsTester
    ~PassiveSyncProductsTester();

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
    PassiveSyncProducts component;

};

#endif
