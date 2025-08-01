// ======================================================================
// \title  PrimitiveF32fArrayAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for PrimitiveF32f array
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "PrimitiveF32fArrayAc.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Constructors
  // ----------------------------------------------------------------------

  PrimitiveF32f ::
    PrimitiveF32f() :
      Serializable()
  {
    // Construct using element-wise constructor
    *this = PrimitiveF32f(
      0.0f,
      0.0f,
      0.0f
    );
  }

  PrimitiveF32f ::
    PrimitiveF32f(const ElementType (&a)[SIZE]) :
      Serializable()
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = a[index];
    }
  }

  PrimitiveF32f ::
    PrimitiveF32f(const ElementType& e) :
      Serializable()
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = e;
    }
  }

  PrimitiveF32f ::
    PrimitiveF32f(
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

  PrimitiveF32f ::
    PrimitiveF32f(const PrimitiveF32f& obj) :
      Serializable()
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = obj.elements[index];
    }
  }

  // ----------------------------------------------------------------------
  // Operators
  // ----------------------------------------------------------------------

  PrimitiveF32f::ElementType& PrimitiveF32f ::
    operator[](const U32 i)
  {
    FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
    return this->elements[i];
  }

  const PrimitiveF32f::ElementType& PrimitiveF32f ::
    operator[](const U32 i) const
  {
    FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));
    return this->elements[i];
  }

  PrimitiveF32f& PrimitiveF32f ::
    operator=(const PrimitiveF32f& obj)
  {
    if (this == &obj) {
      return *this;
    }

    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = obj.elements[index];
    }
    return *this;
  }

  PrimitiveF32f& PrimitiveF32f ::
    operator=(const ElementType (&a)[SIZE])
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = a[index];
    }
    return *this;
  }

  PrimitiveF32f& PrimitiveF32f ::
    operator=(const ElementType& e)
  {
    for (U32 index = 0; index < SIZE; index++) {
      this->elements[index] = e;
    }
    return *this;
  }

  bool PrimitiveF32f ::
    operator==(const PrimitiveF32f& obj) const
  {
    for (U32 index = 0; index < SIZE; index++) {
      if (!((*this)[index] == obj[index])) {
        return false;
      }
    }
    return true;
  }

  bool PrimitiveF32f ::
    operator!=(const PrimitiveF32f& obj) const
  {
    return !(*this == obj);
  }

#ifdef BUILD_UT

  std::ostream& operator<<(std::ostream& os, const PrimitiveF32f& obj) {
    Fw::String s;
    obj.toString(s);
    os << s;
    return os;
  }

#endif

  // ----------------------------------------------------------------------
  // Public member functions
  // ----------------------------------------------------------------------

  Fw::SerializeStatus PrimitiveF32f ::
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

  Fw::SerializeStatus PrimitiveF32f ::
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

  FwSizeType PrimitiveF32f ::
    serializedSize() const
  {
    return SERIALIZED_SIZE;
  }

#if FW_SERIALIZABLE_TO_STRING

  void PrimitiveF32f ::
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
      tmp.format("%.1f", static_cast<F64>(this->elements[index]));

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

}
