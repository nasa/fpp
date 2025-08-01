// ======================================================================
// \title  AbsSerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Abs struct
// ======================================================================

#include "AbsSerializableAc.hpp"
#include "Fw/Types/Assert.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

Abs ::
  Abs() :
    Serializable(),
    m_A()
{

}

Abs ::
  Abs(const AbsType& A) :
    Serializable(),
    m_A(A)
{

}

Abs ::
  Abs(const Abs& obj) :
    Serializable(),
    m_A(obj.m_A)
{

}

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

Abs& Abs ::
  operator=(const Abs& obj)
{
  if (this == &obj) {
    return *this;
  }

  set(obj.m_A);
  return *this;
}

bool Abs ::
  operator==(const Abs& obj) const
{
  return (this->m_A == obj.m_A);
}

bool Abs ::
  operator!=(const Abs& obj) const
{
  return !(*this == obj);
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const Abs& obj) {
  Fw::String s;
  obj.toString(s);
  os << s.toChar();
  return os;
}

#endif

// ----------------------------------------------------------------------
// Member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus Abs ::
  serializeTo(Fw::SerializeBufferBase& buffer) const
{
  Fw::SerializeStatus status;

  status = buffer.serializeFrom(this->m_A);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }

  return status;
}

Fw::SerializeStatus Abs ::
  deserializeFrom(Fw::SerializeBufferBase& buffer)
{
  Fw::SerializeStatus status;

  status = buffer.deserializeTo(this->m_A);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }

  return status;
}

FwSizeType Abs ::
  serializedSize() const
{
  FwSizeType size = 0;
  size += AbsType::SERIALIZED_SIZE;
  return size;
}

#if FW_SERIALIZABLE_TO_STRING

void Abs ::
  toString(Fw::StringBase& sb) const
{
  Fw::String tmp;
  sb = "( ";

  // Format A
  sb += "A = ";
  this->m_A.toString(tmp);
  sb += tmp;
  sb += " )";
}

#endif

// ----------------------------------------------------------------------
// Setter functions
// ----------------------------------------------------------------------

void Abs ::
  set(const AbsType& A)
{
  this->m_A = A;
}

void Abs ::
  set_A(const AbsType& A)
{
  this->m_A = A;
}
