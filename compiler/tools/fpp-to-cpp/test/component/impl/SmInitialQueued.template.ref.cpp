// ======================================================================
// \title  SmInitialQueued.cpp
// \author [user name]
// \brief  cpp file for SmInitialQueued component implementation class
// ======================================================================

#include "SmInitialQueued.hpp"

namespace FppTest {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  SmInitialQueued ::
    SmInitialQueued(const char* const compName) :
      SmInitialQueuedComponentBase(compName)
  {

  }

  SmInitialQueued ::
    ~SmInitialQueued()
  {

  }

  // ----------------------------------------------------------------------
  // Handler implementations for typed input ports
  // ----------------------------------------------------------------------

  void SmInitialQueued ::
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

  void SmInitialQueued ::
    FppTest_SmInitial_Basic_action_a(
        SmId smId,
        FppTest_SmInitial_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmInitialQueued ::
    FppTest_SmInitial_Choice_action_a(
        SmId smId,
        FppTest_SmInitial_Choice::Signal signal
    )
  {
    // TODO
  }

  void SmInitialQueued ::
    FppTest_SmInitial_Nested_action_a(
        SmId smId,
        FppTest_SmInitial_Nested::Signal signal
    )
  {
    // TODO
  }

  void SmInitialQueued ::
    FppTest_SmInitialQueued_Basic_action_a(
        SmId smId,
        FppTest_SmInitialQueued_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmInitialQueued ::
    FppTest_SmInitialQueued_Choice_action_a(
        SmId smId,
        FppTest_SmInitialQueued_Choice::Signal signal
    )
  {
    // TODO
  }

  void SmInitialQueued ::
    FppTest_SmInitialQueued_Nested_action_a(
        SmId smId,
        FppTest_SmInitialQueued_Nested::Signal signal
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine guards
  // ----------------------------------------------------------------------

  bool SmInitialQueued ::
    FppTest_SmInitial_Choice_guard_g(
        SmId smId,
        FppTest_SmInitial_Choice::Signal signal
    ) const
  {
    // TODO
  }

  bool SmInitialQueued ::
    FppTest_SmInitialQueued_Choice_guard_g(
        SmId smId,
        FppTest_SmInitialQueued_Choice::Signal signal
    ) const
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for internal state machines
  // ----------------------------------------------------------------------

  void SmInitialQueued ::
    smInitialNested_stateMachineOverflowHook(
        SmId smId,
        FwEnumStoreType signal,
        Fw::SerializeBufferBase& buffer
    )
  {
    // TODO
  }

}
