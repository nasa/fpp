// ======================================================================
// \title  StringArrayArrayAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for StringArray array
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "StringArrayArrayAc.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

StringArray ::
  StringArray() :
    Serializable()
{
  // Construct using element-wise constructor
  *this = StringArray(
    String2(Fw::String("\"\\"), Fw::String("abc\ndef\n")),
    String2(Fw::String("\"\\"), Fw::String("abc\ndef\n")),
    String2(Fw::String("\"\\"), Fw::String("abc\ndef\n")),
    String2(Fw::String("\"\\"), Fw::String("abc\ndef\n")),
    String2(Fw::String("\"\\"), Fw::String("abc\ndef\n"))
  );
}

StringArray ::
  StringArray(const ElementType (&a)[SIZE]) :
    Serializable()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = a[index];
  }
}

StringArray ::
  StringArray(const ElementType& e) :
    Serializable()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = e;
  }
}

StringArray ::
  StringArray(
      const ElementType& e1,
      const ElementType& e2,
      const ElementType& e3,
      const ElementType& e4,
      const ElementType& e5
  ) :
    Serializable()
{
  this->elements[0] = e1;
  this->elements[1] = e2;
  this->elements[2] = e3;
  this->elements[3] = e4;
  this->elements[4] = e5;
}

StringArray ::
  StringArray(const StringArray& obj) :
    Serializable()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = obj.elements[index];
  }
}

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

StringArray::ElementType& StringArray ::
  operator[](const U32 i)
{
  FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
  return this->elements[i];
}

const StringArray::ElementType& StringArray ::
  operator[](const U32 i) const
{
  FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
  return this->elements[i];
}

StringArray& StringArray ::
  operator=(const StringArray& obj)
{
  if (this == &obj) {
    return *this;
  }

  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = obj.elements[index];
  }
  return *this;
}

StringArray& StringArray ::
  operator=(const ElementType (&a)[SIZE])
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = a[index];
  }
  return *this;
}

StringArray& StringArray ::
  operator=(const ElementType& e)
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = e;
  }
  return *this;
}

bool StringArray ::
  operator==(const StringArray& obj) const
{
  for (U32 index = 0; index < SIZE; index++) {
    if (!((*this)[index] == obj[index])) {
      return false;
    }
  }
  return true;
}

bool StringArray ::
  operator!=(const StringArray& obj) const
{
  return !(*this == obj);
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const StringArray& obj) {
  Fw::String s;
  obj.toString(s);
  os << s;
  return os;
}

#endif

// ----------------------------------------------------------------------
// Public member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus StringArray ::
  serializeTo(Fw::SerializeBufferBase& buffer) const
{
  Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
  for (U32 index = 0; index < SIZE; index++) {
    status = buffer.serializeFrom((*this)[index]);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }
  }
  return status;
}

Fw::SerializeStatus StringArray ::
  deserializeFrom(Fw::SerializeBufferBase& buffer)
{
  Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
  for (U32 index = 0; index < SIZE; index++) {
    status = buffer.deserializeTo((*this)[index]);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }
  }
  return status;
}

FwSizeType StringArray ::
  serializedSize() const
{
  FwSizeType size = 0;
  for (U32 index = 0; index < SIZE; index++) {
    size += this->elements[index].serializedSize();
  }
  return size;
}

#if FW_SERIALIZABLE_TO_STRING

void StringArray ::
  toString(Fw::StringBase& sb) const
{
  // Clear the output string
  sb = "";

  // Array prefix
  if (sb.length() + 2 <= sb.maxLength()) {
    sb += "[ ";
  } else {
    return;
  }

  for (U32 index = 0; index < SIZE; index++) {
    Fw::String tmp;
    this->elements[index].toString(tmp);

    FwSizeType size = tmp.length() + (index > 0 ? 2 : 0);
    if ((size + sb.length()) <= sb.maxLength()) {
      if (index > 0) {
        sb += ", ";
      }
      sb += tmp;
    } else {
      break;
    }
  }

  // Array suffix
  if (sb.length() + 2 <= sb.maxLength()) {
    sb += " ]";
  }
}

#endif
