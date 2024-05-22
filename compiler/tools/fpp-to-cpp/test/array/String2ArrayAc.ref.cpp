// ======================================================================
// \title  String2ArrayAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for String2 array
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "String2ArrayAc.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

String2 ::
  String2() :
    Serializable()
{
  this->initElements();
  // Construct using element-wise constructor
  *this = String2(
    Fw::String("\"\\"),
    Fw::String("abc\ndef")
  );
}

String2 ::
  String2(const ElementType (&a)[SIZE]) :
    Serializable()
{
  this->initElements();
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = a[index];
  }
}

String2 ::
  String2(const Fw::StringBase& e) :
    Serializable()
{
  this->initElements();
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = e;
  }
}

String2 ::
  String2(
      const Fw::StringBase& e1,
      const Fw::StringBase& e2
  ) :
    Serializable()
{
  this->initElements();
  this->elements[0] = e1;
  this->elements[1] = e2;
}

String2 ::
  String2(const String2& obj) :
    Serializable()
{
  this->initElements();
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = obj.elements[index];
  }
}

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

String2::ElementType& String2 ::
  operator[](const U32 i)
{
  FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
  return this->elements[i];
}

const String2::ElementType& String2 ::
  operator[](const U32 i) const
{
  FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
  return this->elements[i];
}

String2& String2 ::
  operator=(const String2& obj)
{
  if (this == &obj) {
    return *this;
  }

  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = obj.elements[index];
  }
  return *this;
}

String2& String2 ::
  operator=(const ElementType (&a)[SIZE])
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = a[index];
  }
  return *this;
}

String2& String2 ::
  operator=(const ElementType& e)
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = e;
  }
  return *this;
}

bool String2 ::
  operator==(const String2& obj) const
{
  for (U32 index = 0; index < SIZE; index++) {
    if (!((*this)[index] == obj[index])) {
      return false;
    }
  }
  return true;
}

bool String2 ::
  operator!=(const String2& obj) const
{
  return !(*this == obj);
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const String2& obj) {
  Fw::String s;
  obj.toString(s);
  os << s;
  return os;
}

#endif

// ----------------------------------------------------------------------
// Public member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus String2 ::
  serialize(Fw::SerializeBufferBase& buffer) const
{
  Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
  for (U32 index = 0; index < SIZE; index++) {
    status = buffer.serialize((*this)[index]);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }
  }
  return status;
}

Fw::SerializeStatus String2 ::
  deserialize(Fw::SerializeBufferBase& buffer)
{
  Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
  for (U32 index = 0; index < SIZE; index++) {
    status = buffer.deserialize((*this)[index]);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }
  }
  return status;
}

#if FW_ARRAY_TO_STRING

void String2 ::
  toString(Fw::StringBase& sb) const
{
  static const char *formatString = "[ "
    "a %s b "
    "a %s b ]";

  sb.format(
    formatString,
    this->elements[0].toChar(),
    this->elements[1].toChar()
  );
}

#endif

// ----------------------------------------------------------------------
// Private member functions
// ----------------------------------------------------------------------

void String2 ::
  initElements()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index].setBuffer(&this->buffers[index][0], sizeof this->buffers[index]);
  }
}
