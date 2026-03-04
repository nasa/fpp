#ifndef M_CmdDispatcher_HPP
#define M_CmdDispatcher_HPP

#include "CmdDispatcherComponentAc.hpp"

namespace M {

class CmdDispatcher : public CmdDispatcherComponentBase {

public:
  CmdDispatcher(const char *name) {}

  void cmdRegIn_handler(FwIndexType portNum, FwOpcodeType opCode) override {}

  void cmdResponseIn_handler(FwIndexType portNum, FwOpcodeType opCode,
                             U32 cmdSeq,
                             const Fw::CmdResponse &response) override {}
};

} // namespace M

#endif
