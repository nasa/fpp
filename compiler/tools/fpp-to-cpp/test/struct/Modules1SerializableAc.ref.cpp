// ======================================================================
// \title  Modules1SerializableAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for Modules1 struct
// ======================================================================

#include "cstdio"
#include "cstring"

#include "Fw/Types/Assert.hpp"
#include "Fw/Types/StringUtils.hpp"
#include "Modules1SerializableAc.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Constructors
  // ----------------------------------------------------------------------

  Modules1 ::
    Modules1() :
      Serializable(),
      x(0),
      y(0.0f)
  {

  }

  Modules1 ::
    Modules1(
        U32 x,
        F32 y
    ) :
      Serializable(),
      x(x),
      y(y)
  {

  }

  Modules1 ::
    Modules1(const Modules1& obj) :
      Serializable(),
      x(obj.x),
      y(obj.y)
  {

  }

  // ----------------------------------------------------------------------
  // Operators
  // ----------------------------------------------------------------------

  Modules1& Modules1 ::
    operator=(const Modules1& obj)
  {
    if (this == &obj) {
      return *this;
    }

    set(obj.x, obj.y);
    return *this;
  }

  bool Modules1 ::
    operator==(const Modules1& obj) const
  {
    return (
      (this->x == obj.x) &&
      (this->y == obj.y)
    );
  }

  bool Modules1 ::
    operator!=(const Modules1& obj) const
  {
    return !(*this == obj);
  }

#ifdef BUILD_UT

  std::ostream& operator<<(std::ostream& os, const Modules1& obj) {
    Fw::String s;
    obj.toString(s);
    os << s.toChar();
    return os;
  }

#endif

  // ----------------------------------------------------------------------
  // Member functions
  // ----------------------------------------------------------------------

  Fw::SerializeStatus Modules1 ::
    serialize(Fw::SerializeBufferBase& buffer) const
  {
    Fw::SerializeStatus status;

    status = buffer.serialize(this->x);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }
    status = buffer.serialize(this->y);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }

    return status;
  }

  Fw::SerializeStatus Modules1 ::
    deserialize(Fw::SerializeBufferBase& buffer)
  {
    Fw::SerializeStatus status;

    status = buffer.deserialize(this->x);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }
    status = buffer.deserialize(this->y);
    if (status != Fw::FW_SERIALIZE_OK) {
      return status;
    }

    return status;
  }

#if FW_SERIALIZABLE_TO_STRING || BUILD_UT

  void Modules1 ::
    toString(Fw::StringBase& sb) const
  {
    static const char* formatString =
      "( "
      "x = %" PRIu32 ", "
      "y = %f"
      " )";

    char outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE];
    (void) snprintf(
      outputString,
      FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE,
      formatString,
      this->x,
      this->y
    );

    outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate
    sb = outputString;
  }

#endif

  // ----------------------------------------------------------------------
  // Getter functions
  // ----------------------------------------------------------------------

  U32 Modules1 ::
    getx() const
  {
    return this->x;
  }

  F32 Modules1 ::
    gety() const
  {
    return this->y;
  }

  // ----------------------------------------------------------------------
  // Setter functions
  // ----------------------------------------------------------------------

  void Modules1 ::
    set(
        U32 x,
        F32 y
    )
  {
    this->x = x;
    this->y = y;
  }

  void Modules1 ::
    setx(U32 x)
  {
    this->x = x;
  }

  void Modules1 ::
    sety(F32 y)
  {
    this->y = y;
  }

}
