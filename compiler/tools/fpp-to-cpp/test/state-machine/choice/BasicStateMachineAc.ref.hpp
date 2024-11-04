// ======================================================================
// \title  BasicStateMachineAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Basic state machine
// ======================================================================

#ifndef FppTest_SmChoice_BasicStateMachineAc_HPP
#define FppTest_SmChoice_BasicStateMachineAc_HPP

#include <FpConfig.hpp>

#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"

namespace FppTest {

  namespace SmChoice {

    //! A basic state machine with a choice
    class BasicStateMachineBase {

      public:

        // ----------------------------------------------------------------------
        // Types
        // ----------------------------------------------------------------------

        //! The state type
        enum class State : FwEnumStoreType {
          //! The uninitialized state
          __FPRIME_AC_UNINITIALIZED,
          //! State S1
          S1,
          //! State S2
          S2,
          //! State S3
          S3,
        };

        //! The signal type
        enum class Signal : FwEnumStoreType {
          //! The initial transition
          __FPRIME_AC_INITIAL_TRANSITION,
          //! Signal s
          s,
        };

      PROTECTED:

        // ----------------------------------------------------------------------
        // Constructors and Destructors
        // ----------------------------------------------------------------------

        //! Constructor
        BasicStateMachineBase();

        //! Destructor
        virtual ~BasicStateMachineBase();

      protected:

        // ----------------------------------------------------------------------
        // Initialization
        // ----------------------------------------------------------------------

        //! Initialize the state machine
        void initBase(
            const FwEnumStoreType id //!< The state machine ID
        );

      public:

        // ----------------------------------------------------------------------
        // Getter functions
        // ----------------------------------------------------------------------

        //! Get the state
        BasicStateMachineBase::State getState() const;

      public:

        // ----------------------------------------------------------------------
        // Send signal functions
        // ----------------------------------------------------------------------

        //! Signal s
        void sendSignal_s();

      PROTECTED:

        // ----------------------------------------------------------------------
        // Actions
        // ----------------------------------------------------------------------

        //! Action a
        virtual void action_a(
            Signal signal //!< The signal
        ) = 0;

        //! Action b
        virtual void action_b(
            Signal signal //!< The signal
        ) = 0;

      PROTECTED:

        // ----------------------------------------------------------------------
        // Guards
        // ----------------------------------------------------------------------

        //! Guard g
        virtual bool guard_g(
            Signal signal //!< The signal
        ) const = 0;

      PRIVATE:

        // ----------------------------------------------------------------------
        // State and junction entry
        // ----------------------------------------------------------------------

        //! Enter state S3
        void enter_S3(
            Signal signal //!< The signal
        );

        //! Enter state S2
        void enter_S2(
            Signal signal //!< The signal
        );

        //! Enter junction C
        void enter_C(
            Signal signal //!< The signal
        );

        //! Enter state S1
        void enter_S1(
            Signal signal //!< The signal
        );

      PROTECTED:

        // ----------------------------------------------------------------------
        // Member variables
        // ----------------------------------------------------------------------

        //! The state machine ID
        FwEnumStoreType m_id = 0;

        //! The state
        State m_state = State::__FPRIME_AC_UNINITIALIZED;

    };

  }

}

#endif
