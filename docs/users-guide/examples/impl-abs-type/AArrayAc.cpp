// ====================================================================== 
// \title  A
// \author Auto-generated
// \brief  cpp file for A
//
// \copyright
// Copyright 2020 California Institute of Technology.
// ALL RIGHTS RESERVED.  United States Government Sponsorship
// acknowledged. Any commercial use must be negotiated with the Office
// of Technology Transfer at the California Institute of Technology.
// 
// This software may be subject to U.S. export control laws and
// regulations.  By accepting this document, the user agrees to comply
// with all U.S. export laws and regulations.  User has the
// responsibility to obtain export licenses, or other export authority
// as may be required before exporting such information to foreign
// countries or providing access to foreign persons.
// ======================================================================

#include <string.h>
#include <stdio.h>

#include "Fw/Types/Assert.hpp"

#include "AArrayAc.hpp"



  // ----------------------------------------------------------------------
  // Constructors
  // ----------------------------------------------------------------------

  A ::
    A(void) :
      Serializable()
  {
    *this = A(
      T(),
      T(),
      T());
  }
  
  A :: 
    A(const ElementType (&a)[SIZE]) :
      Serializable()
  {
    for(U32 index = 0; index < SIZE; index++)
    {
      this->elements[index] = a[index];
    }
  }
  
  A :: 
    A(const ElementType& e) :
      Serializable()
  {
    for(U32 index = 0; index < SIZE; index++)
    {
      this->elements[index] = e;
    }
  }
  
  A :: 
    A(
      const ElementType (&e1), //!< Element 1
      const ElementType (&e2), //!< Element 2
      const ElementType (&e3)
    ) :
      Serializable()
  {
    this->elements[0] = e1;
    this->elements[1] = e2;
    this->elements[2] = e3;
  }
  
  A ::
    A(const A& other) :
      Serializable()
  {
    for(U32 index = 0; index < SIZE; index++)
    {
      this->elements[index] = other.elements[index];
    }
  }
  
  // ----------------------------------------------------------------------
  // Public functions
  // ----------------------------------------------------------------------
  
  
  A::ElementType& A ::
    operator[](const U32 i)
  {
    FW_ASSERT(i < SIZE);
    return this->elements[i];
  }
  
  const A::ElementType& A ::
    operator[](const U32 i) const
  {
    FW_ASSERT(i < SIZE);
    return this->elements[i];
  }
  
  
  const A& A ::
    operator=(const A& other)
  {
    for(U32 index = 0; index < SIZE; index++) {
      this->elements[index] = other.elements[index];
    }
    return *this;
  }
  
  const A& A ::
    operator=(const ElementType (&a)[SIZE])
  {
    for(U32 index = 0; index < SIZE; index++) {
      this->elements[index] = a[index];
    }
    return *this;
  }
  
  const A& A ::
    operator=(const ElementType& e)
  {
    for(U32 index = 0; index < SIZE; index++) {
      this->elements[index] = e;
    }
    return *this;
  }
  
  bool A ::
    operator==(const A& other) const 
  {
    for (U32 i = 0; i < SIZE; ++i) {
      if (!((*this)[i] == other[i])) {
        return false;
      }
    }
    return true;
  }
  
  bool A ::
    operator!=(const A& other) const 
  {
    return !(*this == other);
  }

#if 0

void A::toString(Fw::StringBase& text) const {

    static const char * formatString = "[ "
      "%s, "
      "%s, "
      "%s ]";

    // Need to instantiate toString for arrays and serializable types
    Fw::EightyCharString str0;
    this->elements[0].toString(str0);
    Fw::EightyCharString str1;
    this->elements[1].toString(str1);
    Fw::EightyCharString str2;
    this->elements[2].toString(str2);

    // declare strings to hold any serializable toString() arguments

    char outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE];
    (void)snprintf(outputString,FW_ARRAY_TO_STRING_BUFFER_SIZE,formatString
      ,str0.toChar()
      ,str1.toChar()
      ,str2.toChar()
    );
    outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate

    text = outputString;
}

#endif
  
#if 0
  std::ostream& operator<<(std::ostream& os, const A& obj) {
    Fw::EightyCharString temp;
    obj.toString(temp);

    os << temp;
    return os;
  }
#endif
  
  Fw::SerializeStatus A ::
    serialize(Fw::SerializeBufferBase& buffer) const
  {
    Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
    for (U32 i = 0; i < SIZE; ++i) {
      status = buffer.serialize((*this)[i]);
      if (status != Fw::FW_SERIALIZE_OK) {
        return status;
      }
    }
    return status;
  }
  
  Fw::SerializeStatus A ::
    deserialize(Fw::SerializeBufferBase& buffer)
  {
    Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
    for (U32 i = 0; i < SIZE; ++i) {
      status = buffer.deserialize((*this)[i]);
      if (status != Fw::FW_SERIALIZE_OK) {
        return status;
      }
    }
    return status;
  }

