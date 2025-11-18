#ifndef M_PassiveReceiver_HPP
#define M_PassiveReceiver_HPP

#include "PassiveReceiverComponentAc.hpp"

namespace M {

  class PassiveReceiver :
    public PassiveReceiverComponentBase
  {

    public:

      PassiveReceiver() {

      }

      PassiveReceiver(const char* name) {

      }

      virtual void p1_handler(
          FwIndexType portNum,
          U32 x1,
          F32 x2
      ) {

      }

      virtual U32 p2_handler(
          FwIndexType portNum,
          U32 x
      ) {
        return x;
      }

      virtual F32 p3_handler(
          FwIndexType portNum,
          F32 x
      ) {
        return x;
      }

  };

}

#endif
