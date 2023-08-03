// ======================================================================
// \title  PrimitiveI64ArrayAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for PrimitiveI64 array
// ======================================================================

#include <cstdio>
#include <cstring>

#include "Fw/Types/Assert.hpp"
#include "Fw/Types/StringUtils.hpp"
#include "PrimitiveI64ArrayAc.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Constructors
  // ----------------------------------------------------------------------

  PrimitiveI64 ::
    PrimitiveI64() :
      Serializable()
  {
    // Construct using element-wise constructor
    *this = PrimitiveI64(
      0,
      0,
      0
    );
  }

  PrimitiveI64 ::
    PrimitiveI64(const ElementType (&a)[SIZE]) :
      Serializable()
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = a[index];
    }
  }

  PrimitiveI64 ::
    PrimitiveI64(const ElementType& e) :
      Serializable()
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = e;
    }
  }

  PrimitiveI64 ::
    PrimitiveI64(
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

  PrimitiveI64 ::
    PrimitiveI64(const PrimitiveI64& obj) :
      Serializable()
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = obj.elements[index];
    }
  }

  // ----------------------------------------------------------------------
  // Operators
  // ----------------------------------------------------------------------

  PrimitiveI64::ElementType& PrimitiveI64 ::
    operator[](const U32 i)
  {
    FW_ASSERT(i < SIZE);
    return this->elements[i];
  }

  const PrimitiveI64::ElementType& PrimitiveI64 ::
    operator[](const U32 i) const
  {
    FW_ASSERT(i < SIZE);
    return this->elements[i];
  }

  PrimitiveI64& PrimitiveI64 ::
    operator=(const PrimitiveI64& obj)
  {
    if (this == &obj) {
      return *this;
    }

    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = obj.elements[index];
    }
    return *this;
  }

  PrimitiveI64& PrimitiveI64 ::
    operator=(const ElementType (&a)[SIZE])
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = a[index];
    }
    return *this;
  }

  PrimitiveI64& PrimitiveI64 ::
    operator=(const ElementType& e)
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = e;
    }
    return *this;
  }

  bool PrimitiveI64 ::
    operator==(const PrimitiveI64& obj) const
  {
    for (U32 index = 0; index < SIZE; index++) {
      if (!((*this)[index] == obj[index])) {
        return false;
      }
    }
    return true;
  }

  bool PrimitiveI64 ::
    operator!=(const PrimitiveI64& obj) const
  {
    return !(*this == obj);
  }

#ifdef BUILD_UT

  std::ostream& operator<<(std::ostream& os, const PrimitiveI64& obj) {
    Fw::String s;
    obj.toString(s);
    os << s;
    return os;
  }

#endif

  // ----------------------------------------------------------------------
  // Member functions
  // ----------------------------------------------------------------------

  Fw::SerializeStatus PrimitiveI64 ::
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

  Fw::SerializeStatus PrimitiveI64 ::
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

  void PrimitiveI64 ::
    toString(Fw::StringBase& sb) const
  {
    static const char *formatString = "[ "
      "%" PRIx64 " "
      "%" PRIx64 " "
      "%" PRIx64 " ]";

    char outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE];
    (void) snprintf(
      outputString,
      FW_ARRAY_TO_STRING_BUFFER_SIZE,
      formatString,
      this->elements[0],
      this->elements[1],
      this->elements[2]
    );

    outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate
    sb = outputString;
  }

#endif

}
