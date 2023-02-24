// ======================================================================
// \title  PassiveTelemetryComponentAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for PassiveTelemetry component base class
// ======================================================================

#include <cstdio>

#include "Fw/Types/Assert.hpp"
#if FW_ENABLE_TEXT_LOGGING
#include "Fw/Types/String.hpp"
#endif
#include "PassiveTelemetryComponentAc.hpp"

namespace {
  // Define a message buffer class large enough to handle all the
  // asynchronous inputs to the component
  class ComponentIpcSerializableBuffer :
    public Fw::SerializeBufferBase
  {

    public:

      enum {
        // Max. message size = size of data + message id + port
        SERIALIZATION_SIZE =
          sizeof(BuffUnion) +
          sizeof(NATIVE_INT_TYPE) +
          sizeof(NATIVE_INT_TYPE)
      };

      NATIVE_UINT_TYPE getBuffCapacity() const {
        return sizeof(m_buff);
      }

      U8* getBuffAddr() {
        return m_buff;
      }

      const U8* getBuffAddr() const {
        return m_buff;
      }

    private:
      // Should be the max of all the input ports serialized sizes...
      U8 m_buff[SERIALIZATION_SIZE];

  };
}

// ----------------------------------------------------------------------
// Getters for special input ports
// ----------------------------------------------------------------------

Fw::InputCmdPort* PassiveTelemetryComponentBase ::
  get_cmdIn_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_cmdIn_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_cmdIn_InputPort[portNum];
}

// ----------------------------------------------------------------------
// Getters for typed input ports
// ----------------------------------------------------------------------

InputTypedPort* PassiveTelemetryComponentBase ::
  get_typedGuarded_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_typedGuarded_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_typedGuarded_InputPort[portNum];
}

InputTypedReturnPort* PassiveTelemetryComponentBase ::
  get_typedReturnGuarded_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_typedReturnGuarded_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_typedReturnGuarded_InputPort[portNum];
}

InputTypedReturnPort* PassiveTelemetryComponentBase ::
  get_typedReturnSync_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_typedReturnSync_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_typedReturnSync_InputPort[portNum];
}

InputTypedPort* PassiveTelemetryComponentBase ::
  get_typedSync_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_typedSync_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_typedSync_InputPort[portNum];
}

// ----------------------------------------------------------------------
// Getters for serial input ports
// ----------------------------------------------------------------------

Fw::InputSerializePort* PassiveTelemetryComponentBase ::
  get_serialGuarded_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_serialGuarded_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_serialGuarded_InputPort[portNum];
}

Fw::InputSerializePort* PassiveTelemetryComponentBase ::
  get_serialSync_InputPort(NATIVE_INT_TYPE portNum)
{
  FW_ASSERT(
    portNum < this->getNum_serialSync_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_serialSync_InputPort[portNum];
}

// ----------------------------------------------------------------------
// Connect special input ports to special output ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  set_cmdRegOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputCmdRegPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_cmdRegOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_cmdRegOut_OutputPort[portNum].addCallPort(port);
}

void PassiveTelemetryComponentBase ::
  set_cmdResponseOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputCmdResponsePort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_cmdResponseOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_cmdResponseOut_OutputPort[portNum].addCallPort(port);
}

void PassiveTelemetryComponentBase ::
  set_eventOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputLogPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_eventOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_eventOut_OutputPort[portNum].addCallPort(port);
}

void PassiveTelemetryComponentBase ::
  set_prmGetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputPrmGetPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_prmGetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_prmGetOut_OutputPort[portNum].addCallPort(port);
}

void PassiveTelemetryComponentBase ::
  set_prmSetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputPrmSetPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_prmSetOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_prmSetOut_OutputPort[portNum].addCallPort(port);
}

#if FW_ENABLE_TEXT_LOGGING == 1

void PassiveTelemetryComponentBase ::
  set_textEventOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputLogTextPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_textEventOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_textEventOut_OutputPort[portNum].addCallPort(port);
}

#endif

void PassiveTelemetryComponentBase ::
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

void PassiveTelemetryComponentBase ::
  set_tlmOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputTlmPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_tlmOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_tlmOut_OutputPort[portNum].addCallPort(port);
}

