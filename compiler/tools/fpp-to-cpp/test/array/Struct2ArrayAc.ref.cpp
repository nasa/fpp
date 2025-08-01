// ======================================================================
// \title  Struct2ArrayAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Struct2 array
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "Struct2ArrayAc.hpp"

// ----------------------------------------------------------------------
// Constructors
// ----------------------------------------------------------------------

Struct2 ::
  Struct2() :
    Serializable()
{
  // Construct using element-wise constructor
  *this = Struct2(
    S2(M::S1(0.0f, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, false, Fw::String(""))),
    S2(M::S1(0.0f, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, false, Fw::String(""))),
    S2(M::S1(0.0f, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, false, Fw::String("")))
  );
}

Struct2 ::
  Struct2(const ElementType (&a)[SIZE]) :
    Serializable()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = a[index];
  }
}

Struct2 ::
  Struct2(const ElementType& e) :
    Serializable()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = e;
  }
}

Struct2 ::
  Struct2(
      const ElementType& e1,
      const ElementType& e2,
      const ElementType& e3
  ) :
    Serializable()
{
  this->elements[0] = e1;
  this->elements[1] = e2;
  this->elements[2] = e3;
}

Struct2 ::
  Struct2(const Struct2& obj) :
    Serializable()
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = obj.elements[index];
  }
}

// ----------------------------------------------------------------------
// Operators
// ----------------------------------------------------------------------

Struct2::ElementType& Struct2 ::
  operator[](const U32 i)
{
  FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
  return this->elements[i];
}

const Struct2::ElementType& Struct2 ::
  operator[](const U32 i) const
{
  FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
  return this->elements[i];
}

Struct2& Struct2 ::
  operator=(const Struct2& obj)
{
  if (this == &obj) {
    return *this;
  }

  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = obj.elements[index];
  }
  return *this;
}

Struct2& Struct2 ::
  operator=(const ElementType (&a)[SIZE])
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = a[index];
  }
  return *this;
}

Struct2& Struct2 ::
  operator=(const ElementType& e)
{
  for (U32 index = 0; index < SIZE; index++) {
    this->elements[index] = e;
  }
  return *this;
}

bool Struct2 ::
  operator==(const Struct2& obj) const
{
  for (U32 index = 0; index < SIZE; index++) {
    if (!((*this)[index] == obj[index])) {
      return false;
    }
  }
  return true;
}

bool Struct2 ::
  operator!=(const Struct2& obj) const
{
  return !(*this == obj);
}

#ifdef BUILD_UT

std::ostream& operator<<(std::ostream& os, const Struct2& obj) {
  Fw::String s;
  obj.toString(s);
  os << s;
  return os;
}

#endif

// ----------------------------------------------------------------------
// Public member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus Struct2 ::
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

Fw::SerializeStatus Struct2 ::
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

FwSizeType Struct2 ::
  serializedSize() const
{
  FwSizeType size = 0;
  for (U32 index = 0; index < SIZE; index++) {
    size += this->elements[index].serializedSize();
  }
  return size;
}

#if FW_SERIALIZABLE_TO_STRING

void Struct2 ::
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
