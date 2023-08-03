// ======================================================================
// \title  AbsTypeSerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for AbsType struct
// ======================================================================

#include "cstdio"
#include "cstring"

#include "AbsTypeSerializableAc.hpp"
#include "Fw/Types/Assert.hpp"
#include "Fw/Types/StringUtils.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

AbsType ::
  AbsType() :
    Serializable(),
    t()
{

}

AbsType ::
  AbsType(const T& t) :
    Serializable(),
    t(t)
{

}

AbsType ::
  AbsType(const AbsType& obj) :
    Serializable(),
    t(obj.t)
{

}

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

AbsType& AbsType ::
  operator=(const AbsType& obj)
{
  if (this == &obj) {
    return *this;
  }

  set(obj.t);
  return *this;
}

bool AbsType ::
  operator==(const AbsType& obj) const
{
  return (this->t == obj.t);
}

bool AbsType ::
  operator!=(const AbsType& obj) const
{
  return !(*this == obj);
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const AbsType& obj) {
  Fw::String s;
  obj.toString(s);
  os << s.toChar();
  return os;
}

#endif

// ----------------------------------------------------------------------
// Member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus AbsType ::
  serialize(Fw::SerializeBufferBase& buffer) const
{
  Fw::SerializeStatus status;

  status = buffer.serialize(this->t);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }

  return status;
}

Fw::SerializeStatus AbsType ::
  deserialize(Fw::SerializeBufferBase& buffer)
{
  Fw::SerializeStatus status;

  status = buffer.deserialize(this->t);
  if (status != Fw::FW_SERIALIZE_OK) {
    return status;
  }

  return status;
}

#if FW_SERIALIZABLE_TO_STRING

void AbsType ::
  toString(Fw::StringBase& sb) const
{
  static const char* formatString =
    "( "
    "t = %s"
    " )";

  // Declare strings to hold any serializable toString() arguments
  Fw::String tStr;

  // Call toString for arrays and serializable types
  this->t.toString(tStr);

  char outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE];
  (void) snprintf(
    outputString,
    FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE,
    formatString,
    tStr.toChar()
  );

  outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate
  sb = outputString;
}

#endif

// ----------------------------------------------------------------------
// Setter functions
// ----------------------------------------------------------------------

void AbsType ::
  set(const T& t)
{
  this->t = t;
}

void AbsType ::
  sett(const T& t)
{
  this->t = t;
}
