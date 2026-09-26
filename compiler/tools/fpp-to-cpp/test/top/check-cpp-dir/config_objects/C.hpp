#ifndef M_CONFIG_OBJECTS_C_HPP
#define M_CONFIG_OBJECTS_C_HPP

#include "CComponentAc.hpp"

namespace M {
  class C : public CComponentBase {
    public:
      C(const char* name) {}
      void record(U32 value) {}
  };
}

#endif
