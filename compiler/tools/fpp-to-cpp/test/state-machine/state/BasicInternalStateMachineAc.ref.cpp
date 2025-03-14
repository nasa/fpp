// ======================================================================
// \title  BasicInternalStateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for BasicInternal state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state-machine/state/BasicInternalStateMachineAc.hpp"

namespace FppTest {

  namespace SmState {

    // ----------------------------------------------------------------------
    // Constructors and Destructors
    // ----------------------------------------------------------------------

    BasicInternalStateMachineBase ::
      BasicInternalStateMachineBase()
    {

    }

    BasicInternalStateMachineBase ::
      ~BasicInternalStateMachineBase()
    {

    }

    // ----------------------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------------------

    void BasicInternalStateMachineBase ::
      initBase(const FwEnumStoreType id)
    {
      this->m_id = id;
      // Enter the initial target of the state machine
      this->enter_S(Signal::__FPRIME_AC_INITIAL_TRANSITION);
    }

    // ----------------------------------------------------------------------
    // Getter functions
    // ----------------------------------------------------------------------

    BasicInternalStateMachineBase::State BasicInternalStateMachineBase ::
      getState() const
    {
      return this->m_state;
    }

    // ----------------------------------------------------------------------
    // Send signal functions
    // ----------------------------------------------------------------------

    void BasicInternalStateMachineBase ::
      sendSignal_s()
    {
      switch (this->m_state) {
        case State::S:
          // Do the actions for the transition
          this->action_a(Signal::s);
          break;
        default:
          FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
          break;
      }
    }

    // ----------------------------------------------------------------------
    // State and choice entry
    // ----------------------------------------------------------------------

    void BasicInternalStateMachineBase ::
      enter_S(Signal signal)
    {
      // Do the entry actions
      this->action_a(signal);
      // Update the state
      this->m_state = State::S;
    }

  }

}
