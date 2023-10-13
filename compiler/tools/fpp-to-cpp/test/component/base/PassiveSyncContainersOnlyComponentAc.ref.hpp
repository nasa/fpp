// ======================================================================
// \title  PassiveSyncContainersOnlyComponentAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for PassiveSyncContainersOnly component base class
// ======================================================================

#ifndef PassiveSyncContainersOnlyComponentAc_HPP
#define PassiveSyncContainersOnlyComponentAc_HPP

#include "FpConfig.hpp"
#include "Fw/Comp/ActiveComponentBase.hpp"
#include "Fw/Dp/DpContainer.hpp"
#include "Fw/Dp/DpRequestPortAc.hpp"
#include "Fw/Dp/DpResponsePortAc.hpp"
#include "Fw/Dp/DpSendPortAc.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Port/OutputSerializePort.hpp"
#include "Fw/Time/TimePortAc.hpp"

//! \class PassiveSyncContainersOnlyComponentBase
//! \brief Auto-generated base for PassiveSyncContainersOnly component
//!
//! A passive component with sync product request and containers only
class PassiveSyncContainersOnlyComponentBase :
  public Fw::PassiveComponentBase
{

    // ----------------------------------------------------------------------
    // Friend classes
    // ----------------------------------------------------------------------

    //! Friend class for white-box testing
    friend class PassiveSyncContainersOnlyComponentBaseFriend;

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
      NUM_TIMEGETOUT_OUTPUT_PORTS = 1,
    };

  PROTECTED:

    // ----------------------------------------------------------------------
    // Types for data products
    // ----------------------------------------------------------------------

    //! The container ids
    struct ContainerId {
      enum T : FwDpIdType {
        Container1 = 100,
      };
    };

    //! The container default priorities
    struct ContainerPriority {
      enum T : FwDpPriorityType {
        Container1 = 10,
      };
    };

    //! A data product container
    class DpContainer :
      public Fw::DpContainer
    {

      public:

        //! Constructor with custom initialization
        DpContainer(
            FwDpIdType id, //!< The container id
            const Fw::Buffer& buffer, //!< The packet buffer
            FwDpIdType baseId //!< The component base id
        );

        //! Constructor with default initialization
        DpContainer();

      public:

        FwDpIdType getBaseId() const { return this->baseId; }

        void setBaseId(FwDpIdType baseId) { this->baseId = baseId; }

      PRIVATE:

        //! The component base id
        FwDpIdType baseId;

    };

  public:

    // ----------------------------------------------------------------------
    // Component initialization
    // ----------------------------------------------------------------------

    //! Initialize PassiveSyncContainersOnlyComponentBase object
    void init(
        NATIVE_INT_TYPE instance = 0 //!< The instance number
    );

  public:

    // ----------------------------------------------------------------------
    // Getters for special input ports
    // ----------------------------------------------------------------------

    //! Get special input port at index
    //!
    //! \return productRecvIn[portNum]
    Fw::InputDpResponsePort* get_productRecvIn_InputPort(
        NATIVE_INT_TYPE portNum //!< The port number
    );

  public:

    // ----------------------------------------------------------------------
    // Connect input ports to special output ports
    // ----------------------------------------------------------------------

    //! Connect port to productRequestOut[portNum]
    void set_productRequestOut_OutputPort(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::InputDpRequestPort* port //!< The input port
    );

    //! Connect port to productSendOut[portNum]
    void set_productSendOut_OutputPort(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::InputDpSendPort* port //!< The input port
    );

    //! Connect port to timeGetOut[portNum]
    void set_timeGetOut_OutputPort(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::InputTimePort* port //!< The input port
    );

#if FW_PORT_SERIALIZATION

  public:

    // ----------------------------------------------------------------------
    // Connect serial input ports to special output ports
    // ----------------------------------------------------------------------

    //! Connect port to productRequestOut[portNum]
    void set_productRequestOut_OutputPort(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::InputSerializePort* port //!< The port
    );

    //! Connect port to productSendOut[portNum]
    void set_productSendOut_OutputPort(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::InputSerializePort* port //!< The port
    );

    //! Connect port to timeGetOut[portNum]
    void set_timeGetOut_OutputPort(
        NATIVE_INT_TYPE portNum, //!< The port number
        Fw::InputSerializePort* port //!< The port
    );

