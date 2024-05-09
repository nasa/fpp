// ======================================================================
// \title  StringPortAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for String port
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "Fw/Types/StringUtils.hpp"
#include "StringPortAc.hpp"

namespace {

  // ----------------------------------------------------------------------
  // Port buffer class
  // ----------------------------------------------------------------------

  class StringPortBuffer : public Fw::SerializeBufferBase {

    public:

      Fw::Serializable::SizeType getBuffCapacity() const {
        return InputStringPort::SERIALIZED_SIZE;
      }

      U8* getBuffAddr() {
        return m_buff;
      }

      const U8* getBuffAddr() const {
        return m_buff;
      }

    private:

      U8 m_buff[InputStringPort::SERIALIZED_SIZE];

  };

}

// ----------------------------------------------------------------------
// Input Port Member functions
// ----------------------------------------------------------------------

InputStringPort ::
  InputStringPort() :
    Fw::InputPortBase(),
    m_func(nullptr)
{

}

void InputStringPort ::
  init()
{
  Fw::InputPortBase::init();
}

void InputStringPort ::
  addCallComp(
      Fw::PassiveComponentBase* callComp,
      CompFuncPtr funcPtr
  )
{
  FW_ASSERT(callComp != nullptr);
  FW_ASSERT(funcPtr != nullptr);

  this->m_comp = callComp;
  this->m_func = funcPtr;
  this->m_connObj = callComp;
}

void InputStringPort ::
  invoke(
      const Fw::StringBase& str80,
      Fw::StringBase& str80Ref,
      const Fw::StringBase& str100,
      Fw::StringBase& str100Ref
  )
{
#if FW_PORT_TRACING == 1
  this->trace();
#endif

  FW_ASSERT(this->m_comp != nullptr);
  FW_ASSERT(this->m_func != nullptr);

  return this->m_func(this->m_comp, this->m_portNum, str80, str80Ref, str100, str100Ref);
}

#if FW_PORT_SERIALIZATION == 1

Fw::SerializeStatus InputStringPort ::
  invokeSerial(Fw::SerializeBufferBase& _buffer)
{
  Fw::SerializeStatus _status;

#if FW_PORT_TRACING == 1
  this->trace();
#endif

  FW_ASSERT(this->m_comp != nullptr);
  FW_ASSERT(this->m_func != nullptr);

  StringPortStrings::StringSize80 str80;
  _status = _buffer.deserialize(str80);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  StringPortStrings::StringSize80 str80Ref;
  _status = _buffer.deserialize(str80Ref);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  StringPortStrings::StringSize100 str100;
  _status = _buffer.deserialize(str100);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  StringPortStrings::StringSize100 str100Ref;
  _status = _buffer.deserialize(str100Ref);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  this->m_func(this->m_comp, this->m_portNum, str80, str80Ref, str100, str100Ref);

  return Fw::FW_SERIALIZE_OK;
}

#endif

// ----------------------------------------------------------------------
// Output Port Member functions
// ----------------------------------------------------------------------

OutputStringPort ::
  OutputStringPort() :
    Fw::OutputPortBase(),
    m_port(nullptr)
{

}

void OutputStringPort ::
  init()
{
  Fw::OutputPortBase::init();
}

void OutputStringPort ::
  addCallPort(InputStringPort* callPort)
{
  FW_ASSERT(callPort != nullptr);

  this->m_port = callPort;
  this->m_connObj = callPort;

#if FW_PORT_SERIALIZATION == 1
  this->m_serPort = nullptr;
#endif
}

void OutputStringPort ::
  invoke(
      const Fw::StringBase& str80,
      Fw::StringBase& str80Ref,
      const Fw::StringBase& str100,
      Fw::StringBase& str100Ref
  )
{
#if FW_PORT_TRACING == 1
  this->trace();
#endif

#if FW_PORT_SERIALIZATION
  FW_ASSERT((this->m_port != nullptr) || (this->m_serPort != nullptr));

  if (this->m_port != nullptr) {
    this->m_port->invoke(str80, str80Ref, str100, str100Ref);
  }
  else {
    Fw::SerializeStatus _status;
    StringPortBuffer _buffer;

    _status = _buffer.serialize(str80);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serialize(str80Ref);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serialize(str100);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serialize(str100Ref);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = this->m_serPort->invokeSerial(_buffer);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));
  }
#else
  FW_ASSERT(this->m_port != nullptr);
  this->m_port->invoke(str80, str80Ref, str100, str100Ref);
#endif
}
