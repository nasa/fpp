// ======================================================================
// \title  Modules2SerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Modules2 struct
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "Modules2SerializableAc.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Constructors
  // ----------------------------------------------------------------------

  Modules2 ::
    Modules2() :
      Serializable(),
      m_x(0, 0.0f)
  {

  }

  Modules2 ::
    Modules2(const M::Modules1& x) :
      Serializable(),
      m_x(x)
  {

  }

  Modules2 ::
    Modules2(const Modules2& obj) :
      Serializable(),
      m_x(obj.m_x)
  {

  }

  // ----------------------------------------------------------------------
  // Operators
  // ----------------------------------------------------------------------

  Modules2& Modules2 ::
    operator=(const Modules2& obj)
  {
    if (this == &obj) {
      return *this;
    }

    set(obj.m_x);
    return *this;
  }

  bool Modules2 ::
    operator==(const Modules2& obj) const
  {
    return (this->m_x == obj.m_x);
  }

  bool Modules2 ::
    operator!=(const Modules2& obj) const
  {
    return !(*this == obj);
  }

#ifdef BUILD_UT

  std::ostream& operator<<(std::ostream& os, const Modules2& obj) {
    Fw::String s;
    obj.toString(s);
    os << s.toChar();
    return os;
  }

#endif

  // ----------------------------------------------------------------------
  // Member functions
  // ----------------------------------------------------------------------

  Fw::SerializeStatus Modules2 ::
    serializeTo(Fw::SerializeBufferBase& buffer) const
  {
    Fw::SerializeStatus status;

    status = buffer.serializeFrom(this->m_x);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }

    return status;
  }

  Fw::SerializeStatus Modules2 ::
    deserializeFrom(Fw::SerializeBufferBase& buffer)
  {
    Fw::SerializeStatus status;

    status = buffer.deserializeTo(this->m_x);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }

    return status;
  }

  FwSizeType Modules2 ::
    serializedSize() const
  {
    FwSizeType size = 0;
    size += this->m_x.serializedSize();
    return size;
  }

#if FW_SERIALIZABLE_TO_STRING

  void Modules2 ::
    toString(Fw::StringBase& sb) const
  {
    Fw::String tmp;
    sb = "( ";

    // Format x
    sb += "x = ";
    this->m_x.toString(tmp);
    sb += tmp;
    sb += " )";
  }

#endif

  // ----------------------------------------------------------------------
  // Setter functions
  // ----------------------------------------------------------------------

  void Modules2 ::
    set(const M::Modules1& x)
  {
    this->m_x = x;
  }

  void Modules2 ::
    set_x(const M::Modules1& x)
  {
    this->m_x = x;
  }

}
