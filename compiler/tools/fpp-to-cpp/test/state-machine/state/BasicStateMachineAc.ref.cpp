// ======================================================================
// \title  BasicStateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Basic state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state-machine/state/BasicStateMachineAc.hpp"

namespace FppTest {

  namespace SmState {

    // ----------------------------------------------------------------------
    // Constructors and Destructors
    // ----------------------------------------------------------------------

    BasicStateMachineBase ::
      BasicStateMachineBase()
    {

    }

    BasicStateMachineBase ::
      ~BasicStateMachineBase()
    {

    }

    // ----------------------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------------------

    void BasicStateMachineBase ::
      initBase(const FwEnumStoreType id)
    {
      this->m_id = id;
      this->enter_S(Signal::__FPRIME_AC_INITIAL_TRANSITION);
    }

    // ----------------------------------------------------------------------
    // Getter functions
    // ----------------------------------------------------------------------

    BasicStateMachineBase::State BasicStateMachineBase ::
      getState() const
    {
      return this->m_state;
    }

    // ----------------------------------------------------------------------
    // Send signal functions
    // ----------------------------------------------------------------------

    void BasicStateMachineBase ::
      sendSignal_s()
    {
      switch (this->m_state) {
        case State::S:
          this->action_a(Signal::s);
          this->action_a(Signal::s);
          this->action_a(Signal::s);
          this->enter_T(Signal::s);
          break;
        case State::T:
          break;
        default:
          FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
          break;
      }
    }

    // ----------------------------------------------------------------------
    // State and junction entry
    // ----------------------------------------------------------------------

    void BasicStateMachineBase ::
      enter_T(Signal signal)
    {
      this->action_a(signal);
      this->action_a(signal);
      this->action_a(signal);
      this->m_state = State::T;
    }

    void BasicStateMachineBase ::
      enter_S(Signal signal)
    {
      this->m_state = State::S;
    }

  }

}