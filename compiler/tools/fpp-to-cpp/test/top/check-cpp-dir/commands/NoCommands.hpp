#ifndef M_NoCommands_HPP
#define M_NoCommands_HPP

#include "NoCommandsComponentAc.hpp"

namespace M {

  class NoCommands :
    public NoCommandsComponentBase
  {

    public:

      NoCommands(const char* name) {

      }

      void init(U32 instanceId) {

      }

      void regCommandsSpecial() {

      }

      void NoCommands_cmdHandler(FwOpcodeType opCode, U32 cmdSeq) {

      }

  };

}

#endif
