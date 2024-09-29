// ======================================================================
// \title  PolymorphismStateMachineAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Polymorphism state machine
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "state/PolymorphismStateMachineAc.hpp"

namespace FppTest {

  namespace SmState {

    // ----------------------------------------------------------------------
    // Constructors and Destructors
    // ----------------------------------------------------------------------

    PolymorphismStateMachineBase ::
      PolymorphismStateMachineBase()
    {

    }

    PolymorphismStateMachineBase ::
      ~PolymorphismStateMachineBase()
    {

    }

    // ----------------------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------------------

    void PolymorphismStateMachineBase ::
      init(const FwEnumStoreType id)
    {
      this->m_id = id;
      this->enter_S1(Signal::__FPRIME_AC_INITIAL_TRANSITION);
    }

    // ----------------------------------------------------------------------
    // Send signal functions
    // ----------------------------------------------------------------------

    void PolymorphismStateMachineBase ::
      sendSignal_poly()
    {
      switch (this->m_state) {
        case State::S1_S2:
          this->enter_S4(Signal::poly);
          break;
        case State::S1_S3:
          this->enter_S5(Signal::poly);
          break;
        case State::S4:
          break;
        case State::S5:
          break;
        default:
          FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
          break;
      }
    }

    void PolymorphismStateMachineBase ::
      sendSignal_S2_to_S3()
    {
      switch (this->m_state) {
        case State::S1_S2:
          this->enter_S1_S3(Signal::S2_to_S3);
          break;
        case State::S1_S3:
          break;
        case State::S4:
          break;
        case State::S5:
          break;
        default:
          FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
          break;
      }
    }

    // ----------------------------------------------------------------------
    // State and junction entry
    // ----------------------------------------------------------------------

    void PolymorphismStateMachineBase ::
      enter_S5(Signal signal)
    {
      this->m_state = State::S5;
    }

    void PolymorphismStateMachineBase ::
      enter_S4(Signal signal)
    {
      this->m_state = State::S4;
    }

    void PolymorphismStateMachineBase ::
      enter_S1(Signal signal)
    {
      this->enter_S1_S2(signal);
    }

    void PolymorphismStateMachineBase ::
      enter_S1_S2(Signal signal)
    {
      this->m_state = State::S1_S2;
    }

    void PolymorphismStateMachineBase ::
      enter_S1_S3(Signal signal)
    {
      this->m_state = State::S1_S3;
    }

  }

}
