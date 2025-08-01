// ======================================================================
// \title  SSerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for S struct
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "SSerializableAc.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

S ::
  S() :
    Serializable(),
    m_x(0)
{

}

S ::
  S(U32 x) :
    Serializable(),
    m_x(x)
{

}

S ::
  S(const S& obj) :
    Serializable(),
    m_x(obj.m_x)
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

  set(obj.m_x);
  return *this;
}

bool S ::
  operator==(const S& obj) const
{
  return (this->m_x == obj.m_x);
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

  return status;
}

FwSizeType S ::
  serializedSize() const
{
  FwSizeType size = 0;
  size += sizeof(U32);
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
  sb += " )";
}

#endif

// ----------------------------------------------------------------------
// Setter functions
// ----------------------------------------------------------------------

void S ::
  set(U32 x)
{
  this->m_x = x;
}

void S ::
  set_x(U32 x)
{
  this->m_x = x;
}
