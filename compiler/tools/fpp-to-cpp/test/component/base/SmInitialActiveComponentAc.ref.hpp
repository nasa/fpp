// ======================================================================
// \title  SmInitialActiveComponentAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for SmInitialActive component base class
// ======================================================================

#ifndef FppTest_SmInitialActiveComponentAc_HPP
#define FppTest_SmInitialActiveComponentAc_HPP

#include "FpConfig.hpp"
#include "Fw/Comp/ActiveComponentBase.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Port/OutputSerializePort.hpp"
#include "SmInitialActive_BasicStateMachineAc.hpp"
#include "SmInitialActive_ChoiceStateMachineAc.hpp"
#include "SmInitialActive_NestedStateMachineAc.hpp"
#include "state-machine/initial/BasicStateMachineAc.hpp"
#include "state-machine/initial/ChoiceStateMachineAc.hpp"
#include "state-machine/initial/NestedStateMachineAc.hpp"

namespace FppTest {

  //! \class SmInitialActiveComponentBase
  //! \brief Auto-generated base for SmInitialActive component
  class SmInitialActiveComponentBase :
    public Fw::ActiveComponentBase
  {

      // ----------------------------------------------------------------------
      // Friend classes
      // ----------------------------------------------------------------------

      //! Friend class for white-box testing
      friend class SmInitialActiveComponentBaseFriend;

    PROTECTED:

      // ----------------------------------------------------------------------
      // Constants
      // ----------------------------------------------------------------------

      //! State machine identifiers
      enum class SmId : FwEnumStoreType {
        basic1,
        basic2,
        choice,
        nested,
        smInitialBasic1,
        smInitialBasic2,
        smInitialChoice,
        smInitialNested,
      };

    PROTECTED:

      // ----------------------------------------------------------------------
      // Types for internal state machines
      // ----------------------------------------------------------------------

      //! Implementation of state machine FppTest_SmInitial_Basic
      class FppTest_SmInitial_Basic :
        public FppTest::SmInitial::BasicStateMachineBase
      {

        public:

          //! Constructor
          FppTest_SmInitial_Basic(
              SmInitialActiveComponentBase& component //!< The enclosing component
          );

        public:

          //! Initialize the state machine
          void init(
              SmInitialActiveComponentBase::SmId smId //!< The state machine id
          );

        public:

          //! Get the state machine id
          SmInitialActiveComponentBase::SmId getId() const;

        PRIVATE:

          //! Implementation for action a
          void action_a(
              Signal signal //!< The signal
          );

        PRIVATE:

          //! The enclosing component
          SmInitialActiveComponentBase& m_component;

      };

      //! Implementation of state machine FppTest_SmInitial_Choice
      class FppTest_SmInitial_Choice :
        public FppTest::SmInitial::ChoiceStateMachineBase
      {

        public:

          //! Constructor
          FppTest_SmInitial_Choice(
              SmInitialActiveComponentBase& component //!< The enclosing component
          );

        public:

          //! Initialize the state machine
          void init(
              SmInitialActiveComponentBase::SmId smId //!< The state machine id
          );

        public:

          //! Get the state machine id
          SmInitialActiveComponentBase::SmId getId() const;

        PRIVATE:

          //! Implementation for action a
          void action_a(
              Signal signal //!< The signal
          );

        PRIVATE:

          //! Implementation for guard g
          bool guard_g(
              Signal signal //!< The signal
          ) const;

        PRIVATE:

          //! The enclosing component
          SmInitialActiveComponentBase& m_component;

      };

      //! Implementation of state machine FppTest_SmInitial_Nested
      class FppTest_SmInitial_Nested :
        public FppTest::SmInitial::NestedStateMachineBase
      {

        public:

          //! Constructor
          FppTest_SmInitial_Nested(
              SmInitialActiveComponentBase& component //!< The enclosing component
          );

        public:

          //! Initialize the state machine
          void init(
              SmInitialActiveComponentBase::SmId smId //!< The state machine id
          );

        public:

          //! Get the state machine id
          SmInitialActiveComponentBase::SmId getId() const;

        PRIVATE:

          //! Implementation for action a
          void action_a(
              Signal signal //!< The signal
          );

        PRIVATE:

          //! The enclosing component
          SmInitialActiveComponentBase& m_component;

      };

      //! Implementation of state machine FppTest_SmInitialActive_Basic
      class FppTest_SmInitialActive_Basic :
        public FppTest::SmInitialActive_BasicStateMachineBase
      {

        public:

          //! Constructor
          FppTest_SmInitialActive_Basic(
              SmInitialActiveComponentBase& component //!< The enclosing component
          );

        public:

          //! Initialize the state machine
          void init(
              SmInitialActiveComponentBase::SmId smId //!< The state machine id
          );

        public:

          //! Get the state machine id
          SmInitialActiveComponentBase::SmId getId() const;

        PRIVATE:

          //! Implementation for action a
          void action_a(
              Signal signal //!< The signal
          );

        PRIVATE:

          //! The enclosing component
          SmInitialActiveComponentBase& m_component;

      };

      //! Implementation of state machine FppTest_SmInitialActive_Choice
      class FppTest_SmInitialActive_Choice :
        public FppTest::SmInitialActive_ChoiceStateMachineBase
      {

        public:

          //! Constructor
          FppTest_SmInitialActive_Choice(
              SmInitialActiveComponentBase& component //!< The enclosing component
          );

        public:

          //! Initialize the state machine
          void init(
              SmInitialActiveComponentBase::SmId smId //!< The state machine id
          );

        public:

          //! Get the state machine id
          SmInitialActiveComponentBase::SmId getId() const;

        PRIVATE:

          //! Implementation for action a
          void action_a(
              Signal signal //!< The signal
          );

        PRIVATE:

          //! Implementation for guard g
          bool guard_g(
              Signal signal //!< The signal
          ) const;

        PRIVATE:

          //! The enclosing component
          SmInitialActiveComponentBase& m_component;

      };

      //! Implementation of state machine FppTest_SmInitialActive_Nested
      class FppTest_SmInitialActive_Nested :
        public FppTest::SmInitialActive_NestedStateMachineBase
      {

        public:

          //! Constructor
          FppTest_SmInitialActive_Nested(
              SmInitialActiveComponentBase& component //!< The enclosing component
          );

        public:

          //! Initialize the state machine
          void init(
              SmInitialActiveComponentBase::SmId smId //!< The state machine id
          );

        public:

          //! Get the state machine id
          SmInitialActiveComponentBase::SmId getId() const;

        PRIVATE:

          //! Implementation for action a
          void action_a(
              Signal signal //!< The signal
          );

        PRIVATE:

          //! The enclosing component
          SmInitialActiveComponentBase& m_component;

      };

    public:

      // ----------------------------------------------------------------------
      // Component initialization
      // ----------------------------------------------------------------------

      //! Initialize SmInitialActiveComponentBase object
      void init(
          FwSizeType queueDepth, //!< The queue depth
          FwEnumStoreType instance = 0 //!< The instance number
      );

    PROTECTED:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct SmInitialActiveComponentBase object
      SmInitialActiveComponentBase(
          const char* compName = "" //!< The component name
      );

      //! Destroy SmInitialActiveComponentBase object
      virtual ~SmInitialActiveComponentBase();

    PROTECTED:

      // ----------------------------------------------------------------------
      // State getter functions
      // ----------------------------------------------------------------------

      //! Get the state of state machine instance basic1
      FppTest_SmInitialActive_Basic::State basic1_getState() const;

      //! Get the state of state machine instance basic2
      FppTest_SmInitialActive_Basic::State basic2_getState() const;

      //! Get the state of state machine instance choice
      FppTest_SmInitialActive_Choice::State choice_getState() const;

      //! Get the state of state machine instance nested
      FppTest_SmInitialActive_Nested::State nested_getState() const;

      //! Get the state of state machine instance smInitialBasic1
      FppTest_SmInitial_Basic::State smInitialBasic1_getState() const;

      //! Get the state of state machine instance smInitialBasic2
      FppTest_SmInitial_Basic::State smInitialBasic2_getState() const;

      //! Get the state of state machine instance smInitialChoice
      FppTest_SmInitial_Choice::State smInitialChoice_getState() const;

      //! Get the state of state machine instance smInitialNested
      FppTest_SmInitial_Nested::State smInitialNested_getState() const;

    PROTECTED:

      // ----------------------------------------------------------------------
      // Overflow hooks for internal state machine instances
      //
      // When sending a signal to a state machine instance, if
      // the queue overflows and the instance is marked with 'hook' behavior,
      // the corresponding function here is called.
      // ----------------------------------------------------------------------

      //! Overflow hook for state machine smInitialNested
      virtual void smInitialNested_stateMachineOverflowHook(
          SmId smId, //!< The state machine ID
          FwEnumStoreType signal, //!< The signal
          Fw::SerializeBufferBase& buffer //!< The message buffer
      ) = 0;

    PROTECTED:

      // ----------------------------------------------------------------------
      // Functions to implement for internal state machine actions
      // ----------------------------------------------------------------------

      //! Implementation for action a of state machine FppTest_SmInitial_Basic
      //!
      //! Action a
      virtual void FppTest_SmInitial_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Basic::Signal signal //!< The signal
      ) = 0;