#endif

  PROTECTED:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct PassiveSyncContainersOnlyComponentBase object
    PassiveSyncContainersOnlyComponentBase(
        const char* compName = "" //!< The component name
    );

    //! Destroy PassiveSyncContainersOnlyComponentBase object
    virtual ~PassiveSyncContainersOnlyComponentBase();

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of special input ports
    // ----------------------------------------------------------------------

    //! Get the number of productRecvIn input ports
    //!
    //! \return The number of productRecvIn input ports
    NATIVE_INT_TYPE getNum_productRecvIn_InputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of special output ports
    // ----------------------------------------------------------------------

    //! Get the number of productRequestOut output ports
    //!
    //! \return The number of productRequestOut output ports
    NATIVE_INT_TYPE getNum_productRequestOut_OutputPorts() const;

    //! Get the number of productSendOut output ports
    //!
    //! \return The number of productSendOut output ports
    NATIVE_INT_TYPE getNum_productSendOut_OutputPorts() const;

    //! Get the number of timeGetOut output ports
    //!
    //! \return The number of timeGetOut output ports
    NATIVE_INT_TYPE getNum_timeGetOut_OutputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Connection status queries for special output ports
    // ----------------------------------------------------------------------

    //! Check whether port productRequestOut is connected
    //!
    //! \return Whether port productRequestOut is connected
    bool isConnected_productRequestOut_OutputPort(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Check whether port productSendOut is connected
    //!
    //! \return Whether port productSendOut is connected
    bool isConnected_productSendOut_OutputPort(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Check whether port timeGetOut is connected
    //!
    //! \return Whether port timeGetOut is connected
    bool isConnected_timeGetOut_OutputPort(
        NATIVE_INT_TYPE portNum //!< The port number
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Port handler base-class functions for special input ports
    //
    // Call these functions directly to bypass the corresponding ports
    // ----------------------------------------------------------------------

    //! Handler base-class function for input port productRecvIn
    void productRecvIn_handlerBase(
        NATIVE_INT_TYPE portNum, //!< The port number
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
        NATIVE_INT_TYPE portNum, //!< The port number
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
        NATIVE_INT_TYPE portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        FwSizeType size //!< The size of the requested buffer
    );

    //! Invoke output port productSendOut
    void productSendOut_out(
        NATIVE_INT_TYPE portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer //!< The buffer
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Functions for managing data products
    // ----------------------------------------------------------------------

    //! Request a Container1 container
    void dpRequest_Container1(
        FwSizeType size //!< The buffer size (input)
    ) {
      return this->dpRequest(ContainerId::Container1, size);
    }

    //! Send a data product
    void dpSend(
        DpContainer& container, //!< The data product container
        Fw::Time timeTag = Fw::ZERO_TIME //!< The time tag
    );

  PROTECTED:

    // ----------------------------------------------------------------------
    // Handlers to implement for data products
    // ----------------------------------------------------------------------

    //! Receive a container of type Container1
    virtual void dpRecv_Container1_handler(
        DpContainer& container, //!< The container
        Fw::Success::T status //!< The container status
    ) = 0;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Time
    // ----------------------------------------------------------------------

    //!  Get the time
    //!
    //! \\return The current time
    Fw::Time getTime();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Calls for messages received on special input ports
    // ----------------------------------------------------------------------

    //! Callback for port productRecvIn
    static void m_p_productRecvIn_in(
        Fw::PassiveComponentBase* callComp, //!< The component instance
        NATIVE_INT_TYPE portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer, //!< The buffer
        const Fw::Success& status //!< The status
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Private data product handling functions
    // ----------------------------------------------------------------------

    //! Request a data product container
    void dpRequest(
        ContainerId::T containerId, //!< The component-local container id
        FwSizeType size //!< The buffer size
    );

    //! Handler implementation for productRecvIn
    void productRecvIn_handler(
        const NATIVE_INT_TYPE portNum, //!< The port number
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

    //! Output port timeGetOut
    Fw::OutputTimePort m_timeGetOut_OutputPort[NUM_TIMEGETOUT_OUTPUT_PORTS];

};

#endif