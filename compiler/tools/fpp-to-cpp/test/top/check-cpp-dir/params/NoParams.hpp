#ifndef M_NoParams_HPP
#define M_NoParams_HPP

#include "NoParamsComponentAc.hpp"

namespace M {

  class NoParams :
    public NoParamsComponentBase
  {

    public:

      NoParams(const char* name) {

      }

      void init(U32 instanceId) {

      }

      void regParamsSpecial() {

      }

      void NoParams_cmdHandler(FwOpcodeType opCode, U32 cmdSeq) {

      }

  };

}

#endif
