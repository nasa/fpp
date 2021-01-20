#ifndef __Types_T_HPP__
#define __Types_T_HPP__

#include "Fw/Types/Serializable.hpp"

//! C++ interface for type Types_T
//! This simulates a handwritten C++ serializable class
//! that is represented as an abstract type in an FPP model.
class Types_T :
  public Fw::Serializable
{

  public:

    enum {
      SERIALIZED_SIZE = 10
    };

  public:

    Types_T();

  public:

    bool operator==(const Types_T& other) const;
    bool operator!=(const Types_T& other) const;

    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const;

    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer);

};

#endif
