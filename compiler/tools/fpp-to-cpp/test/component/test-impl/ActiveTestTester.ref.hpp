// ======================================================================
// \title  ActiveTestTester.hpp
// \author [user name]
// \brief  hpp file for ActiveTest component test harness implementation class
// ======================================================================

#ifndef M_ActiveTestTester_HPP
#define M_ActiveTestTester_HPP

#include "ActiveTestGTestBase.hpp"
#include "ActiveTest.hpp"

namespace M {

  class ActiveTestTester final :
    public ActiveTestGTestBase
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

      //! Construct object ActiveTestTester
      ActiveTestTester();

      //! Destroy object ActiveTestTester
      ~ActiveTestTester();

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
      ActiveTest component;

  };

}

#endif
