// ======================================================================
// \title  PassiveGetProductsTester.hpp
// \author [user name]
// \brief  hpp file for PassiveGetProducts component test harness implementation class
// ======================================================================

#ifndef PassiveGetProductsTester_HPP
#define PassiveGetProductsTester_HPP

#include "PassiveGetProductsGTestBase.hpp"
#include "PassiveGetProducts.hpp"

class PassiveGetProductsTester final :
  public PassiveGetProductsGTestBase
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

    //! Construct object PassiveGetProductsTester
    PassiveGetProductsTester();

    //! Destroy object PassiveGetProductsTester
    ~PassiveGetProductsTester();

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
    PassiveGetProducts component;

};

#endif
