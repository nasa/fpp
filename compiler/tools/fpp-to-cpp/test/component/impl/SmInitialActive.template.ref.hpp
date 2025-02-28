// ======================================================================
// \title  SmInitialActive.hpp
// \author [user name]
// \brief  hpp file for SmInitialActive component implementation class
// ======================================================================

#ifndef FppTest_SmInitialActive_HPP
#define FppTest_SmInitialActive_HPP

#include "SmInitialActiveComponentAc.hpp"

namespace FppTest {

  class SmInitialActive final :
    public SmInitialActiveComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct SmInitialActive object
      SmInitialActive(
          const char* const compName //!< The component name
      );

      //! Destroy SmInitialActive object
      ~SmInitialActive();

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

      //! Implementation for action a of state machine FppTest_SmInitial_Choice
      //!
      //! Action a
      void FppTest_SmInitial_Choice_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Choice::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitial_Nested
      //!
      //! Action a
      void FppTest_SmInitial_Nested_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Nested::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitialActive_Basic
      //!
      //! Action a
      void FppTest_SmInitialActive_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitialActive_Choice
      //!
      //! Action a
      void FppTest_SmInitialActive_Choice_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Choice::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmInitialActive_Nested
      //!
      //! Action a
      void FppTest_SmInitialActive_Nested_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Nested::Signal signal //!< The signal
      ) override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine guards
      // ----------------------------------------------------------------------

      //! Implementation for guard g of state machine FppTest_SmInitial_Choice
      //!
      //! Guard g
      bool FppTest_SmInitial_Choice_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Choice::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmInitialActive_Choice
      //!
      //! Guard g
      bool FppTest_SmInitialActive_Choice_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Choice::Signal signal //!< The signal
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
