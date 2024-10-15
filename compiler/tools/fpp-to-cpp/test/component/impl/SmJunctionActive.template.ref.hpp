// ======================================================================
// \title  SmJunctionActive.hpp
// \author [user name]
// \brief  hpp file for SmJunctionActive component implementation class
// ======================================================================

#ifndef FppTest_SmJunctionActive_HPP
#define FppTest_SmJunctionActive_HPP

#include "SmJunctionActiveComponentAc.hpp"

namespace FppTest {

  class SmJunctionActive :
    public SmJunctionActiveComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct SmJunctionActive object
      SmJunctionActive(
          const char* const compName //!< The component name
      );

      //! Destroy SmJunctionActive object
      ~SmJunctionActive();

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine actions
      // ----------------------------------------------------------------------

      //! Implementation for action a of state machine FppTest_SmJunction_Basic
      //!
      //! Action a
      void FppTest_SmJunction_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmJunction_Basic
      //!
      //! Action b
      void FppTest_SmJunction_Basic_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunction_BasicU32
      //!
      //! Action a
      void FppTest_SmJunction_BasicU32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_BasicU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action b of state machine FppTest_SmJunction_BasicU32
      //!
      //! Action b
      void FppTest_SmJunction_BasicU32_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_BasicU32::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunction_InputPairU16U32
      //!
      //! Action a
      void FppTest_SmJunction_InputPairU16U32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_InputPairU16U32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmJunction_JunctionToJunction
      //!
      //! Exit S1
      void FppTest_SmJunction_JunctionToJunction_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToJunction::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunction_JunctionToJunction
      //!
      //! Action a
      void FppTest_SmJunction_JunctionToJunction_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToJunction::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmJunction_JunctionToJunction
      //!
      //! Enter S2
      void FppTest_SmJunction_JunctionToJunction_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToJunction::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmJunction_JunctionToState
      //!
      //! Exit S1
      void FppTest_SmJunction_JunctionToState_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunction_JunctionToState
      //!
      //! Action a
      void FppTest_SmJunction_JunctionToState_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmJunction_JunctionToState
      //!
      //! Enter S2
      void FppTest_SmJunction_JunctionToState_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS3 of state machine FppTest_SmJunction_JunctionToState
      //!
      //! Enter S3
      void FppTest_SmJunction_JunctionToState_action_enterS3(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunction_Sequence
      //!
      //! Action a
      void FppTest_SmJunction_Sequence_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Sequence::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmJunction_Sequence
      //!
      //! Action b
      void FppTest_SmJunction_Sequence_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Sequence::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunction_SequenceU32
      //!
      //! Action a
      void FppTest_SmJunction_SequenceU32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_SequenceU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action b of state machine FppTest_SmJunction_SequenceU32
      //!
      //! Action b
      void FppTest_SmJunction_SequenceU32_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_SequenceU32::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmJunctionActive_Basic
      //!
      //! Action a
      void FppTest_SmJunctionActive_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmJunctionActive_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmJunctionActive_Basic
      //!
      //! Action b
      void FppTest_SmJunctionActive_Basic_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmJunctionActive_Basic::Signal signal //!< The signal
      ) override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine guards
      // ----------------------------------------------------------------------

      //! Implementation for guard g of state machine FppTest_SmJunction_Basic
      //!
      //! Guard g
      bool FppTest_SmJunction_Basic_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Basic::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmJunction_BasicU32
      //!
      //! Guard g
      bool FppTest_SmJunction_BasicU32_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_BasicU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmJunction_InputPairU16U32
      //!
      //! Guard g
      bool FppTest_SmJunction_InputPairU16U32_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_InputPairU16U32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g1 of state machine FppTest_SmJunction_JunctionToJunction
      //!
      //! Guard g1
      bool FppTest_SmJunction_JunctionToJunction_guard_g1(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToJunction::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g2 of state machine FppTest_SmJunction_JunctionToJunction
      //!
      //! Guard g2
      bool FppTest_SmJunction_JunctionToJunction_guard_g2(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToJunction::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmJunction_JunctionToState
      //!
      //! Guard g
      bool FppTest_SmJunction_JunctionToState_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_JunctionToState::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g1 of state machine FppTest_SmJunction_Sequence
      //!
      //! Guard g1
      bool FppTest_SmJunction_Sequence_guard_g1(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Sequence::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g2 of state machine FppTest_SmJunction_Sequence
      //!
      //! Guard g2
      bool FppTest_SmJunction_Sequence_guard_g2(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_Sequence::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g1 of state machine FppTest_SmJunction_SequenceU32
      //!
      //! Guard g1
      bool FppTest_SmJunction_SequenceU32_guard_g1(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_SequenceU32::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g2 of state machine FppTest_SmJunction_SequenceU32
      //!
      //! Guard g2
      bool FppTest_SmJunction_SequenceU32_guard_g2(
          SmId smId, //!< The state machine id
          FppTest_SmJunction_SequenceU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmJunctionActive_Basic
      //!
      //! Guard g
      bool FppTest_SmJunctionActive_Basic_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmJunctionActive_Basic::Signal signal //!< The signal
      ) const override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Overflow hook implementations for internal state machines
      // ----------------------------------------------------------------------

      //! Overflow hook implementation for smJunctionJunctionToJunction
      void smJunctionJunctionToJunction_stateMachineOverflowHook(
          SmId smId, //!< The state machine ID
          FwEnumStoreType signal, //!< The signal
          Fw::SerializeBufferBase& buffer //!< The message buffer
      ) override;

  };

}

#endif
