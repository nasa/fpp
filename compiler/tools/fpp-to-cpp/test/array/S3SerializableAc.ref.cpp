// ======================================================================
// \title  S3SerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for S3 struct
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "S3SerializableAc.hpp"

namespace S {

  // ----------------------------------------------------------------------
  // Constructors
  // ----------------------------------------------------------------------

  S3 ::
    S3() :
      Serializable(),
      m_mF64(0.0)
  {
    for (FwSizeType i = 0; i < 3; i++) {
      this->m_mU32Array[i] = 0;
    }
  }

  S3 ::
    S3(
        const Type_of_mU32Array& mU32Array,
        F64 mF64
    ) :
      Serializable(),
      m_mF64(mF64)
  {
    for (FwSizeType i = 0; i < 3; i++) {
      this->m_mU32Array[i] = mU32Array[i];
    }
  }

  S3 ::
    S3(const S3& obj) :
      Serializable(),
      m_mF64(obj.m_mF64)
  {
    for (FwSizeType i = 0; i < 3; i++) {
      this->m_mU32Array[i] = obj.m_mU32Array[i];
    }
  }

  S3 ::
    S3(
        U32 mU32Array,
        F64 mF64
    ) :
      Serializable(),
      m_mF64(mF64)
  {
    for (FwSizeType i = 0; i < 3; i++) {
      this->m_mU32Array[i] = mU32Array;
    }
  }

  // ----------------------------------------------------------------------
  // Operators
  // ----------------------------------------------------------------------

  S3& S3 ::
    operator=(const S3& obj)
  {
    if (this == &obj) {
      return *this;
    }

    set(obj.m_mU32Array, obj.m_mF64);
    return *this;
  }

  bool S3 ::
    operator==(const S3& obj) const
  {
    if (this == &obj) { return true; }

    // Compare non-array members
    if (!(this->m_mF64 == obj.m_mF64)) {
      return false;
    }

    // Compare array members
    for (FwSizeType i = 0; i < 3; i++) {
      if (!(this->m_mU32Array[i] == obj.m_mU32Array[i])) {
        return false;
      }
    }

    return true;
  }

  bool S3 ::
    operator!=(const S3& obj) const
  {
    return !(*this == obj);
  }

#ifdef BUILD_UT

  std::ostream& operator<<(std::ostream& os, const S3& obj) {
    Fw::String s;
    obj.toString(s);
    os << s.toChar();
    return os;
  }

#endif

  // ----------------------------------------------------------------------
  // Member functions
  // ----------------------------------------------------------------------

  Fw::SerializeStatus S3 ::
    serialize(Fw::SerializeBufferBase& buffer) const
  {
    Fw::SerializeStatus status;

    for (FwSizeType i = 0; i < 3; i++) {
      status = buffer.serialize(this->m_mU32Array[i]);
      if (status != Fw::FW_SERIALIZE_OK) {
        return status;
      }
    }
    status = buffer.serialize(this->m_mF64);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }

    return status;
  }

  Fw::SerializeStatus S3 ::
    deserialize(Fw::SerializeBufferBase& buffer)
  {
    Fw::SerializeStatus status;

    for (FwSizeType i = 0; i < 3; i++) {
      status = buffer.deserialize(this->m_mU32Array[i]);
      if (status != Fw::FW_SERIALIZE_OK) {
        return status;
      }
    }
    status = buffer.deserialize(this->m_mF64);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }

    return status;
  }

#if FW_SERIALIZABLE_TO_STRING

  void S3 ::
    toString(Fw::StringBase& sb) const
  {
    static const char* formatString =
      "( "
      "mU32Array = [ %" PRIu32 ", "
      "%" PRIu32 ", "
      "%" PRIu32 " ], "
      "mF64 = %f"
      " )";

    sb.format(
      formatString,
      this->m_mU32Array[0],
      this->m_mU32Array[1],
      this->m_mU32Array[2],
      this->m_mF64
    );
  }

#endif

  // ----------------------------------------------------------------------
  // Setter functions
  // ----------------------------------------------------------------------

  void S3 ::
    set(
        const Type_of_mU32Array& mU32Array,
        F64 mF64
    )
  {
    this->m_mF64 = mF64;

    for (FwSizeType i = 0; i < 3; i++) {
      this->m_mU32Array[i] = mU32Array[i];
    }
  }

  void S3 ::
    setmU32Array(const Type_of_mU32Array& mU32Array)
  {
    for (FwSizeType i = 0; i < 3; i++) {
      this->m_mU32Array[i] = mU32Array[i];
    }
  }

  void S3 ::
    setmF64(F64 mF64)
  {
    this->m_mF64 = mF64;
  }

}
