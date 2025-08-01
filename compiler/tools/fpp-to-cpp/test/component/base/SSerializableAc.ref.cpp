// ======================================================================
// \title  SSerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for S struct
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "base/SSerializableAc.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

S ::
  S() :
    Serializable(),
    m_x(0),
    m_y(m___fprime_ac_y_buffer, sizeof m___fprime_ac_y_buffer, Fw::String(""))
{

}

S ::
  S(
      U32 x,
      const Fw::StringBase& y
  ) :
    Serializable(),
    m_x(x),
    m_y(m___fprime_ac_y_buffer, sizeof m___fprime_ac_y_buffer, y)
{

}

S ::
  S(const S& obj) :
    Serializable(),
    m_x(obj.m_x),
    m_y(m___fprime_ac_y_buffer, sizeof m___fprime_ac_y_buffer, obj.m_y)
{

}

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

S& S ::
  operator=(const S& obj)
{
  if (this == &obj) {
    return *this;
  }

  set(obj.m_x, obj.m_y);
  return *this;
}

bool S ::
  operator==(const S& obj) const
{
  if (this == &obj) { return true; }
  return (
    (this->m_x == obj.m_x) &&
    (this->m_y == obj.m_y)
  );
}

bool S ::
  operator!=(const S& obj) const
{
  return !(*this == obj);
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const S& obj) {
  Fw::String s;
  obj.toString(s);
  os << s.toChar();
  return os;
}

#endif

// ----------------------------------------------------------------------
// Member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus S ::
  serializeTo(Fw::SerializeBufferBase& buffer) const
{
  Fw::SerializeStatus status;

  status = buffer.serializeFrom(this->m_x);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }
  status = buffer.serializeFrom(this->m_y);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }

  return status;
}

Fw::SerializeStatus S ::
  deserializeFrom(Fw::SerializeBufferBase& buffer)
{
  Fw::SerializeStatus status;

  status = buffer.deserializeTo(this->m_x);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }
  status = buffer.deserializeTo(this->m_y);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }

  return status;
}

FwSizeType S ::
  serializedSize() const
{
  FwSizeType size = 0;
  size += sizeof(U32);
  size += this->m_y.serializedSize();
  return size;
}

#if FW_SERIALIZABLE_TO_STRING

void S ::
  toString(Fw::StringBase& sb) const
{
  Fw::String tmp;
  sb = "( ";

  // Format x
  sb += "x = ";
  tmp.format("%" PRIu32 "", this->m_x);
  sb += tmp;
  sb += ", ";

  // Format y
  sb += "y = ";
  sb += this->m_y;
  sb += " )";
}

#endif

// ----------------------------------------------------------------------
// Setter functions
// ----------------------------------------------------------------------

void S ::
  set(
      U32 x,
      const Fw::StringBase& y
  )
{
  this->m_x = x;
  this->m_y = y;
}

void S ::
  set_x(U32 x)
{
  this->m_x = x;
}

void S ::
  set_y(const Fw::StringBase& y)
{
  this->m_y = y;
}
