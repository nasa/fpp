// ======================================================================
// \title  SmInitialActive.cpp
// \author [user name]
// \brief  cpp file for SmInitialActive component implementation class
// ======================================================================

#include "SmInitialActive.hpp"

namespace FppTest {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  SmInitialActive ::
    SmInitialActive(const char* const compName) :
      SmInitialActiveComponentBase(compName)
  {

  }

  SmInitialActive ::
    ~SmInitialActive()
  {

  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine actions
  // ----------------------------------------------------------------------

  void SmInitialActive ::
    FppTest_SmInitial_Basic_action_a(
        SmId smId,
        FppTest_SmInitial_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmInitialActive ::
    FppTest_SmInitial_Choice_action_a(
        SmId smId,
        FppTest_SmInitial_Choice::Signal signal
    )
  {
    // TODO
  }

  void SmInitialActive ::
    FppTest_SmInitial_Nested_action_a(
        SmId smId,
        FppTest_SmInitial_Nested::Signal signal
    )
  {
    // TODO
  }

  void SmInitialActive ::
    FppTest_SmInitialActive_Basic_action_a(
        SmId smId,
        FppTest_SmInitialActive_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmInitialActive ::
    FppTest_SmInitialActive_Choice_action_a(
        SmId smId,
        FppTest_SmInitialActive_Choice::Signal signal
    )
  {
    // TODO
  }

  void SmInitialActive ::
    FppTest_SmInitialActive_Nested_action_a(
        SmId smId,
        FppTest_SmInitialActive_Nested::Signal signal
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine guards
  // ----------------------------------------------------------------------

  bool SmInitialActive ::
    FppTest_SmInitial_Choice_guard_g(
        SmId smId,
        FppTest_SmInitial_Choice::Signal signal
    ) const
  {
    // TODO
  }

  bool SmInitialActive ::
    FppTest_SmInitialActive_Choice_guard_g(
        SmId smId,
        FppTest_SmInitialActive_Choice::Signal signal
    ) const
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for internal state machines
  // ----------------------------------------------------------------------

  void SmInitialActive ::
    smInitialNested_stateMachineOverflowHook(
        SmId smId,
        FwEnumStoreType signal,
        Fw::SerializeBufferBase& buffer
    )
  {
    // TODO
  }

}