#if FW_PORT_SERIALIZATION

// ----------------------------------------------------------------------
// Connect serial input ports to special output ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  set_cmdRegOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_cmdResponseOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_eventOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_prmGetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_prmSetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

#if FW_ENABLE_TEXT_LOGGING == 1

void PassiveTelemetryComponentBase ::
  set_textEventOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

#endif

void PassiveTelemetryComponentBase ::
  set_timeGetOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_tlmOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

#endif

// ----------------------------------------------------------------------
// Connect typed input ports to typed output ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  set_typedOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      InputTypedPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_typedOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_typedOut_OutputPort[portNum].addCallPort(port);
}

void PassiveTelemetryComponentBase ::
  set_typedReturnOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      InputTypedReturnPort* port
  )
{
  FW_ASSERT(
    portNum < this->getNum_typedReturnOut_OutputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_typedReturnOut_OutputPort[portNum].addCallPort(port);
}

#if FW_PORT_SERIALIZATION

// ----------------------------------------------------------------------
// Connect serial input ports to typed output ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  set_typedOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_typedReturnOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

#endif

#if FW_PORT_SERIALIZATION

// ----------------------------------------------------------------------
// Connect serial input ports to serial output ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  set_serialOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputSerializePort* port
  )
{

}

void PassiveTelemetryComponentBase ::
  set_serialOut_OutputPort(
      NATIVE_INT_TYPE portNum,
      Fw::InputPortBase* port
  )
{

}

#endif

// ----------------------------------------------------------------------
// Component construction, initialization, and destruction
// ----------------------------------------------------------------------

PassiveTelemetryComponentBase ::
  PassiveTelemetryComponentBase(const char* compName) :
    Fw::PassiveComponentBase(compName)
{
  // Write telemetry channel ChannelU32OnChange
  this->m_first_update_ChannelU32OnChange = true;
  this->m_last_ChannelU32OnChange = 0;

  // Write telemetry channel ChannelEnumOnChange
  this->m_first_update_ChannelEnumOnChange = true;
}

