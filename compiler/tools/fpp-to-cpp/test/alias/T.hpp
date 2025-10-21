// A minimal implementation of abstract type T

#ifndef T_HPP
#define T_HPP

// Include Fw/Types/Serializable.fpp from the F Prime framework
#include "Fw/Types/Serializable.hpp"

struct T : public Fw::Serializable { // Extend Fw::Serializable

  // Define some shorthand for F Prime types
  typedef Fw::SerializeStatus SS;
  typedef Fw::LinearBufferBase B;
  typedef Fw::Endianness E;

  // Define the constant SERIALIZED_SIZE
  enum Constants { SERIALIZED_SIZE = sizeof(U32) };

  // Provide a zero-argument constructor
  T() : x(0) { }

  // Define a comparison operator
  bool operator==(const T& that) const { return this->x == that.x; }

  // Define the virtual serializeTo method
  SS serializeTo(B& b, E e) const { return b.serializeFrom(x, e); }

  // Define the virtual deserializeFrom method
  SS deserializeFrom(B& b, E e) { return b.deserializeTo(x, e); }

  // Provide some data
  U32 x;

};

#endif
