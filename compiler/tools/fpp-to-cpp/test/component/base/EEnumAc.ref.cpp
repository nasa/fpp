// ======================================================================
// \title  EEnumAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for E enum
// ======================================================================

#include <cstring>
#include <limits>

#include "Fw/Types/Assert.hpp"
#include "base/EEnumAc.hpp"

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

E& E ::
  operator=(const E& obj)
{
  this->e = obj.e;
  return *this;
}

E& E ::
  operator=(T e1)
{
  this->e = e1;
  return *this;
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const E& obj) {
  Fw::String s;
  obj.toString(s);
  os << s;
  return os;
}

#endif

// ----------------------------------------------------------------------
// Member functions
// ----------------------------------------------------------------------

bool E ::
  isValid() const
{
  return ((e >= X) && (e <= Z));
}

Fw::SerializeStatus E ::
  serialize(Fw::SerializeBufferBase& buffer) const
{
  const Fw::SerializeStatus status = buffer.serialize(
      static_cast<SerialType>(this->e)
  );
  return status;
}

Fw::SerializeStatus E ::
  deserialize(Fw::SerializeBufferBase& buffer)
{
  SerialType es;
  Fw::SerializeStatus status = buffer.deserialize(es);
  if (status == Fw::FW_SERIALIZE_OK) {
    this->e = static_cast<T>(es);
    if (!this->isValid()) {
      status = Fw::FW_DESERIALIZE_FORMAT_ERROR;
    }
  }
  return status;
}

#if FW_SERIALIZABLE_TO_STRING

void E ::
  toString(Fw::StringBase& sb) const
{
  Fw::String s;
  switch (e) {
    case X:
      s = "X";
      break;
    case Y:
      s = "Y";
      break;
    case Z:
      s = "Z";
      break;
    default:
      s = "[invalid]";
      break;
  }
  sb.format("%s (%" PRIi32 ")", s.toChar(), e);
}

#elif FW_ENABLE_TEXT_LOGGING

void E ::
  toString(Fw::StringBase& sb) const
{
  sb.format("%" PRIi32 "", e);
}

#endif
