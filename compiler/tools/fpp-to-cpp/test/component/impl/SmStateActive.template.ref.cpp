// ======================================================================
// \title  SmStateActive.cpp
// \author [user name]
// \brief  cpp file for SmStateActive component implementation class
// ======================================================================

#include "SmStateActive.hpp"

namespace FppTest {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  SmStateActive ::
    SmStateActive(const char* const compName) :
      SmStateActiveComponentBase(compName)
  {

  }

  SmStateActive ::
    ~SmStateActive()
  {

  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine actions
  // ----------------------------------------------------------------------

  void SmStateActive ::
    FppTest_SmState_Basic_action_a(
        SmId smId,
        FppTest_SmState_Basic::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuard_action_a(
        SmId smId,
        FppTest_SmState_BasicGuard::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuardString_action_a(
        SmId smId,
        FppTest_SmState_BasicGuardString::Signal signal,
        const Fw::StringBase& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuardTestAbsType_action_a(
        SmId smId,
        FppTest_SmState_BasicGuardTestAbsType::Signal signal,
        const FppTest::SmHarness::TestAbsType& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuardTestArray_action_a(
        SmId smId,
        FppTest_SmState_BasicGuardTestArray::Signal signal,
        const FppTest::SmHarness::TestArray& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuardTestEnum_action_a(
        SmId smId,
        FppTest_SmState_BasicGuardTestEnum::Signal signal,
        const FppTest::SmHarness::TestEnum& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuardTestStruct_action_a(
        SmId smId,
        FppTest_SmState_BasicGuardTestStruct::Signal signal,
        const FppTest::SmHarness::TestStruct& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicGuardU32_action_a(
        SmId smId,
        FppTest_SmState_BasicGuardU32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicInternal_action_a(
        SmId smId,
        FppTest_SmState_BasicInternal::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicSelf_action_a(
        SmId smId,
        FppTest_SmState_BasicSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicString_action_a(
        SmId smId,
        FppTest_SmState_BasicString::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicString_action_b(
        SmId smId,
        FppTest_SmState_BasicString::Signal signal,
        const Fw::StringBase& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestAbsType_action_a(
        SmId smId,
        FppTest_SmState_BasicTestAbsType::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestAbsType_action_b(
        SmId smId,
        FppTest_SmState_BasicTestAbsType::Signal signal,
        const FppTest::SmHarness::TestAbsType& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestArray_action_a(
        SmId smId,
        FppTest_SmState_BasicTestArray::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestArray_action_b(
        SmId smId,
        FppTest_SmState_BasicTestArray::Signal signal,
        const FppTest::SmHarness::TestArray& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestEnum_action_a(
        SmId smId,
        FppTest_SmState_BasicTestEnum::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestEnum_action_b(
        SmId smId,
        FppTest_SmState_BasicTestEnum::Signal signal,
        const FppTest::SmHarness::TestEnum& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestStruct_action_a(
        SmId smId,
        FppTest_SmState_BasicTestStruct::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicTestStruct_action_b(
        SmId smId,
        FppTest_SmState_BasicTestStruct::Signal signal,
        const FppTest::SmHarness::TestStruct& value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicU32_action_a(
        SmId smId,
        FppTest_SmState_BasicU32::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_BasicU32_action_b(
        SmId smId,
        FppTest_SmState_BasicU32::Signal signal,
        U32 value
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_Internal_action_a(
        SmId smId,
        FppTest_SmState_Internal::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChild_action_exitS2(
        SmId smId,
        FppTest_SmState_StateToChild::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChild_action_exitS3(
        SmId smId,
        FppTest_SmState_StateToChild::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChild_action_a(
        SmId smId,
        FppTest_SmState_StateToChild::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChild_action_enterS2(
        SmId smId,
        FppTest_SmState_StateToChild::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChild_action_enterS3(
        SmId smId,
        FppTest_SmState_StateToChild::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_exitS1(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_exitS2(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_exitS3(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_a(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_enterS1(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_enterS2(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_enterS3(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToChoice_action_enterS4(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_exitS1(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_exitS2(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_exitS3(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_a(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_enterS1(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_enterS2(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToSelf_action_enterS3(
        SmId smId,
        FppTest_SmState_StateToSelf::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_exitS1(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_exitS2(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_exitS3(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_a(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_enterS1(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_enterS2(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_enterS3(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_enterS4(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmState_StateToState_action_enterS5(
        SmId smId,
        FppTest_SmState_StateToState::Signal signal
    )
  {
    // TODO
  }

  void SmStateActive ::
    FppTest_SmStateActive_Basic_action_a(
        SmId smId,
        FppTest_SmStateActive_Basic::Signal signal
    )
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Implementations for internal state machine guards
  // ----------------------------------------------------------------------

  bool SmStateActive ::
    FppTest_SmState_BasicGuard_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuard::Signal signal
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_BasicGuardString_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuardString::Signal signal,
        const Fw::StringBase& value
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_BasicGuardTestAbsType_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuardTestAbsType::Signal signal,
        const FppTest::SmHarness::TestAbsType& value
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_BasicGuardTestArray_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuardTestArray::Signal signal,
        const FppTest::SmHarness::TestArray& value
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_BasicGuardTestEnum_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuardTestEnum::Signal signal,
        const FppTest::SmHarness::TestEnum& value
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_BasicGuardTestStruct_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuardTestStruct::Signal signal,
        const FppTest::SmHarness::TestStruct& value
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_BasicGuardU32_guard_g(
        SmId smId,
        FppTest_SmState_BasicGuardU32::Signal signal,
        U32 value
    ) const
  {
    // TODO
  }

  bool SmStateActive ::
    FppTest_SmState_StateToChoice_guard_g(
        SmId smId,
        FppTest_SmState_StateToChoice::Signal signal
    ) const
  {
    // TODO
  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for internal state machines
  // ----------------------------------------------------------------------

  void SmStateActive ::
    smStateBasicGuardTestAbsType_stateMachineOverflowHook(
        SmId smId,
        FwEnumStoreType signal,
        Fw::SerializeBufferBase& buffer
    )
  {
    // TODO
  }

}