void PassiveTelemetryComponentBase ::
  init(NATIVE_INT_TYPE instance)
{
  // Initialize base class
  Fw::PassiveComponentBase::init(instance);

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_cmdIn_InputPorts());
    port++
  ) {
    this->m_cmdIn_InputPort[port].init();
    this->m_cmdIn_InputPort[port].addCallComp(
      this,
      m_p_cmdIn_in
    );
    this->m_cmdIn_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_cmdIn_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_cmdIn_InputPort[port].setObjName(portName);
#endif
  }

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_typedGuarded_InputPorts());
    port++
  ) {
    this->m_typedGuarded_InputPort[port].init();
    this->m_typedGuarded_InputPort[port].addCallComp(
      this,
      m_p_typedGuarded_in
    );
    this->m_typedGuarded_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_typedGuarded_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_typedGuarded_InputPort[port].setObjName(portName);
#endif
  }

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_typedReturnGuarded_InputPorts());
    port++
  ) {
    this->m_typedReturnGuarded_InputPort[port].init();
    this->m_typedReturnGuarded_InputPort[port].addCallComp(
      this,
      m_p_typedReturnGuarded_in
    );
    this->m_typedReturnGuarded_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_typedReturnGuarded_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_typedReturnGuarded_InputPort[port].setObjName(portName);
#endif
  }

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_typedReturnSync_InputPorts());
    port++
  ) {
    this->m_typedReturnSync_InputPort[port].init();
    this->m_typedReturnSync_InputPort[port].addCallComp(
      this,
      m_p_typedReturnSync_in
    );
    this->m_typedReturnSync_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_typedReturnSync_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_typedReturnSync_InputPort[port].setObjName(portName);
#endif
  }

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_typedSync_InputPorts());
    port++
  ) {
    this->m_typedSync_InputPort[port].init();
    this->m_typedSync_InputPort[port].addCallComp(
      this,
      m_p_typedSync_in
    );
    this->m_typedSync_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_typedSync_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_typedSync_InputPort[port].setObjName(portName);
#endif
  }

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_serialGuarded_InputPorts());
    port++
  ) {
    this->m_serialGuarded_InputPort[port].init();
    this->m_serialGuarded_InputPort[port].addCallComp(
      this,
      m_p_serialGuarded_in
    );
    this->m_serialGuarded_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_serialGuarded_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_serialGuarded_InputPort[port].setObjName(portName);
#endif
  }

  // Connect input port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_serialSync_InputPorts());
    port++
  ) {
    this->m_serialSync_InputPort[port].init();
    this->m_serialSync_InputPort[port].addCallComp(
      this,
      m_p_serialSync_in
    );
    this->m_serialSync_InputPort[port].setPortNum(port);

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_serialSync_InputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_serialSync_InputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_cmdRegOut_OutputPorts());
    port++
  ) {
    this->m_cmdRegOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_cmdRegOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_cmdRegOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_cmdResponseOut_OutputPorts());
    port++
  ) {
    this->m_cmdResponseOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_cmdResponseOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_cmdResponseOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_eventOut_OutputPorts());
    port++
  ) {
    this->m_eventOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_eventOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_eventOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_prmGetOut_OutputPorts());
    port++
  ) {
    this->m_prmGetOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_prmGetOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_prmGetOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_prmSetOut_OutputPorts());
    port++
  ) {
    this->m_prmSetOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_prmSetOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_prmSetOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_textEventOut_OutputPorts());
    port++
  ) {
    this->m_textEventOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_textEventOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_textEventOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
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

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_tlmOut_OutputPorts());
    port++
  ) {
    this->m_tlmOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_tlmOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_tlmOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_typedOut_OutputPorts());
    port++
  ) {
    this->m_typedOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_typedOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_typedOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_typedReturnOut_OutputPorts());
    port++
  ) {
    this->m_typedReturnOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_typedReturnOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_typedReturnOut_OutputPort[port].setObjName(portName);
#endif
  }

  // Connect output port PassiveTelemetry
  for (
    PlatformIntType port = 0;
    port < static_cast<PlatformIntType>(this->getNum_serialOut_OutputPorts());
    port++
  ) {
    this->m_serialOut_OutputPort[port].init();

#if FW_OBJECT_NAMES == 1
    char portName[120];
    (void) snprintf(
      portName,
      sizeof(portName),
      "%s_serialOut_OutputPort[%" PRI_PlatformIntType "]",
      this->m_objName,
      port
    );
    this->m_serialOut_OutputPort[port].setObjName(portName);
#endif
  }
}

PassiveTelemetryComponentBase ::
  ~PassiveTelemetryComponentBase()
{

}

// ----------------------------------------------------------------------
// Getters for numbers of special input ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_cmdIn_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_cmdIn_InputPort));
}

// ----------------------------------------------------------------------
// Getters for numbers of typed input ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_typedGuarded_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_typedGuarded_InputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_typedReturnGuarded_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_typedReturnGuarded_InputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_typedReturnSync_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_typedReturnSync_InputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_typedSync_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_typedSync_InputPort));
}

// ----------------------------------------------------------------------
// Getters for numbers of serial input ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_serialGuarded_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_serialGuarded_InputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_serialSync_InputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_serialSync_InputPort));
}

// ----------------------------------------------------------------------
// Getters for numbers of special output ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_cmdRegOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_cmdRegOut_OutputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_cmdResponseOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_cmdResponseOut_OutputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_eventOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_eventOut_OutputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_prmGetOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_prmGetOut_OutputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_prmSetOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_prmSetOut_OutputPort));
}

#if FW_ENABLE_TEXT_LOGGING == 1

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_textEventOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_textEventOut_OutputPort));
}

#endif

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_timeGetOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_timeGetOut_OutputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_tlmOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_tlmOut_OutputPort));
}

// ----------------------------------------------------------------------
// Getters for numbers of typed output ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_typedOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_typedOut_OutputPort));
}

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_typedReturnOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_typedReturnOut_OutputPort));
}

// ----------------------------------------------------------------------
// Getters for numbers of serial output ports
// ----------------------------------------------------------------------

NATIVE_INT_TYPE PassiveTelemetryComponentBase ::
  getNum_serialOut_OutputPorts()
{
  return static_cast<NATIVE_INT_TYPE>(FW_NUM_ARRAY_ELEMENTS(this->m_serialOut_OutputPort));
}

