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

      //! Handler for input port p1
      virtual void p1_handler(
          FwIndexType portNum, //!< The port number
          U32 x
      ) {

      }

      //! Handler for input port p2
      virtual U32 p2_handler(
          FwIndexType portNum, //!< The port number
          U32 x
      ) {
        return 0;
      }

  };

}

#endif
