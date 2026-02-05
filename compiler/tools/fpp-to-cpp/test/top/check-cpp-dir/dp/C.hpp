#ifndef M_C_HPP
#define M_C_HPP

#include "CComponentAc.hpp"

namespace M {

class C : public CComponentBase {

public:
  C(const char *name) {}

  void dpRecv_C_handler(DpContainer &container, Fw::Success::T) override;
};

} // namespace M

#endif
