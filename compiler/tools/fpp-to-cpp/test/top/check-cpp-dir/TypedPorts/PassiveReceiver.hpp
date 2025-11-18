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
          U32 x
      ) {

      }

      virtual U32 p2_handler(
          FwIndexType portNum,
          U32 x
      ) {
        return x;
      }

      virtual void p3_handler(
          FwIndexType portNum,
          F32 x
      ) {

      }

      virtual F32 p4_handler(
          FwIndexType portNum,
          F32 x
      ) {
        return x;
      }

  };

}

#endif
