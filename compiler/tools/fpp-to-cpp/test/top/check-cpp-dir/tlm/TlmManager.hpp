#ifndef M_TlmManager_HPP
#define M_TlmManager_HPP

#include "TlmManagerComponentAc.hpp"

namespace M {

class TlmManager : public TlmManagerComponentBase {

public:
  TlmManager(const char *name) {}

  void tlmIn_handler(FwIndexType portNum, FwChanIdType id, Fw::Time &timeTag,
                     Fw::TlmBuffer &val) override;
};

} // namespace M

#endif
