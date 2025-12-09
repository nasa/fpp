#ifndef M_C_HPP
#define M_C_HPP

#include "CComponentAc.hpp"

namespace M {

class C : public CComponentBase {

public:
  C(const char *name) {}

  void regCommandsSpecial() {}

  void C_cmdHandler(FwOpcodeType opCode, U32 cmdSeq) override {}
};

} // namespace M

#endif
