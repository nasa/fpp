// ======================================================================
// \title  SmInitialQueued.hpp
// \author [user name]
// \brief  hpp file for SmInitialQueued component implementation class
// ======================================================================

#ifndef FppTest_SmInitialQueued_HPP
#define FppTest_SmInitialQueued_HPP

#include "SmInitialQueuedComponentAc.hpp"

namespace FppTest {

  class SmInitialQueued :
    public SmInitialQueuedComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct SmInitialQueued object
      SmInitialQueued(
          const char* const compName //!< The component name
      );

      //! Destroy SmInitialQueued object
      ~SmInitialQueued();

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine actions
      // ----------------------------------------------------------------------

      //! Implementation for action a of state machine FppTest_SmInitial_Basic
      //!
      //! Action a
      void FppTest_SmInitial_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitial_Junction
      //!
      //! Action a
      void FppTest_SmInitial_Junction_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Junction::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitial_Nested
      //!
      //! Action a
      void FppTest_SmInitial_Nested_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Nested::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitialQueued_Basic
      //!
      //! Action a
      void FppTest_SmInitialQueued_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialQueued_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitialQueued_Junction
      //!
      //! Action a
      void FppTest_SmInitialQueued_Junction_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialQueued_Junction::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitialQueued_Nested
      //!
      //! Action a
      void FppTest_SmInitialQueued_Nested_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialQueued_Nested::Signal signal //!< The signal
      ) override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine guards
      // ----------------------------------------------------------------------

      //! Implementation for guard g of state machine FppTest_SmInitial_Junction
      //!
      //! Guard g
      bool FppTest_SmInitial_Junction_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Junction::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmInitialQueued_Junction
      //!
      //! Guard g
      bool FppTest_SmInitialQueued_Junction_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmInitialQueued_Junction::Signal signal //!< The signal
      ) const override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Overflow hook implementations for internal state machines
      // ----------------------------------------------------------------------

      //! Overflow hook implementation for smInitialNested
      void smInitialNested_stateMachineOverflowHook(
          SmId smId, //!< The state machine ID
          FwEnumStoreType signal, //!< The signal
          Fw::SerializeBufferBase& buffer //!< The message buffer
      ) override;

  };

}

#endif
