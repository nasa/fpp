#ifndef M_Health_HPP
#define M_Health_HPP

#include "HealthComponentAc.hpp"

namespace Svc {

  class Health :
    public HealthComponentBase
  {

    public:

      typedef struct {
        U32 a;
        U32 b;
        const char* c;
      } PingEntry;

      Health(const char* name) {

      }

      void init(U32 instanceId) {

      }

      virtual void pingIn_handler(NATIVE_INT_TYPE portNum) {

      }

  };

}

#endif
