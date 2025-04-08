// ======================================================================
// \title  QueuedNoArgsPortsOnlyComponentAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for QueuedNoArgsPortsOnly component base class
// ======================================================================

#ifndef QueuedNoArgsPortsOnlyComponentAc_HPP
#define QueuedNoArgsPortsOnlyComponentAc_HPP

#include "Fw/Comp/ActiveComponentBase.hpp"
#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Port/OutputSerializePort.hpp"
#include "NoArgsPortAc.hpp"
#include "NoArgsReturnPortAc.hpp"
#include "Os/Mutex.hpp"

//! \class QueuedNoArgsPortsOnlyComponentBase
//! \brief Auto-generated base for QueuedNoArgsPortsOnly component
//!
//! An queued component with only ports without arguments
class QueuedNoArgsPortsOnlyComponentBase :
  public Fw::QueuedComponentBase
{

    // ----------------------------------------------------------------------
    // Friend classes
    // ----------------------------------------------------------------------

    //! Friend class for white-box testing
    friend class QueuedNoArgsPortsOnlyComponentBaseFriend;
    //! Friend class tester to support autocoded test harness
    friend class QueuedNoArgsPortsOnlyTesterBase;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    //! Enumerations for numbers of typed input ports
    enum {
      NUM_NOARGSASYNC_INPUT_PORTS = 3,
      NUM_NOARGSGUARDED_INPUT_PORTS = 1,
      NUM_NOARGSRETURNGUARDED_INPUT_PORTS = 1,
      NUM_NOARGSRETURNSYNC_INPUT_PORTS = 3,
    };

    //! Enumerations for numbers of typed output ports
    enum {
      NUM_NOARGSOUT_OUTPUT_PORTS = 1,
      NUM_NOARGSRETURNOUT_OUTPUT_PORTS = 1,
    };

  public:

    // ----------------------------------------------------------------------
    // Component initialization
    // ----------------------------------------------------------------------

    //! Initialize QueuedNoArgsPortsOnlyComponentBase object
    void init(
        FwSizeType queueDepth, //!< The queue depth
        FwEnumStoreType instance = 0 //!< The instance number
    );

  public:

    // ----------------------------------------------------------------------
    // Getters for typed input ports
    // ----------------------------------------------------------------------

    //! Get typed input port at index
    //!
    //! \return noArgsAsync[portNum]
    Ports::InputNoArgsPort* get_noArgsAsync_InputPort(
        FwIndexType portNum //!< The port number
    );

    //! Get typed input port at index
    //!
    //! \return noArgsGuarded[portNum]
    Ports::InputNoArgsPort* get_noArgsGuarded_InputPort(
        FwIndexType portNum //!< The port number
    );

    //! Get typed input port at index
    //!
    //! \return noArgsReturnGuarded[portNum]
    Ports::InputNoArgsReturnPort* get_noArgsReturnGuarded_InputPort(
        FwIndexType portNum //!< The port number
    );

    //! Get typed input port at index
    //!
    //! \return noArgsReturnSync[portNum]
    Ports::InputNoArgsReturnPort* get_noArgsReturnSync_InputPort(
        FwIndexType portNum //!< The port number
    );

  public:

    // ----------------------------------------------------------------------
    // Connect typed input ports to typed output ports
    // ----------------------------------------------------------------------

    //! Connect port to noArgsOut[portNum]
    void set_noArgsOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsPort* port //!< The input port
    );

    //! Connect port to noArgsReturnOut[portNum]
    void set_noArgsReturnOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsReturnPort* port //!< The input port
    );

#if FW_PORT_SERIALIZATION

  public:

    // ----------------------------------------------------------------------
    // Connect serial input ports to typed output ports
    // ----------------------------------------------------------------------

    //! Connect port to noArgsOut[portNum]
    void set_noArgsOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Fw::InputSerializePort* port //!< The port
    );

