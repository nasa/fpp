#ifndef M_DpManager_HPP
#define M_DpManager_HPP

#include "DpManagerComponentAc.hpp"

namespace M {

class DpManager final : public DpManagerComponentBase {

public:
  DpManager(const char *name) {}

  Fw::Success productGetIn_handler(const FwIndexType portNum, FwDpIdType id,
                                   FwSizeType size,
                                   Fw::Buffer &buffer) override;

  void productSendIn_handler(const FwIndexType portNum, FwDpIdType id,
                             const Fw::Buffer &buffer) override;
};

} // namespace M

#endif
