// ======================================================================
// \title  BasicU32StateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for BasicU32 state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state/BasicU32StateMachineAc.hpp"

namespace FppTest {

  namespace SmState {

    // ----------------------------------------------------------------------
    // Constructors and Destructors
    // ----------------------------------------------------------------------

    BasicU32StateMachineBase ::
      BasicU32StateMachineBase()
    {

    }

    BasicU32StateMachineBase ::
      ~BasicU32StateMachineBase()
    {

    }

    // ----------------------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------------------

    void BasicU32StateMachineBase ::
      initBase(const FwEnumStoreType id)
    {
      this->m_id = id;
      this->enter_S(Signal::__FPRIME_AC_INITIAL_TRANSITION);
    }

    // ----------------------------------------------------------------------
    // Getter functions
    // ----------------------------------------------------------------------

    BasicU32StateMachineBase::State BasicU32StateMachineBase ::
      getState() const
    {
      return this->m_state;
    }

    // ----------------------------------------------------------------------
    // Send signal functions
    // ----------------------------------------------------------------------

    void BasicU32StateMachineBase ::
      sendSignal_s(U32 value)
    {
      switch (this->m_state) {
        case State::S:
          this->action_a(Signal::s);
          this->action_a(Signal::s);
          this->action_b(Signal::s, value);
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

    void BasicU32StateMachineBase ::
      enter_T(Signal signal)
    {
      this->action_a(signal);
      this->action_a(signal);
      this->action_a(signal);
      this->m_state = State::T;
    }

    void BasicU32StateMachineBase ::
      enter_S(Signal signal)
    {
      this->m_state = State::S;
    }

  }

}