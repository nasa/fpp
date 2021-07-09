#ifndef M_Active_HPP
#define M_Active_HPP

#include "ActiveComponentAc.hpp"

namespace M {

  class Active :
    public ActiveComponentBase
  {

    public:

      Active() {

      }

      Active(const char* name) {

      }

      void init(U32 queueSize, U32 instanceId) {

      }

      void initSpecial() {

      }

      void config() {

      }

      void startSpecial() {

      }

      void p_handler(NATIVE_INT_TYPE portNum) {

      }

      void stopSpecial() {

      }

      void freeSpecial() {

      }

      void tearDown() {

      }

  };

}

#endif
