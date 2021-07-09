#ifndef M_HealthImpl_HPP
#define M_HealthImpl_HPP

#include "HealthComponentAc.hpp"

namespace Svc {

  class HealthImpl :
    public HealthComponentBase
  {

    public:

      typedef struct {
        U32 a;
        U32 b;
        const char* c;
      } PingEntry;

      HealthImpl(const char* name) {

      }

      void init(U32 instanceId) {

      }

      virtual void pingIn_handler(NATIVE_INT_TYPE portNum) {

      }

  };

}

#endif
