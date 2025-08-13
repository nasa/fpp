// ======================================================================
// \title  SmChoiceActive.hpp
// \author [user name]
// \brief  hpp file for SmChoiceActive component implementation class
// ======================================================================

#ifndef FppTest_SmChoiceActive_HPP
#define FppTest_SmChoiceActive_HPP

#include "SmChoiceActiveComponentAc.hpp"

namespace FppTest {

  class SmChoiceActive final :
    public SmChoiceActiveComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct SmChoiceActive object
      SmChoiceActive(
          const char* const compName //!< The component name
      );

      //! Destroy SmChoiceActive object
      ~SmChoiceActive();

    private:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine actions
      // ----------------------------------------------------------------------

      //! Implementation for action a of state machine FppTest_SmChoice_Basic
      //!
      //! Action a
      void FppTest_SmChoice_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmChoice_Basic
      //!
      //! Action b
      void FppTest_SmChoice_Basic_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoice_BasicU32
      //!
      //! Action a
      void FppTest_SmChoice_BasicU32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_BasicU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action b of state machine FppTest_SmChoice_BasicU32
      //!
      //! Action b
      void FppTest_SmChoice_BasicU32_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_BasicU32::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmChoice_ChoiceToChoice
      //!
      //! Exit S1
      void FppTest_SmChoice_ChoiceToChoice_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoice_ChoiceToChoice
      //!
      //! Action a
      void FppTest_SmChoice_ChoiceToChoice_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmChoice_ChoiceToChoice
      //!
      //! Enter S2
      void FppTest_SmChoice_ChoiceToChoice_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmChoice_ChoiceToState
      //!
      //! Exit S1
      void FppTest_SmChoice_ChoiceToState_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoice_ChoiceToState
      //!
      //! Action a
      void FppTest_SmChoice_ChoiceToState_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmChoice_ChoiceToState
      //!
      //! Enter S2
      void FppTest_SmChoice_ChoiceToState_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS3 of state machine FppTest_SmChoice_ChoiceToState
      //!
      //! Enter S3
      void FppTest_SmChoice_ChoiceToState_action_enterS3(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoice_InputPairU16U32
      //!
      //! Action a
      void FppTest_SmChoice_InputPairU16U32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_InputPairU16U32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoice_Sequence
      //!
      //! Action a
      void FppTest_SmChoice_Sequence_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Sequence::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmChoice_Sequence
      //!
      //! Action b
      void FppTest_SmChoice_Sequence_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Sequence::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoice_SequenceU32
      //!
      //! Action a
      void FppTest_SmChoice_SequenceU32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_SequenceU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action b of state machine FppTest_SmChoice_SequenceU32
      //!
      //! Action b
      void FppTest_SmChoice_SequenceU32_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_SequenceU32::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmChoiceActive_Basic
      //!
      //! Action a
      void FppTest_SmChoiceActive_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmChoiceActive_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmChoiceActive_Basic
      //!
      //! Action b
      void FppTest_SmChoiceActive_Basic_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmChoiceActive_Basic::Signal signal //!< The signal
      ) override;

    private:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine guards
      // ----------------------------------------------------------------------

      //! Implementation for guard g of state machine FppTest_SmChoice_Basic
      //!
      //! Guard g
      bool FppTest_SmChoice_Basic_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Basic::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmChoice_BasicU32
      //!
      //! Guard g
      bool FppTest_SmChoice_BasicU32_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_BasicU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g1 of state machine FppTest_SmChoice_ChoiceToChoice
      //!
      //! Guard g1
      bool FppTest_SmChoice_ChoiceToChoice_guard_g1(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToChoice::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g2 of state machine FppTest_SmChoice_ChoiceToChoice
      //!
      //! Guard g2
      bool FppTest_SmChoice_ChoiceToChoice_guard_g2(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToChoice::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmChoice_ChoiceToState
      //!
      //! Guard g
      bool FppTest_SmChoice_ChoiceToState_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_ChoiceToState::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmChoice_InputPairU16U32
      //!
      //! Guard g
      bool FppTest_SmChoice_InputPairU16U32_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_InputPairU16U32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g1 of state machine FppTest_SmChoice_Sequence
      //!
      //! Guard g1
      bool FppTest_SmChoice_Sequence_guard_g1(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Sequence::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g2 of state machine FppTest_SmChoice_Sequence
      //!
      //! Guard g2
      bool FppTest_SmChoice_Sequence_guard_g2(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_Sequence::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g1 of state machine FppTest_SmChoice_SequenceU32
      //!
      //! Guard g1
      bool FppTest_SmChoice_SequenceU32_guard_g1(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_SequenceU32::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g2 of state machine FppTest_SmChoice_SequenceU32
      //!
      //! Guard g2
      bool FppTest_SmChoice_SequenceU32_guard_g2(
          SmId smId, //!< The state machine id
          FppTest_SmChoice_SequenceU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmChoiceActive_Basic
      //!
      //! Guard g
      bool FppTest_SmChoiceActive_Basic_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmChoiceActive_Basic::Signal signal //!< The signal
      ) const override;

    private:

      // ----------------------------------------------------------------------
      // Overflow hook implementations for internal state machines
      // ----------------------------------------------------------------------

      //! Overflow hook implementation for smChoiceChoiceToChoice
      void smChoiceChoiceToChoice_stateMachineOverflowHook(
          SmId smId, //!< The state machine ID
          FwEnumStoreType signal, //!< The signal
          Fw::SerializeBufferBase& buffer //!< The message buffer
      ) override;

  };

}

#endif