      //! Implementation for action a of state machine FppTest_SmInitial_Choice
      //!
      //! Action a
      virtual void FppTest_SmInitial_Choice_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Choice::Signal signal //!< The signal
      ) = 0;

      //! Implementation for action a of state machine FppTest_SmInitial_Nested
      //!
      //! Action a
      virtual void FppTest_SmInitial_Nested_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Nested::Signal signal //!< The signal
      ) = 0;

      //! Implementation for action a of state machine FppTest_SmInitialActive_Basic
      //!
      //! Action a
      virtual void FppTest_SmInitialActive_Basic_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Basic::Signal signal //!< The signal
      ) = 0;

      //! Implementation for action a of state machine FppTest_SmInitialActive_Choice
      //!
      //! Action a
      virtual void FppTest_SmInitialActive_Choice_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Choice::Signal signal //!< The signal
      ) = 0;

      //! Implementation for action a of state machine FppTest_SmInitialActive_Nested
      //!
      //! Action a
      virtual void FppTest_SmInitialActive_Nested_action_a(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Nested::Signal signal //!< The signal
      ) = 0;

    PROTECTED:

      // ----------------------------------------------------------------------
      // Functions to implement for internal state machine guards
      // ----------------------------------------------------------------------

      //! Implementation for guard g of state machine FppTest_SmInitial_Choice
      //!
      //! Guard g
      virtual bool FppTest_SmInitial_Choice_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmInitial_Choice::Signal signal //!< The signal
      ) const = 0;

      //! Implementation for guard g of state machine FppTest_SmInitialActive_Choice
      //!
      //! Guard g
      virtual bool FppTest_SmInitialActive_Choice_guard_g(
          SmId smId, //!< The state machine id
          FppTest_SmInitialActive_Choice::Signal signal //!< The signal
      ) const = 0;

    PRIVATE:

      // ----------------------------------------------------------------------
      // Message dispatch functions
      // ----------------------------------------------------------------------

      //! Called in the message loop to dispatch a message from the queue
      virtual MsgDispatchStatus doDispatch();

    PRIVATE:

      // ----------------------------------------------------------------------
      // Helper functions for state machine dispatch
      // ----------------------------------------------------------------------

      //! Dispatch a signal to a state machine instance
      void smDispatch(
          Fw::SerializeBufferBase& buffer //!< The message buffer
      );

      //! Deserialize the state machine ID and signal from the message buffer
      static void deserializeSmIdAndSignal(
          Fw::SerializeBufferBase& buffer, //!< The message buffer (input and output)
          FwEnumStoreType& smId, //!< The state machine ID (output)
          FwEnumStoreType& signal //!< The signal (output)
      );

      //! Dispatch a signal to a state machine instance of type FppTest_SmInitial_Basic
      void FppTest_SmInitial_Basic_smDispatch(
          Fw::SerializeBufferBase& buffer, //!< The message buffer
          FppTest_SmInitial_Basic& sm, //!< The state machine
          FppTest_SmInitial_Basic::Signal signal //!< The signal
      );

      //! Dispatch a signal to a state machine instance of type FppTest_SmInitial_Choice
      void FppTest_SmInitial_Choice_smDispatch(
          Fw::SerializeBufferBase& buffer, //!< The message buffer
          FppTest_SmInitial_Choice& sm, //!< The state machine
          FppTest_SmInitial_Choice::Signal signal //!< The signal
      );

      //! Dispatch a signal to a state machine instance of type FppTest_SmInitial_Nested
      void FppTest_SmInitial_Nested_smDispatch(
          Fw::SerializeBufferBase& buffer, //!< The message buffer
          FppTest_SmInitial_Nested& sm, //!< The state machine
          FppTest_SmInitial_Nested::Signal signal //!< The signal
      );

      //! Dispatch a signal to a state machine instance of type FppTest_SmInitialActive_Basic
      void FppTest_SmInitialActive_Basic_smDispatch(
          Fw::SerializeBufferBase& buffer, //!< The message buffer
          FppTest_SmInitialActive_Basic& sm, //!< The state machine
          FppTest_SmInitialActive_Basic::Signal signal //!< The signal
      );

      //! Dispatch a signal to a state machine instance of type FppTest_SmInitialActive_Choice
      void FppTest_SmInitialActive_Choice_smDispatch(
          Fw::SerializeBufferBase& buffer, //!< The message buffer
          FppTest_SmInitialActive_Choice& sm, //!< The state machine
          FppTest_SmInitialActive_Choice::Signal signal //!< The signal
      );

      //! Dispatch a signal to a state machine instance of type FppTest_SmInitialActive_Nested
      void FppTest_SmInitialActive_Nested_smDispatch(
          Fw::SerializeBufferBase& buffer, //!< The message buffer
          FppTest_SmInitialActive_Nested& sm, //!< The state machine
          FppTest_SmInitialActive_Nested::Signal signal //!< The signal
      );

    PRIVATE:

      // ----------------------------------------------------------------------
      // State machine instances
      // ----------------------------------------------------------------------

      //! State machine basic1
      FppTest_SmInitialActive_Basic m_stateMachine_basic1;

      //! State machine basic2
      FppTest_SmInitialActive_Basic m_stateMachine_basic2;

      //! State machine choice
      FppTest_SmInitialActive_Choice m_stateMachine_choice;

      //! State machine nested
      FppTest_SmInitialActive_Nested m_stateMachine_nested;

      //! State machine smInitialBasic1
      FppTest_SmInitial_Basic m_stateMachine_smInitialBasic1;

      //! State machine smInitialBasic2
      FppTest_SmInitial_Basic m_stateMachine_smInitialBasic2;

      //! State machine smInitialChoice
      FppTest_SmInitial_Choice m_stateMachine_smInitialChoice;

      //! State machine smInitialNested
      FppTest_SmInitial_Nested m_stateMachine_smInitialNested;

  };

}

#endif