#endif

  PROTECTED:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct QueuedNoArgsPortsOnlyComponentBase object
    QueuedNoArgsPortsOnlyComponentBase(
        const char* compName = "" //!< The component name
    );

    //! Destroy QueuedNoArgsPortsOnlyComponentBase object
    virtual ~QueuedNoArgsPortsOnlyComponentBase();

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of typed input ports
    // ----------------------------------------------------------------------

    //! Get the number of noArgsAsync input ports
    //!
    //! \return The number of noArgsAsync input ports
    FwIndexType getNum_noArgsAsync_InputPorts() const;

    //! Get the number of noArgsGuarded input ports
    //!
    //! \return The number of noArgsGuarded input ports
    FwIndexType getNum_noArgsGuarded_InputPorts() const;

    //! Get the number of noArgsReturnGuarded input ports
    //!
    //! \return The number of noArgsReturnGuarded input ports
    FwIndexType getNum_noArgsReturnGuarded_InputPorts() const;

    //! Get the number of noArgsReturnSync input ports
    //!
    //! \return The number of noArgsReturnSync input ports
    FwIndexType getNum_noArgsReturnSync_InputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of typed output ports
    // ----------------------------------------------------------------------

    //! Get the number of noArgsOut output ports
    //!
    //! \return The number of noArgsOut output ports
    FwIndexType getNum_noArgsOut_OutputPorts() const;

    //! Get the number of noArgsReturnOut output ports
    //!
    //! \return The number of noArgsReturnOut output ports
    FwIndexType getNum_noArgsReturnOut_OutputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Connection status queries for typed output ports
    // ----------------------------------------------------------------------

    //! Check whether port noArgsOut is connected
    //!
    //! \return Whether port noArgsOut is connected
    bool isConnected_noArgsOut_OutputPort(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port noArgsReturnOut is connected
    //!
    //! \return Whether port noArgsReturnOut is connected
    bool isConnected_noArgsReturnOut_OutputPort(
        FwIndexType portNum //!< The port number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Handlers to implement for typed input ports
    // ----------------------------------------------------------------------

    //! Handler for input port noArgsAsync
    virtual void noArgsAsync_handler(
        FwIndexType portNum //!< The port number
    ) = 0;

    //! Handler for input port noArgsGuarded
    virtual void noArgsGuarded_handler(
        FwIndexType portNum //!< The port number
    ) = 0;

    //! Handler for input port noArgsReturnGuarded
    virtual U32 noArgsReturnGuarded_handler(
        FwIndexType portNum //!< The port number
    ) = 0;

    //! Handler for input port noArgsReturnSync
    virtual U32 noArgsReturnSync_handler(
        FwIndexType portNum //!< The port number
    ) = 0;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Port handler base-class functions for typed input ports
    //
    // Call these functions directly to bypass the corresponding ports
    // ----------------------------------------------------------------------

    //! Handler base-class function for input port noArgsAsync
    void noArgsAsync_handlerBase(
        FwIndexType portNum //!< The port number
    );

    //! Handler base-class function for input port noArgsGuarded
    void noArgsGuarded_handlerBase(
        FwIndexType portNum //!< The port number
    );

    //! Handler base-class function for input port noArgsReturnGuarded
    U32 noArgsReturnGuarded_handlerBase(
        FwIndexType portNum //!< The port number
    );

    //! Handler base-class function for input port noArgsReturnSync
    U32 noArgsReturnSync_handlerBase(
        FwIndexType portNum //!< The port number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Pre-message hooks for typed async input ports
    //
    // Each of these functions is invoked just before processing a message
    // on the corresponding port. By default, they do nothing. You can
    // override them to provide specific pre-message behavior.
    // ----------------------------------------------------------------------

    //! Pre-message hook for async input port noArgsAsync
    virtual void noArgsAsync_preMsgHook(
        FwIndexType portNum //!< The port number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Invocation functions for typed output ports
    // ----------------------------------------------------------------------

    //! Invoke output port noArgsOut
    void noArgsOut_out(
        FwIndexType portNum //!< The port number
    );

    //! Invoke output port noArgsReturnOut
    U32 noArgsReturnOut_out(
        FwIndexType portNum //!< The port number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Mutex operations for guarded ports
    //
    // You can override these operations to provide more sophisticated
    // synchronization
    // ----------------------------------------------------------------------

    //! Lock the guarded mutex
    virtual void lock();

    //! Unlock the guarded mutex
    virtual void unLock();

  PROTECTED:

    // ----------------------------------------------------------------------
    // Message dispatch functions
    // ----------------------------------------------------------------------

    //! Called in the message loop to dispatch a message from the queue
    virtual MsgDispatchStatus doDispatch();

  protected:

    // ----------------------------------------------------------------------
    // Helper functions for dispatching current messages
    // ----------------------------------------------------------------------

    //! Dispatch all current messages unless ERROR or EXIT occurs
    MsgDispatchStatus dispatchCurrentMessages();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Calls for messages received on typed input ports
    // ----------------------------------------------------------------------

    //! Callback for port noArgsAsync
    static void m_p_noArgsAsync_in(
        Fw::PassiveComponentBase* callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

    //! Callback for port noArgsGuarded
    static void m_p_noArgsGuarded_in(
        Fw::PassiveComponentBase* callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

    //! Callback for port noArgsReturnGuarded
    static U32 m_p_noArgsReturnGuarded_in(
        Fw::PassiveComponentBase* callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

    //! Callback for port noArgsReturnSync
    static U32 m_p_noArgsReturnSync_in(
        Fw::PassiveComponentBase* callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Typed input ports
    // ----------------------------------------------------------------------

    //! Input port noArgsAsync
    Ports::InputNoArgsPort m_noArgsAsync_InputPort[NUM_NOARGSASYNC_INPUT_PORTS];

    //! Input port noArgsGuarded
    Ports::InputNoArgsPort m_noArgsGuarded_InputPort[NUM_NOARGSGUARDED_INPUT_PORTS];

    //! Input port noArgsReturnGuarded
    Ports::InputNoArgsReturnPort m_noArgsReturnGuarded_InputPort[NUM_NOARGSRETURNGUARDED_INPUT_PORTS];

    //! Input port noArgsReturnSync
    Ports::InputNoArgsReturnPort m_noArgsReturnSync_InputPort[NUM_NOARGSRETURNSYNC_INPUT_PORTS];

  PRIVATE:

    // ----------------------------------------------------------------------
    // Typed output ports
    // ----------------------------------------------------------------------

    //! Output port noArgsOut
    Ports::OutputNoArgsPort m_noArgsOut_OutputPort[NUM_NOARGSOUT_OUTPUT_PORTS];

    //! Output port noArgsReturnOut
    Ports::OutputNoArgsReturnPort m_noArgsReturnOut_OutputPort[NUM_NOARGSRETURNOUT_OUTPUT_PORTS];

  PRIVATE:

    // ----------------------------------------------------------------------
    // Mutexes
    // ----------------------------------------------------------------------

    //! Mutex for guarded ports
    Os::Mutex m_guardedPortMutex;

};

#endif
