#ifndef __T_HPP__
#define __T_HPP__

#include "Fw/Types/Serializable.hpp"

//! C++ interface for type T
//! This simulates a handwritten C++ serializable class
//! that is represented as an abstract type in an FPP model.
class T :
  public Fw::Serializable
{

  public:

    enum {
      SERIALIZED_SIZE = 10
    };

  public:

    T();

  public:

    bool operator==(const T& other) const;
    bool operator!=(const T& other) const;

    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const;

    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer);

};

#endif
