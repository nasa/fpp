// ======================================================================
// \title  SmChoiceQueued.cpp
// \author [user name]
// \brief  cpp file for SmChoiceQueued component implementation class
// ======================================================================

#include "SmChoiceQueued.hpp"

namespace FppTest {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  SmChoiceQueued ::
    SmChoiceQueued(const char* const compName) :
      SmChoiceQueuedComponentBase(compName)
  {

  }

  SmChoiceQueued ::
    ~SmChoiceQueued()
  {

  }

  // ----------------------------------------------------------------------
  // Handler implementations for typed input ports
  // ----------------------------------------------------------------------

  void SmChoiceQueued ::
    schedIn_handler(
        FwIndexType portNum,
        U32 context
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine actions
  // ----------------------------------------------------------------------

  void SmChoiceQueued ::
    FppTest_SmChoice_Basic_action_a(
        SmId smId,
        FppTest_SmChoice_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_Basic_action_b(
        SmId smId,
        FppTest_SmChoice_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_BasicU32_action_a(
        SmId smId,
        FppTest_SmChoice_BasicU32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_BasicU32_action_b(
        SmId smId,
        FppTest_SmChoice_BasicU32::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToChoice_action_exitS1(
        SmId smId,
        FppTest_SmChoice_ChoiceToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToChoice_action_a(
        SmId smId,
        FppTest_SmChoice_ChoiceToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToChoice_action_enterS2(
        SmId smId,
        FppTest_SmChoice_ChoiceToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToState_action_exitS1(
        SmId smId,
        FppTest_SmChoice_ChoiceToState::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToState_action_a(
        SmId smId,
        FppTest_SmChoice_ChoiceToState::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToState_action_enterS2(
        SmId smId,
        FppTest_SmChoice_ChoiceToState::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToState_action_enterS3(
        SmId smId,
        FppTest_SmChoice_ChoiceToState::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_InputPairU16U32_action_a(
        SmId smId,
        FppTest_SmChoice_InputPairU16U32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_Sequence_action_a(
        SmId smId,
        FppTest_SmChoice_Sequence::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_Sequence_action_b(
        SmId smId,
        FppTest_SmChoice_Sequence::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_SequenceU32_action_a(
        SmId smId,
        FppTest_SmChoice_SequenceU32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoice_SequenceU32_action_b(
        SmId smId,
        FppTest_SmChoice_SequenceU32::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoiceQueued_Basic_action_a(
        SmId smId,
        FppTest_SmChoiceQueued_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmChoiceQueued ::
    FppTest_SmChoiceQueued_Basic_action_b(
        SmId smId,
        FppTest_SmChoiceQueued_Basic::Signal signal
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine guards
  // ----------------------------------------------------------------------

  bool SmChoiceQueued ::
    FppTest_SmChoice_Basic_guard_g(
        SmId smId,
        FppTest_SmChoice_Basic::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_BasicU32_guard_g(
        SmId smId,
        FppTest_SmChoice_BasicU32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToChoice_guard_g1(
        SmId smId,
        FppTest_SmChoice_ChoiceToChoice::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToChoice_guard_g2(
        SmId smId,
        FppTest_SmChoice_ChoiceToChoice::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_ChoiceToState_guard_g(
        SmId smId,
        FppTest_SmChoice_ChoiceToState::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_InputPairU16U32_guard_g(
        SmId smId,
        FppTest_SmChoice_InputPairU16U32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_Sequence_guard_g1(
        SmId smId,
        FppTest_SmChoice_Sequence::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_Sequence_guard_g2(
        SmId smId,
        FppTest_SmChoice_Sequence::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_SequenceU32_guard_g1(
        SmId smId,
        FppTest_SmChoice_SequenceU32::Signal signal
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoice_SequenceU32_guard_g2(
        SmId smId,
        FppTest_SmChoice_SequenceU32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmChoiceQueued ::
    FppTest_SmChoiceQueued_Basic_guard_g(
        SmId smId,
        FppTest_SmChoiceQueued_Basic::Signal signal
    ) const
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for internal state machines
  // ----------------------------------------------------------------------

  void SmChoiceQueued ::
    smChoiceChoiceToChoice_stateMachineOverflowHook(
        SmId smId,
        FwEnumStoreType signal,
        Fw::SerializeBufferBase& buffer
    )
  {
    // TODO
  }

}