// ----------------------------------------------------------------------
// Connection status queries for special output ports
// ----------------------------------------------------------------------

bool PassiveTelemetryComponentBase ::
  isConnected_cmdRegOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

bool PassiveTelemetryComponentBase ::
  isConnected_cmdResponseOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

bool PassiveTelemetryComponentBase ::
  isConnected_eventOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

bool PassiveTelemetryComponentBase ::
  isConnected_prmGetOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

bool PassiveTelemetryComponentBase ::
  isConnected_prmSetOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

#if FW_ENABLE_TEXT_LOGGING == 1

bool PassiveTelemetryComponentBase ::
  isConnected_textEventOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

#endif

bool PassiveTelemetryComponentBase ::
  isConnected_timeGetOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

bool PassiveTelemetryComponentBase ::
  isConnected_tlmOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

// ----------------------------------------------------------------------
// Connection status queries for typed output ports
// ----------------------------------------------------------------------

bool PassiveTelemetryComponentBase ::
  isConnected_typedOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

bool PassiveTelemetryComponentBase ::
  isConnected_typedReturnOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

// ----------------------------------------------------------------------
// Connection status queries for serial output ports
// ----------------------------------------------------------------------

bool PassiveTelemetryComponentBase ::
  isConnected_serialOut_OutputPort(NATIVE_INT_TYPE portNum)
{

}

// ----------------------------------------------------------------------
// Handlers to implement for typed input ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  typedGuarded_handler(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{

}

