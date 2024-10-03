// ======================================================================
// \title  BasicGuardStringStateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for BasicGuardString state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state/BasicGuardStringStateMachineAc.hpp"

namespace FppTest {

  namespace SmState {

    // ----------------------------------------------------------------------
    // Constructors and Destructors
    // ----------------------------------------------------------------------

    BasicGuardStringStateMachineBase ::
      BasicGuardStringStateMachineBase()
    {

    }

    BasicGuardStringStateMachineBase ::
      ~BasicGuardStringStateMachineBase()
    {

    }

    // ----------------------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------------------

    void BasicGuardStringStateMachineBase ::
      init(const FwEnumStoreType id)
    {
      this->m_id = id;
      this->enter_S(Signal::__FPRIME_AC_INITIAL_TRANSITION);
    }

    // ----------------------------------------------------------------------
    // Send signal functions
    // ----------------------------------------------------------------------

    void BasicGuardStringStateMachineBase ::
      sendSignal_s(const Fw::StringBase& value)
    {
      switch (this->m_state) {
        case State::S:
          if (this->guard_g(Signal::s, value)) {
            this->action_a(Signal::s, value);
            this->enter_T(Signal::s);
          }
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

    void BasicGuardStringStateMachineBase ::
      enter_T(Signal signal)
    {
      this->m_state = State::T;
    }

    void BasicGuardStringStateMachineBase ::
      enter_S(Signal signal)
    {
      this->m_state = State::S;
    }

  }

}
