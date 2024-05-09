// ======================================================================
// \title  TypedReturnPortAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for TypedReturn port
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "Fw/Types/StringUtils.hpp"
#include "base/TypedReturnPortAc.hpp"

namespace Ports {

  // ----------------------------------------------------------------------
  // Input Port Member functions
  // ----------------------------------------------------------------------

  InputTypedReturnPort ::
    InputTypedReturnPort() :
      Fw::InputPortBase(),
      m_func(nullptr)
  {

  }

  void InputTypedReturnPort ::
    init()
  {
    Fw::InputPortBase::init();
  }

  void InputTypedReturnPort ::
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

  F32 InputTypedReturnPort ::
    invoke(
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str2,
        const E& e,
        const A& a,
        const S& s
    )
  {
#if FW_PORT_TRACING == 1
    this->trace();
#endif

    FW_ASSERT(this->m_comp != nullptr);
    FW_ASSERT(this->m_func != nullptr);

    return this->m_func(this->m_comp, this->m_portNum, u32, f32, b, str2, e, a, s);
  }

#if FW_PORT_SERIALIZATION == 1

  Fw::SerializeStatus InputTypedReturnPort ::
    invokeSerial(Fw::SerializeBufferBase& _buffer)
  {
    // For ports with a return type, invokeSerial is not used
    (void) _buffer;

    FW_ASSERT(0);
    return Fw::FW_SERIALIZE_OK;
  }

#endif

  // ----------------------------------------------------------------------
  // Output Port Member functions
  // ----------------------------------------------------------------------

  OutputTypedReturnPort ::
    OutputTypedReturnPort() :
      Fw::OutputPortBase(),
      m_port(nullptr)
  {

  }

  void OutputTypedReturnPort ::
    init()
  {
    Fw::OutputPortBase::init();
  }

  void OutputTypedReturnPort ::
    addCallPort(InputTypedReturnPort* callPort)
  {
    FW_ASSERT(callPort != nullptr);

    this->m_port = callPort;
    this->m_connObj = callPort;

#if FW_PORT_SERIALIZATION == 1
    this->m_serPort = nullptr;
#endif
  }

  F32 OutputTypedReturnPort ::
    invoke(
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str2,
        const E& e,
        const A& a,
        const S& s
    )
  {
#if FW_PORT_TRACING == 1
    this->trace();
#endif

    FW_ASSERT(this->m_port != nullptr);
    return this->m_port->invoke(u32, f32, b, str2, e, a, s);
  }

}
