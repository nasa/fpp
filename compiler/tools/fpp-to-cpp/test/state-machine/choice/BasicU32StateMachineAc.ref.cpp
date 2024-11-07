// ======================================================================
// \title  BasicU32StateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for BasicU32 state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state-machine/choice/BasicU32StateMachineAc.hpp"

namespace FppTest {

  namespace SmChoice {

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
      this->enter_S1(Signal::__FPRIME_AC_INITIAL_TRANSITION);
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
        case State::S1:
          this->enter_C(Signal::s, value);
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
    // State and choice entry
    // ----------------------------------------------------------------------

    void BasicU32StateMachineBase ::
      enter_S3(Signal signal)
    {
      this->m_state = State::S3;
    }

    void BasicU32StateMachineBase ::
      enter_S2(Signal signal)
    {
      this->m_state = State::S2;
    }

    void BasicU32StateMachineBase ::
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
        this->action_b(signal);
        this->enter_S3(signal);
      }
    }

    void BasicU32StateMachineBase ::
      enter_S1(Signal signal)
    {
      this->m_state = State::S1;
    }

  }

}