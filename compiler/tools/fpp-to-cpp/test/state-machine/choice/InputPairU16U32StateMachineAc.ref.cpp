// ======================================================================
// \title  InputPairU16U32StateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for InputPairU16U32 state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state-machine/choice/InputPairU16U32StateMachineAc.hpp"

namespace FppTest {

  namespace SmChoice {

    // ----------------------------------------------------------------------
    // Constructors and Destructors
    // ----------------------------------------------------------------------

    InputPairU16U32StateMachineBase ::
      InputPairU16U32StateMachineBase()
    {

    }

    InputPairU16U32StateMachineBase ::
      ~InputPairU16U32StateMachineBase()
    {

    }

    // ----------------------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------------------

    void InputPairU16U32StateMachineBase ::
      initBase(const FwEnumStoreType id)
    {
      this->m_id = id;
      this->enter_S1(Signal::__FPRIME_AC_INITIAL_TRANSITION);
    }

    // ----------------------------------------------------------------------
    // Getter functions
    // ----------------------------------------------------------------------

    InputPairU16U32StateMachineBase::State InputPairU16U32StateMachineBase ::
      getState() const
    {
      return this->m_state;
    }

    // ----------------------------------------------------------------------
    // Send signal functions
    // ----------------------------------------------------------------------

    void InputPairU16U32StateMachineBase ::
      sendSignal_s1(U16 value)
    {
      switch (this->m_state) {
        case State::S1:
          this->enter_C(Signal::s1, value);
          break;
        case State::S2:
          break;
        case State::S3:
          break;
        default:
          FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
          break;
      }
    }

    void InputPairU16U32StateMachineBase ::
      sendSignal_s2(U32 value)
    {
      switch (this->m_state) {
        case State::S1:
          this->enter_C(Signal::s2, value);
          break;
        case State::S2:
          break;
        case State::S3:
          break;
        default:
          FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
          break;
      }
    }

    // ----------------------------------------------------------------------
    // State and junction entry
    // ----------------------------------------------------------------------

    void InputPairU16U32StateMachineBase ::
      enter_S3(Signal signal)
    {
      this->m_state = State::S3;
    }

    void InputPairU16U32StateMachineBase ::
      enter_S2(Signal signal)
    {
      this->m_state = State::S2;
    }

    void InputPairU16U32StateMachineBase ::
      enter_C(
          Signal signal,
          U32 value
      )
    {
      if (this->guard_g(signal, value)) {
        this->action_a(signal, value);
        this->enter_S2(signal);
      }
      else {
        this->action_a(signal, value);
        this->enter_S3(signal);
      }
    }

    void InputPairU16U32StateMachineBase ::
      enter_S1(Signal signal)
    {
      this->m_state = State::S1;
    }

  }

}
