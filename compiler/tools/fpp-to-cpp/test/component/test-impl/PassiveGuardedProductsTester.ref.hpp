// ======================================================================
// \title  PassiveGuardedProductsTester.hpp
// \author [user name]
// \brief  hpp file for PassiveGuardedProducts component test harness implementation class
// ======================================================================

#ifndef PassiveGuardedProductsTester_HPP
#define PassiveGuardedProductsTester_HPP

#include "PassiveGuardedProductsGTestBase.hpp"
#include "PassiveGuardedProducts.hpp"

class PassiveGuardedProductsTester final :
  public PassiveGuardedProductsGTestBase
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

    //! Construct object PassiveGuardedProductsTester
    PassiveGuardedProductsTester();

    //! Destroy object PassiveGuardedProductsTester
    ~PassiveGuardedProductsTester();

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
    PassiveGuardedProducts component;

};

#endif
