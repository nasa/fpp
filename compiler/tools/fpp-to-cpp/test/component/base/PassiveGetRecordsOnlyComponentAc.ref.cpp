// ======================================================================
// \title  PassiveGetRecordsOnlyComponentAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for PassiveGetRecordsOnly component base class
// ======================================================================

#include <cstdio>

#include "Fw/Types/Assert.hpp"
#if FW_ENABLE_TEXT_LOGGING
#include "Fw/Types/String.hpp"
#endif
#include "base/PassiveGetRecordsOnlyComponentAc.hpp"

// ----------------------------------------------------------------------
// Types for data products
// ----------------------------------------------------------------------

PassiveGetRecordsOnlyComponentBase::DpContainer ::
  DpContainer(
      FwDpIdType id,
      const Fw::Buffer& buffer,
      FwDpIdType baseId
  ) :
    Fw::DpContainer(id, buffer),
    baseId(baseId)
{

}

PassiveGetRecordsOnlyComponentBase::DpContainer ::
  DpContainer() :
    Fw::DpContainer(),
    baseId(0)
{

}

Fw::SerializeStatus PassiveGetRecordsOnlyComponentBase::DpContainer ::
  serializeRecord_U32Record(U32 elt)
{
  Fw::SerializeBufferBase& serializeRepr = this->buffer.getSerializeRepr();
  const FwSizeType sizeDelta =
    sizeof(FwDpIdType) +
    sizeof(U32);
  Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
  if (serializeRepr.getBuffLength() + sizeDelta <= serializeRepr.getBuffCapacity()) {
    const FwDpIdType id = this->baseId + RecordId::U32Record;
    status = serializeRepr.serialize(id);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
    status = serializeRepr.serialize(elt);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
    this->dataSize += sizeDelta;
  }
  else {
    status = Fw::FW_SERIALIZE_NO_ROOM_LEFT;
  }
  return status;
}

// ----------------------------------------------------------------------
// Component initialization
// ----------------------------------------------------------------------

void PassiveGetRecordsOnlyComponentBase ::
  init(NATIVE_INT_TYPE instance)
{
  // Initialize base class
  Fw::PassiveComponentBase::init(instance);

  // Connect output port productGetOut
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_productGetOut_OutputPorts());
    port++
  ) {
    this->m_productGetOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_productGetOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_productGetOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port productSendOut
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_productSendOut_OutputPorts());
    port++
  ) {
    this->m_productSendOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_productSendOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_productSendOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port timeGetOut
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_timeGetOut_OutputPorts());
    port++
  ) {
    this->m_timeGetOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_timeGetOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_timeGetOut_OutputPort[port].setObjName(portName);
#endif
  }
}

// ----------------------------------------------------------------------
// Connect input ports to special output ports
// ----------------------------------------------------------------------

void PassiveGetRecordsOnlyComponentBase ::
  set_productGetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputDpGetPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_productGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_productGetOut_OutputPort[portNum].addCallPort(port);
}

void PassiveGetRecordsOnlyComponentBase ::
  set_productSendOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputDpSendPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_productSendOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_productSendOut_OutputPort[portNum].addCallPort(port);
}

void PassiveGetRecordsOnlyComponentBase ::
  set_timeGetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputTimePort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_timeGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_timeGetOut_OutputPort[portNum].addCallPort(port);
}

#if FW_PORT_SERIALIZATION

// ----------------------------------------------------------------------
// Connect serial input ports to special output ports
// ----------------------------------------------------------------------

void PassiveGetRecordsOnlyComponentBase ::
  set_productSendOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_productSendOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_productSendOut_OutputPort[portNum].registerSerialPort(port);
}

void PassiveGetRecordsOnlyComponentBase ::
  set_timeGetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_timeGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_timeGetOut_OutputPort[portNum].registerSerialPort(port);
}

#endif

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

PassiveGetRecordsOnlyComponentBase ::
  PassiveGetRecordsOnlyComponentBase(const char* compName) :
    Fw::PassiveComponentBase(compName)
{

}

PassiveGetRecordsOnlyComponentBase ::
  ~PassiveGetRecordsOnlyComponentBase()
{

}

// ----------------------------------------------------------------------
// Getters for numbers of special output ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveGetRecordsOnlyComponentBase ::
  getNum_productGetOut_OutputPorts() const
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_productGetOut_OutputPort));
}

NATIVE_INT_TYPE PassiveGetRecordsOnlyComponentBase ::
  getNum_productSendOut_OutputPorts() const
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_productSendOut_OutputPort));
}

NATIVE_INT_TYPE PassiveGetRecordsOnlyComponentBase ::
  getNum_timeGetOut_OutputPorts() const
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_timeGetOut_OutputPort));
}

// ----------------------------------------------------------------------
// Connection status queries for special output ports
// ----------------------------------------------------------------------

bool PassiveGetRecordsOnlyComponentBase ::
  isConnected_productGetOut_OutputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_productGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return this->m_productGetOut_OutputPort[portNum].isConnected();
}

bool PassiveGetRecordsOnlyComponentBase ::
  isConnected_productSendOut_OutputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_productSendOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return this->m_productSendOut_OutputPort[portNum].isConnected();
}

bool PassiveGetRecordsOnlyComponentBase ::
  isConnected_timeGetOut_OutputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_timeGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return this->m_timeGetOut_OutputPort[portNum].isConnected();
}

// ----------------------------------------------------------------------
// Invocation functions for special output ports
// ----------------------------------------------------------------------

Fw::Success PassiveGetRecordsOnlyComponentBase ::
  productGetOut_out(
      NATIVE_INT_TYPE portNum,
      FwDpIdType id,
      FwSizeType size,
      Fw::Buffer& buffer
  )
{
  FW_ASSERT(
    portNum < this->getNum_productGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );
  return this->m_productGetOut_OutputPort[portNum].invoke(
    id,
    size,
    buffer
  );
}

void PassiveGetRecordsOnlyComponentBase ::
  productSendOut_out(
      NATIVE_INT_TYPE portNum,
      FwDpIdType id,
      const Fw::Buffer& buffer
  )
{
  FW_ASSERT(
    portNum < this->getNum_productSendOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );
  this->m_productSendOut_OutputPort[portNum].invoke(
    id,
    buffer
  );
}

// ----------------------------------------------------------------------
// Functions for managing data products
// ----------------------------------------------------------------------

void PassiveGetRecordsOnlyComponentBase ::
  dpSend(
      DpContainer& container,
      Fw::Time timeTag
  )
{
  // Update the time tag
  if (timeTag == Fw::ZERO_TIME) {
    // Get the time from the time port
    timeTag = this->getTime();
  }
  container.setTimeTag(timeTag);
  // Serialize the header into the packet
  Fw::SerializeStatus status = container.serializeHeader();
  FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
  // Update the size of the buffer according to the data size
  const FwSizeType packetSize = container.getPacketSize();
  Fw::Buffer buffer = container.getBuffer();
  FW_ASSERT(packetSize <= buffer.getSize(), packetSize, buffer.getSize());
  buffer.setSize(packetSize);
  // Send the buffer
  this->productSendOut_out(0, container.getId(), buffer);
}

// ----------------------------------------------------------------------
// Time
// ----------------------------------------------------------------------

Fw::Time PassiveGetRecordsOnlyComponentBase ::
  getTime()
{
  if (this->m_timeGetOut_OutputPort[0].isConnected()) {
    Fw::Time _time;
    this->m_timeGetOut_OutputPort[0].invoke(_time);
    return _time;
  }
  else {
    return Fw::Time(TB_NONE, 0, 0);
  }
}