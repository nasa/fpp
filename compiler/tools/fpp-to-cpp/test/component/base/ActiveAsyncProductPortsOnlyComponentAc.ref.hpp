// ======================================================================
// \title  ActiveAsyncProductPortsOnlyComponentAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for ActiveAsyncProductPortsOnly component base class
// ======================================================================

#ifndef ActiveAsyncProductPortsOnlyComponentAc_HPP
#define ActiveAsyncProductPortsOnlyComponentAc_HPP

#include "Fw/Comp/ActiveComponentBase.hpp"
#include "Fw/Dp/DpRequestPortAc.hpp"
#include "Fw/Dp/DpResponsePortAc.hpp"
#include "Fw/Dp/DpSendPortAc.hpp"
#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Port/OutputSerializePort.hpp"

//! \class ActiveAsyncProductPortsOnlyComponentBase
//! \brief Auto-generated base for ActiveAsyncProductPortsOnly component
//!
//! An active component with async product request and ports only
class ActiveAsyncProductPortsOnlyComponentBase :
  public Fw::ActiveComponentBase
{

    // ----------------------------------------------------------------------
    // Friend classes
    // ----------------------------------------------------------------------

    //! Friend class for white-box testing
    friend class ActiveAsyncProductPortsOnlyComponentBaseFriend;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    //! Enumerations for numbers of special input ports
    enum {
      NUM_PRODUCTRECVIN_INPUT_PORTS = 1,
    };

    //! Enumerations for numbers of special output ports
    enum {
      NUM_PRODUCTREQUESTOUT_OUTPUT_PORTS = 1,
      NUM_PRODUCTSENDOUT_OUTPUT_PORTS = 1,
    };

  public:

    // ----------------------------------------------------------------------
    // Component initialization
    // ----------------------------------------------------------------------

    //! Initialize ActiveAsyncProductPortsOnlyComponentBase object
    void init(
        FwSizeType queueDepth, //!< The queue depth
        FwEnumStoreType instance = 0 //!< The instance number
    );

  public:

    // ----------------------------------------------------------------------
    // Getters for special input ports
    // ----------------------------------------------------------------------

    //! Get special input port at index
    //!
    //! \return productRecvIn[portNum]
    Fw::InputDpResponsePort* get_productRecvIn_InputPort(
        FwIndexType portNum //!< The port number
    );

  public:

    // ----------------------------------------------------------------------
    // Connect input ports to special output ports
    // ----------------------------------------------------------------------

    //! Connect port to productRequestOut[portNum]
    void set_productRequestOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Fw::InputDpRequestPort* port //!< The input port
    );

    //! Connect port to productSendOut[portNum]
    void set_productSendOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Fw::InputDpSendPort* port //!< The input port
    );

#if FW_PORT_SERIALIZATION

  public:

    // ----------------------------------------------------------------------
    // Connect serial input ports to special output ports
    // ----------------------------------------------------------------------

    //! Connect port to productRequestOut[portNum]
    void set_productRequestOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Fw::InputSerializePort* port //!< The port
    );

    //! Connect port to productSendOut[portNum]
    void set_productSendOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Fw::InputSerializePort* port //!< The port
    );

#endif

  PROTECTED:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveAsyncProductPortsOnlyComponentBase object
    ActiveAsyncProductPortsOnlyComponentBase(
        const char* compName = "" //!< The component name
    );

    //! Destroy ActiveAsyncProductPortsOnlyComponentBase object
    virtual ~ActiveAsyncProductPortsOnlyComponentBase();

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of special input ports
    // ----------------------------------------------------------------------

    //! Get the number of productRecvIn input ports
    //!
    //! \return The number of productRecvIn input ports
    FwIndexType getNum_productRecvIn_InputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of special output ports
    // ----------------------------------------------------------------------

    //! Get the number of productRequestOut output ports
    //!
    //! \return The number of productRequestOut output ports
    FwIndexType getNum_productRequestOut_OutputPorts() const;

    //! Get the number of productSendOut output ports
    //!
    //! \return The number of productSendOut output ports
    FwIndexType getNum_productSendOut_OutputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Connection status queries for special output ports
    // ----------------------------------------------------------------------

    //! Check whether port productRequestOut is connected
    //!
    //! \return Whether port productRequestOut is connected
    bool isConnected_productRequestOut_OutputPort(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port productSendOut is connected
    //!
    //! \return Whether port productSendOut is connected
    bool isConnected_productSendOut_OutputPort(
        FwIndexType portNum //!< The port number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Port handler base-class functions for special input ports
    //
    // Call these functions directly to bypass the corresponding ports
    // ----------------------------------------------------------------------

    //! Handler base-class function for input port productRecvIn
    void productRecvIn_handlerBase(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer, //!< The buffer
        const Fw::Success& status //!< The status
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Pre-message hooks for special async input ports
    //
    // Each of these functions is invoked just before processing a message
    // on the corresponding port. By default, they do nothing. You can
    // override them to provide specific pre-message behavior.
    // ----------------------------------------------------------------------

    //! Pre-message hook for async input port productRecvIn
    virtual void productRecvIn_preMsgHook(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer, //!< The buffer
        const Fw::Success& status //!< The status
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Invocation functions for special output ports
    // ----------------------------------------------------------------------

    //! Invoke output port productRequestOut
    void productRequestOut_out(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        FwSizeType dataSize //!< The data size of the requested buffer
    );

    //! Invoke output port productSendOut
    void productSendOut_out(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer //!< The buffer
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Message dispatch functions
    // ----------------------------------------------------------------------

    //! Called in the message loop to dispatch a message from the queue
    virtual MsgDispatchStatus doDispatch();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Calls for messages received on special input ports
    // ----------------------------------------------------------------------

    //! Callback for port productRecvIn
    static void m_p_productRecvIn_in(
        Fw::PassiveComponentBase* callComp, //!< The component instance
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer, //!< The buffer
        const Fw::Success& status //!< The status
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Private data product handling functions
    // ----------------------------------------------------------------------

    //! Handler implementation for productRecvIn
    void productRecvIn_handler(
        const FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container id
        const Fw::Buffer& buffer, //!< The buffer
        const Fw::Success& status //!< The buffer status
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Special input ports
    // ----------------------------------------------------------------------

    //! Input port productRecvIn
    Fw::InputDpResponsePort m_productRecvIn_InputPort[NUM_PRODUCTRECVIN_INPUT_PORTS];

  PRIVATE:

    // ----------------------------------------------------------------------
    // Special output ports
    // ----------------------------------------------------------------------

    //! Output port productRequestOut
    Fw::OutputDpRequestPort m_productRequestOut_OutputPort[NUM_PRODUCTREQUESTOUT_OUTPUT_PORTS];

    //! Output port productSendOut
    Fw::OutputDpSendPort m_productSendOut_OutputPort[NUM_PRODUCTSENDOUT_OUTPUT_PORTS];

};

#endif
