#ifndef M_Passive_HPP
#define M_Passive_HPP

#include "PassiveComponentAc.hpp"

namespace M {

  class Passive :
    public PassiveComponentBase
  {

    public:

      Passive(const char* name) {

      }

      void init(U32 instanceId) {

      }

  };

  // Simulate a concrete implementation
  typedef Passive ConcretePassive;

}

#endif