F32 PassiveTelemetryComponentBase ::
  typedReturnGuarded_handler(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{

}

F32 PassiveTelemetryComponentBase ::
  typedReturnSync_handler(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{

}

void PassiveTelemetryComponentBase ::
  typedSync_handler(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{

}

// ----------------------------------------------------------------------
// Port handler base-class functions for typed input ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  typedGuarded_handlerBase(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  // Make sure port number is valid
  FW_ASSERT(
    portNum < this->getNum_typedGuarded_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  // Lock guard mutex before calling
  this->lock();

  this->typedGuarded_handler(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );

  // Unlock guard mutex
  this->unLock();
}

F32 PassiveTelemetryComponentBase ::
  typedReturnGuarded_handlerBase(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  // Make sure port number is valid
  FW_ASSERT(
    portNum < this->getNum_typedReturnGuarded_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  F32 retVal;

  // Lock guard mutex before calling
  this->lock();

  retVal = this->typedReturnGuarded_handler(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );

  // Unlock guard mutex
  this->unLock();

  return retVal;
}

F32 PassiveTelemetryComponentBase ::
  typedReturnSync_handlerBase(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  // Make sure port number is valid
  FW_ASSERT(
    portNum < this->getNum_typedReturnSync_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  F32 retVal;

  retVal = this->typedReturnSync_handler(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );

  return retVal;
}

void PassiveTelemetryComponentBase ::
  typedSync_handlerBase(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  // Make sure port number is valid
  FW_ASSERT(
    portNum < this->getNum_typedSync_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->typedSync_handler(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );
}

// ----------------------------------------------------------------------
// Handlers to implement for serial input ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  serialGuarded_handler(
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{

}

void PassiveTelemetryComponentBase ::
  serialSync_handler(
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{

}

// ----------------------------------------------------------------------
// Port handler base-class functions for serial input ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  serialGuarded_handlerBase(
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // Make sure port number is valid
  FW_ASSERT(
    portNum < this->getNum_serialGuarded_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  // Lock guard mutex before calling
  this->lock();

  this->serialGuarded_handler(
    portNum,
    buffer
  );

  // Unlock guard mutex
  this->unLock();
}

void PassiveTelemetryComponentBase ::
  serialSync_handlerBase(
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  // Make sure port number is valid
  FW_ASSERT(
    portNum < this->getNum_serialSync_InputPorts(),
    static_cast<FwAssertArgType>(portNum)
  );

  this->serialSync_handler(
    portNum,
    buffer
  );
}

// ----------------------------------------------------------------------
// Invocation functions for typed output ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  typedOut_out(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{

}

F32 PassiveTelemetryComponentBase ::
  typedReturnOut_out(
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{

}

// ----------------------------------------------------------------------
// Invocation functions for serial output ports
// ----------------------------------------------------------------------

Fw::SerializeStatus PassiveTelemetryComponentBase ::
  serialOut_out(
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{

}

// ----------------------------------------------------------------------
// Telemetry write functions
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelU32Format(
      U32 arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelF32Format(
      F32 arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelStringFormat(
      const Fw::TlmString& arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelEnum(
      const E& arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelArrayFreq(
      const A& arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelStructFreq(
      const S& arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelU32Limits(
      U32 arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelF32Limits(
      F32 arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelF64(
      F64 arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelU32OnChange(
      U32 arg,
      Fw::Time _tlmTime
  )
{

}

void PassiveTelemetryComponentBase ::
  tlmWrite_ChannelEnumOnChange(
      const E& arg,
      Fw::Time _tlmTime
  )
{

}

// ----------------------------------------------------------------------
// Time
// ----------------------------------------------------------------------

Fw::Time PassiveTelemetryComponentBase ::
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

// ----------------------------------------------------------------------
// Mutex operations for guarded ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  lock()
{
  this->m_guardedPortMutex.lock();
}

void PassiveTelemetryComponentBase ::
  unLock()
{
  this->m_guardedPortMutex.unLock();
}

// ----------------------------------------------------------------------
// Calls for messages received on special input ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  m_p_cmdIn_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      FwOpcodeType opCode,
      U32 cmdSeq,
      Fw::CmdArgBuffer& args
  )
{

}

// ----------------------------------------------------------------------
// Calls for messages received on typed input ports
// ----------------------------------------------------------------------

void PassiveTelemetryComponentBase ::
  m_p_typedGuarded_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  FW_ASSERT(callComp);
  PassiveTelemetryComponentBase* compPtr = static_cast<PassiveTelemetryComponentBase*>(callComp);
  compPtr->typedGuarded_handlerBase(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );
}

F32 PassiveTelemetryComponentBase ::
  m_p_typedReturnGuarded_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  FW_ASSERT(callComp);
  PassiveTelemetryComponentBase* compPtr = static_cast<PassiveTelemetryComponentBase*>(callComp);
  return compPtr->typedReturnGuarded_handlerBase(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );
}

F32 PassiveTelemetryComponentBase ::
  m_p_typedReturnSync_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedReturnPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  FW_ASSERT(callComp);
  PassiveTelemetryComponentBase* compPtr = static_cast<PassiveTelemetryComponentBase*>(callComp);
  return compPtr->typedReturnSync_handlerBase(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );
}

void PassiveTelemetryComponentBase ::
  m_p_typedSync_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      U32 u32,
      F32 f32,
      bool b,
      const TypedPortStrings::StringSize80& str,
      const E& e,
      const A& a,
      const S& s
  )
{
  FW_ASSERT(callComp);
  PassiveTelemetryComponentBase* compPtr = static_cast<PassiveTelemetryComponentBase*>(callComp);
  compPtr->typedSync_handlerBase(
    portNum,
    u32,
    f32,
    b,
    str,
    e,
    a,
    s
  );
}

// ----------------------------------------------------------------------
// Calls for messages received on serial input ports
// ----------------------------------------------------------------------

#if FW_PORT_SERIALIZATION

void PassiveTelemetryComponentBase ::
  m_p_serialGuarded_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  FW_ASSERT(callComp);
  PassiveTelemetryComponentBase* compPtr = static_cast<PassiveTelemetryComponentBase*>(callComp);
  compPtr->serialGuarded_handlerBase(
    portNum,
    buffer
  );
}

void PassiveTelemetryComponentBase ::
  m_p_serialSync_in(
      Fw::PassiveComponentBase* callComp,
      NATIVE_INT_TYPE portNum,
      Fw::SerializeBufferBase& buffer
  )
{
  FW_ASSERT(callComp);
  PassiveTelemetryComponentBase* compPtr = static_cast<PassiveTelemetryComponentBase*>(callComp);
  compPtr->serialSync_handlerBase(
    portNum,
    buffer
  );
}

#endif

#if FW_ENABLE_TEXT_LOGGING == 1

#endif