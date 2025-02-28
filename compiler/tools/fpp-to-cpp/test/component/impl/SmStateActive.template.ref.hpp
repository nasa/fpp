// ======================================================================
// \title  SmStateActive.hpp
// \author [user name]
// \brief  hpp file for SmStateActive component implementation class
// ======================================================================

#ifndef FppTest_SmStateActive_HPP
#define FppTest_SmStateActive_HPP

#include "SmStateActiveComponentAc.hpp"

namespace FppTest {

  class SmStateActive final :
    public SmStateActiveComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct SmStateActive object
      SmStateActive(
          const char* const compName //!< The component name
      );

      //! Destroy SmStateActive object
      ~SmStateActive();

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine actions
      // ----------------------------------------------------------------------

      //! Implementation for action a of state machine FppTest_SmState_Basic
      //!
      //! Action a
      void FppTest_SmState_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_Basic::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuard
      //!
      //! Action a
      void FppTest_SmState_BasicGuard_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuard::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuardString
      //!
      //! Action a
      void FppTest_SmState_BasicGuardString_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardString::Signal signal, //!< The signal
          const Fw::StringBase& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuardTestAbsType
      //!
      //! Action a
      void FppTest_SmState_BasicGuardTestAbsType_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestAbsType::Signal signal, //!< The signal
          const FppTest::SmHarness::TestAbsType& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuardTestArray
      //!
      //! Action a
      void FppTest_SmState_BasicGuardTestArray_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestArray::Signal signal, //!< The signal
          const FppTest::SmHarness::TestArray& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuardTestEnum
      //!
      //! Action a
      void FppTest_SmState_BasicGuardTestEnum_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestEnum::Signal signal, //!< The signal
          const FppTest::SmHarness::TestEnum& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuardTestStruct
      //!
      //! Action a
      void FppTest_SmState_BasicGuardTestStruct_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestStruct::Signal signal, //!< The signal
          const FppTest::SmHarness::TestStruct& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicGuardU32
      //!
      //! Action a
      void FppTest_SmState_BasicGuardU32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicInternal
      //!
      //! Action a
      void FppTest_SmState_BasicInternal_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicInternal::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicSelf
      //!
      //! Action a
      void FppTest_SmState_BasicSelf_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicString
      //!
      //! Action a
      void FppTest_SmState_BasicString_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicString::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmState_BasicString
      //!
      //! Action b
      void FppTest_SmState_BasicString_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicString::Signal signal, //!< The signal
          const Fw::StringBase& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicTestAbsType
      //!
      //! Action a
      void FppTest_SmState_BasicTestAbsType_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestAbsType::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmState_BasicTestAbsType
      //!
      //! Action b
      void FppTest_SmState_BasicTestAbsType_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestAbsType::Signal signal, //!< The signal
          const FppTest::SmHarness::TestAbsType& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicTestArray
      //!
      //! Action a
      void FppTest_SmState_BasicTestArray_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestArray::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmState_BasicTestArray
      //!
      //! Action b
      void FppTest_SmState_BasicTestArray_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestArray::Signal signal, //!< The signal
          const FppTest::SmHarness::TestArray& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicTestEnum
      //!
      //! Action a
      void FppTest_SmState_BasicTestEnum_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestEnum::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmState_BasicTestEnum
      //!
      //! Action b
      void FppTest_SmState_BasicTestEnum_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestEnum::Signal signal, //!< The signal
          const FppTest::SmHarness::TestEnum& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicTestStruct
      //!
      //! Action a
      void FppTest_SmState_BasicTestStruct_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestStruct::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmState_BasicTestStruct
      //!
      //! Action b
      void FppTest_SmState_BasicTestStruct_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicTestStruct::Signal signal, //!< The signal
          const FppTest::SmHarness::TestStruct& value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_BasicU32
      //!
      //! Action a
      void FppTest_SmState_BasicU32_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicU32::Signal signal //!< The signal
      ) override;

      //! Implementation for action b of state machine FppTest_SmState_BasicU32
      //!
      //! Action b
      void FppTest_SmState_BasicU32_action_b(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_Internal
      //!
      //! Action a
      void FppTest_SmState_Internal_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_Internal::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS2 of state machine FppTest_SmState_StateToChild
      //!
      //! Exit S2
      void FppTest_SmState_StateToChild_action_exitS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChild::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS3 of state machine FppTest_SmState_StateToChild
      //!
      //! Exit S3
      void FppTest_SmState_StateToChild_action_exitS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChild::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_StateToChild
      //!
      //! Action a
      void FppTest_SmState_StateToChild_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChild::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmState_StateToChild
      //!
      //! Enter S2
      void FppTest_SmState_StateToChild_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChild::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS3 of state machine FppTest_SmState_StateToChild
      //!
      //! Enter S3
      void FppTest_SmState_StateToChild_action_enterS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChild::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmState_StateToChoice
      //!
      //! Exit S1
      void FppTest_SmState_StateToChoice_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS2 of state machine FppTest_SmState_StateToChoice
      //!
      //! Exit S2
      void FppTest_SmState_StateToChoice_action_exitS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS3 of state machine FppTest_SmState_StateToChoice
      //!
      //! Exit S3
      void FppTest_SmState_StateToChoice_action_exitS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_StateToChoice
      //!
      //! Action a
      void FppTest_SmState_StateToChoice_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS1 of state machine FppTest_SmState_StateToChoice
      //!
      //! Enter S1
      void FppTest_SmState_StateToChoice_action_enterS1(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmState_StateToChoice
      //!
      //! Enter S2
      void FppTest_SmState_StateToChoice_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS3 of state machine FppTest_SmState_StateToChoice
      //!
      //! Enter S3
      void FppTest_SmState_StateToChoice_action_enterS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS4 of state machine FppTest_SmState_StateToChoice
      //!
      //! Enter S4
      void FppTest_SmState_StateToChoice_action_enterS4(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmState_StateToSelf
      //!
      //! Exit S1
      void FppTest_SmState_StateToSelf_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS2 of state machine FppTest_SmState_StateToSelf
      //!
      //! Exit S2
      void FppTest_SmState_StateToSelf_action_exitS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS3 of state machine FppTest_SmState_StateToSelf
      //!
      //! Exit S3
      void FppTest_SmState_StateToSelf_action_exitS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_StateToSelf
      //!
      //! Action a
      void FppTest_SmState_StateToSelf_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS1 of state machine FppTest_SmState_StateToSelf
      //!
      //! Enter S1
      void FppTest_SmState_StateToSelf_action_enterS1(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmState_StateToSelf
      //!
      //! Enter S2
      void FppTest_SmState_StateToSelf_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS3 of state machine FppTest_SmState_StateToSelf
      //!
      //! Enter S3
      void FppTest_SmState_StateToSelf_action_enterS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToSelf::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS1 of state machine FppTest_SmState_StateToState
      //!
      //! Exit S1
      void FppTest_SmState_StateToState_action_exitS1(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS2 of state machine FppTest_SmState_StateToState
      //!
      //! Exit S2
      void FppTest_SmState_StateToState_action_exitS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action exitS3 of state machine FppTest_SmState_StateToState
      //!
      //! Exit S3
      void FppTest_SmState_StateToState_action_exitS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmState_StateToState
      //!
      //! Action a
      void FppTest_SmState_StateToState_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS1 of state machine FppTest_SmState_StateToState
      //!
      //! Enter S1
      void FppTest_SmState_StateToState_action_enterS1(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS2 of state machine FppTest_SmState_StateToState
      //!
      //! Enter S2
      void FppTest_SmState_StateToState_action_enterS2(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS3 of state machine FppTest_SmState_StateToState
      //!
      //! Enter S3
      void FppTest_SmState_StateToState_action_enterS3(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS4 of state machine FppTest_SmState_StateToState
      //!
      //! Enter S4
      void FppTest_SmState_StateToState_action_enterS4(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action enterS5 of state machine FppTest_SmState_StateToState
      //!
      //! Enter S5
      void FppTest_SmState_StateToState_action_enterS5(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToState::Signal signal //!< The signal
      ) override;

      //! Implementation for action a of state machine FppTest_SmStateActive_Basic
      //!
      //! Action a
      void FppTest_SmStateActive_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmStateActive_Basic::Signal signal //!< The signal
      ) override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Implementations for internal state machine guards
      // ----------------------------------------------------------------------

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuard
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuard_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuard::Signal signal //!< The signal
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuardString
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuardString_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardString::Signal signal, //!< The signal
          const Fw::StringBase& value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuardTestAbsType
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuardTestAbsType_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestAbsType::Signal signal, //!< The signal
          const FppTest::SmHarness::TestAbsType& value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuardTestArray
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuardTestArray_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestArray::Signal signal, //!< The signal
          const FppTest::SmHarness::TestArray& value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuardTestEnum
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuardTestEnum_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestEnum::Signal signal, //!< The signal
          const FppTest::SmHarness::TestEnum& value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuardTestStruct
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuardTestStruct_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardTestStruct::Signal signal, //!< The signal
          const FppTest::SmHarness::TestStruct& value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_BasicGuardU32
      //!
      //! Guard g
      bool FppTest_SmState_BasicGuardU32_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_BasicGuardU32::Signal signal, //!< The signal
          U32 value //!< The value
      ) const override;

      //! Implementation for guard g of state machine FppTest_SmState_StateToChoice
      //!
      //! Guard g
      bool FppTest_SmState_StateToChoice_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmState_StateToChoice::Signal signal //!< The signal
      ) const override;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Overflow hook implementations for internal state machines
      // ----------------------------------------------------------------------

      //! Overflow hook implementation for smStateBasicGuardTestAbsType
      void smStateBasicGuardTestAbsType_stateMachineOverflowHook(
          SmId smId, //!< The state machine ID
          FwEnumStoreType signal, //!< The signal
          Fw::SerializeBufferBase& buffer //!< The message buffer
      ) override;

  };

}

#endif
