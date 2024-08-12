// ======================================================================
// \title  ActiveStateMachinesComponentAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for ActiveStateMachines component base class
// ======================================================================

#ifndef ActiveStateMachinesComponentAc_HPP
#define ActiveStateMachinesComponentAc_HPP

#include "ActiveStateMachines_S1.hpp"
#include "ActiveStateMachines_S2.hpp"
#include "FpConfig.hpp"
#include "Fw/Comp/ActiveComponentBase.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Port/OutputSerializePort.hpp"
#include "Fw/Types/SMSignalsSerializableAc.hpp"

//! \class ActiveStateMachinesComponentBase
//! \brief Auto-generated base for ActiveStateMachines component
//!
//! An active component with state machines
class ActiveStateMachinesComponentBase :
  public Fw::ActiveComponentBase, public ActiveStateMachines_S1_Interface, public ActiveStateMachines_S2_Interface
{

    // ----------------------------------------------------------------------
    // Friend classes
    // ----------------------------------------------------------------------

    //! Friend class for white-box testing
    friend class ActiveStateMachinesComponentBaseFriend;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    //! State machine identifiers
    enum SmId {
      STATE_MACHINE_SM1,
      STATE_MACHINE_SM2,
      STATE_MACHINE_SM3,
      STATE_MACHINE_SM4,
      STATE_MACHINE_SM5,
      STATE_MACHINE_SM6,
    };

  public:

    // ----------------------------------------------------------------------
    // Component initialization
    // ----------------------------------------------------------------------

    //! Initialize ActiveStateMachinesComponentBase object
    void init(
        FwQueueSizeType queueDepth, //!< The queue depth
        FwEnumStoreType instance = 0 //!< The instance number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveStateMachinesComponentBase object
    ActiveStateMachinesComponentBase(
        const char* compName = "" //!< The component name
    );

    //! Destroy ActiveStateMachinesComponentBase object
    virtual ~ActiveStateMachinesComponentBase();

  PROTECTED:

    // ----------------------------------------------------------------------
    // State machine function to push signals to the input queue
    // ----------------------------------------------------------------------

    //! State machine base-class function for sendSignals
    void stateMachineInvoke(
        const Fw::SMSignals& ev //!< The state machine signal
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Message dispatch functions
    // ----------------------------------------------------------------------

    //! Called in the message loop to dispatch a message from the queue
    virtual MsgDispatchStatus doDispatch();

  PRIVATE:

    // ----------------------------------------------------------------------
    // State machine instances
    // ----------------------------------------------------------------------

    //! State machine sm1
    ActiveStateMachines_S1 m_stateMachine_sm1;

    //! State machine sm2
    ActiveStateMachines_S1 m_stateMachine_sm2;

    //! State machine sm3
    ActiveStateMachines_S2 m_stateMachine_sm3;

    //! State machine sm4
    ActiveStateMachines_S2 m_stateMachine_sm4;

    //! State machine sm5
    ActiveStateMachines_S2 m_stateMachine_sm5;

    //! State machine sm6
    ActiveStateMachines_S2 m_stateMachine_sm6;

};

#endif
