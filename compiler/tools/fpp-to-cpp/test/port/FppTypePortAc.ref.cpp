// ======================================================================
// \title  FppTypePortAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for FppType port
// ======================================================================

#include "FppTypePortAc.hpp"
#include "Fw/Types/Assert.hpp"
#include "Fw/Types/ExternalString.hpp"

namespace {

  // ----------------------------------------------------------------------
  // Port buffer class
  // ----------------------------------------------------------------------

  class FppTypePortBuffer : public Fw::SerializeBufferBase {

    public:

      Fw::Serializable::SizeType getBuffCapacity() const {
        return InputFppTypePort::SERIALIZED_SIZE;
      }

      U8* getBuffAddr() {
        return m_buff;
      }

      const U8* getBuffAddr() const {
        return m_buff;
      }

    private:

      U8 m_buff[InputFppTypePort::SERIALIZED_SIZE];

  };

}

// ----------------------------------------------------------------------
// Input Port Member functions
// ----------------------------------------------------------------------

InputFppTypePort ::
  InputFppTypePort() :
    Fw::InputPortBase(),
    m_func(nullptr)
{

}

void InputFppTypePort ::
  init()
{
  Fw::InputPortBase::init();
}

void InputFppTypePort ::
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

void InputFppTypePort ::
  invoke(
      const E& e,
      E& eRef,
      const A& a,
      A& aRef,
      const S& s,
      S& sRef
  )
{
#if FW_PORT_TRACING == 1
  this->trace();
#endif

  FW_ASSERT(this->m_comp != nullptr);
  FW_ASSERT(this->m_func != nullptr);

  return this->m_func(this->m_comp, this->m_portNum, e, eRef, a, aRef, s, sRef);
}

#if FW_PORT_SERIALIZATION == 1

Fw::SerializeStatus InputFppTypePort ::
  invokeSerial(Fw::SerializeBufferBase& _buffer)
{
  Fw::SerializeStatus _status;

#if FW_PORT_TRACING == 1
  this->trace();
#endif

  FW_ASSERT(this->m_comp != nullptr);
  FW_ASSERT(this->m_func != nullptr);

  E e;
  _status = _buffer.deserializeTo(e);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  E eRef;
  _status = _buffer.deserializeTo(eRef);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  A a;
  _status = _buffer.deserializeTo(a);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  A aRef;
  _status = _buffer.deserializeTo(aRef);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  S s;
  _status = _buffer.deserializeTo(s);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  S sRef;
  _status = _buffer.deserializeTo(sRef);
  if (_status != Fw::FW_SERIALIZE_OK) {
    return _status;
  }

  this->m_func(this->m_comp, this->m_portNum, e, eRef, a, aRef, s, sRef);

  return Fw::FW_SERIALIZE_OK;
}

#endif

// ----------------------------------------------------------------------
// Output Port Member functions
// ----------------------------------------------------------------------

OutputFppTypePort ::
  OutputFppTypePort() :
    Fw::OutputPortBase(),
    m_port(nullptr)
{

}

void OutputFppTypePort ::
  init()
{
  Fw::OutputPortBase::init();
}

void OutputFppTypePort ::
  addCallPort(InputFppTypePort* callPort)
{
  FW_ASSERT(callPort != nullptr);

  this->m_port = callPort;
  this->m_connObj = callPort;

#if FW_PORT_SERIALIZATION == 1
  this->m_serPort = nullptr;
#endif
}

void OutputFppTypePort ::
  invoke(
      const E& e,
      E& eRef,
      const A& a,
      A& aRef,
      const S& s,
      S& sRef
  ) const
{
#if FW_PORT_TRACING == 1
  this->trace();
#endif

#if FW_PORT_SERIALIZATION
  FW_ASSERT((this->m_port != nullptr) || (this->m_serPort != nullptr));

  if (this->m_port != nullptr) {
    this->m_port->invoke(e, eRef, a, aRef, s, sRef);
  }
  else {
    Fw::SerializeStatus _status;
    FppTypePortBuffer _buffer;

    _status = _buffer.serializeFrom(e);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serializeFrom(eRef);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serializeFrom(a);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serializeFrom(aRef);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serializeFrom(s);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = _buffer.serializeFrom(sRef);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));

    _status = this->m_serPort->invokeSerial(_buffer);
    FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));
  }
#else
  FW_ASSERT(this->m_port != nullptr);
  this->m_port->invoke(e, eRef, a, aRef, s, sRef);
#endif
}
