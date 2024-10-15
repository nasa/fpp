// ======================================================================
// \title  SmJunctionActive.cpp
// \author [user name]
// \brief  cpp file for SmJunctionActive component implementation class
// ======================================================================

#include "SmJunctionActive.hpp"

namespace FppTest {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  SmJunctionActive ::
    SmJunctionActive(const char* const compName) :
      SmJunctionActiveComponentBase(compName)
  {

  }

  SmJunctionActive ::
    ~SmJunctionActive()
  {

  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine actions
  // ----------------------------------------------------------------------

  void SmJunctionActive ::
    FppTest_SmJunction_Basic_action_a(
        SmId smId,
        FppTest_SmJunction_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_Basic_action_b(
        SmId smId,
        FppTest_SmJunction_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_BasicU32_action_a(
        SmId smId,
        FppTest_SmJunction_BasicU32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_BasicU32_action_b(
        SmId smId,
        FppTest_SmJunction_BasicU32::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_InputPairU16U32_action_a(
        SmId smId,
        FppTest_SmJunction_InputPairU16U32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToJunction_action_exitS1(
        SmId smId,
        FppTest_SmJunction_JunctionToJunction::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToJunction_action_a(
        SmId smId,
        FppTest_SmJunction_JunctionToJunction::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToJunction_action_enterS2(
        SmId smId,
        FppTest_SmJunction_JunctionToJunction::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToState_action_exitS1(
        SmId smId,
        FppTest_SmJunction_JunctionToState::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToState_action_a(
        SmId smId,
        FppTest_SmJunction_JunctionToState::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToState_action_enterS2(
        SmId smId,
        FppTest_SmJunction_JunctionToState::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_JunctionToState_action_enterS3(
        SmId smId,
        FppTest_SmJunction_JunctionToState::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_Sequence_action_a(
        SmId smId,
        FppTest_SmJunction_Sequence::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_Sequence_action_b(
        SmId smId,
        FppTest_SmJunction_Sequence::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_SequenceU32_action_a(
        SmId smId,
        FppTest_SmJunction_SequenceU32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunction_SequenceU32_action_b(
        SmId smId,
        FppTest_SmJunction_SequenceU32::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunctionActive_Basic_action_a(
        SmId smId,
        FppTest_SmJunctionActive_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmJunctionActive ::
    FppTest_SmJunctionActive_Basic_action_b(
        SmId smId,
        FppTest_SmJunctionActive_Basic::Signal signal
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine guards
  // ----------------------------------------------------------------------

  bool SmJunctionActive ::
    FppTest_SmJunction_Basic_guard_g(
        SmId smId,
        FppTest_SmJunction_Basic::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_BasicU32_guard_g(
        SmId smId,
        FppTest_SmJunction_BasicU32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_InputPairU16U32_guard_g(
        SmId smId,
        FppTest_SmJunction_InputPairU16U32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_JunctionToJunction_guard_g1(
        SmId smId,
        FppTest_SmJunction_JunctionToJunction::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_JunctionToJunction_guard_g2(
        SmId smId,
        FppTest_SmJunction_JunctionToJunction::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_JunctionToState_guard_g(
        SmId smId,
        FppTest_SmJunction_JunctionToState::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_Sequence_guard_g1(
        SmId smId,
        FppTest_SmJunction_Sequence::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_Sequence_guard_g2(
        SmId smId,
        FppTest_SmJunction_Sequence::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_SequenceU32_guard_g1(
        SmId smId,
        FppTest_SmJunction_SequenceU32::Signal signal
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunction_SequenceU32_guard_g2(
        SmId smId,
        FppTest_SmJunction_SequenceU32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmJunctionActive ::
    FppTest_SmJunctionActive_Basic_guard_g(
        SmId smId,
        FppTest_SmJunctionActive_Basic::Signal signal
    ) const
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for internal state machines
  // ----------------------------------------------------------------------

  void SmJunctionActive ::
    smJunctionJunctionToJunction_stateMachineOverflowHook(
        SmId smId,
        FwEnumStoreType signal,
        Fw::SerializeBufferBase& buffer
    )
  {
    // TODO
  }

}
