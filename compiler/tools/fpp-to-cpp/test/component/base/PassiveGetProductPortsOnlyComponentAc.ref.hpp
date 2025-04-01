// ======================================================================
// \title  PassiveGetProductPortsOnlyComponentAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for PassiveGetProductPortsOnly component base class
// ======================================================================

#ifndef PassiveGetProductPortsOnlyComponentAc_HPP
#define PassiveGetProductPortsOnlyComponentAc_HPP

#include <FpConfig.hpp>

#include "Fw/Comp/ActiveComponentBase.hpp"
#include "Fw/Dp/DpGetPortAc.hpp"
#include "Fw/Dp/DpSendPortAc.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Port/OutputSerializePort.hpp"

//! \class PassiveGetProductPortsOnlyComponentBase
//! \brief Auto-generated base for PassiveGetProductPortsOnly component
//!
//! A passive component with product get and ports only
class PassiveGetProductPortsOnlyComponentBase :
  public Fw::PassiveComponentBase
{

    // ----------------------------------------------------------------------
    // Friend classes
    // ----------------------------------------------------------------------

    //! Friend class for white-box testing
    friend class PassiveGetProductPortsOnlyComponentBaseFriend;
    //! Friend class tester to support autocoded test harness
    friend class PassiveGetProductPortsOnlyTesterBase;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    //! Enumerations for numbers of special output ports
    enum {
      NUM_PRODUCTGETOUT_OUTPUT_PORTS = 1,
      NUM_PRODUCTSENDOUT_OUTPUT_PORTS = 1,
    };

  public:

    // ----------------------------------------------------------------------
    // Component initialization
    // ----------------------------------------------------------------------

    //! Initialize PassiveGetProductPortsOnlyComponentBase object
    void init(
        FwEnumStoreType instance = 0 //!< The instance number
    );

  public:

    // ----------------------------------------------------------------------
    // Connect input ports to special output ports
    // ----------------------------------------------------------------------

    //! Connect port to productGetOut[portNum]
    void set_productGetOut_OutputPort(
        FwIndexType portNum, //!< The port number
        Fw::InputDpGetPort* port //!< The input port
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

    //! Construct PassiveGetProductPortsOnlyComponentBase object
    PassiveGetProductPortsOnlyComponentBase(
        const char* compName = "" //!< The component name
    );

    //! Destroy PassiveGetProductPortsOnlyComponentBase object
    virtual ~PassiveGetProductPortsOnlyComponentBase();

  PROTECTED:

    // ----------------------------------------------------------------------
    // Getters for numbers of special output ports
    // ----------------------------------------------------------------------

    //! Get the number of productGetOut output ports
    //!
    //! \return The number of productGetOut output ports
    FwIndexType getNum_productGetOut_OutputPorts() const;

    //! Get the number of productSendOut output ports
    //!
    //! \return The number of productSendOut output ports
    FwIndexType getNum_productSendOut_OutputPorts() const;

  PROTECTED:

    // ----------------------------------------------------------------------
    // Connection status queries for special output ports
    // ----------------------------------------------------------------------

    //! Check whether port productGetOut is connected
    //!
    //! \return Whether port productGetOut is connected
    bool isConnected_productGetOut_OutputPort(
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
    // Invocation functions for special output ports
    // ----------------------------------------------------------------------

    //! Invoke output port productGetOut
    Fw::Success productGetOut_out(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID (input)
        FwSizeType dataSize, //!< The data size of the requested buffer (input)
        Fw::Buffer& buffer //!< The buffer (output)
    );

    //! Invoke output port productSendOut
    void productSendOut_out(
        FwIndexType portNum, //!< The port number
        FwDpIdType id, //!< The container ID
        const Fw::Buffer& buffer //!< The buffer
    );

  PRIVATE:

    // ----------------------------------------------------------------------
    // Special output ports
    // ----------------------------------------------------------------------

    //! Output port productGetOut
    Fw::OutputDpGetPort m_productGetOut_OutputPort[NUM_PRODUCTGETOUT_OUTPUT_PORTS];

    //! Output port productSendOut
    Fw::OutputDpSendPort m_productSendOut_OutputPort[NUM_PRODUCTSENDOUT_OUTPUT_PORTS];

};

#endif
