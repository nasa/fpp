#ifndef M_EventManager_HPP
#define M_EventManager_HPP

#include "EventManagerComponentAc.hpp"

namespace M {

class EventManager : public EventManagerComponentBase {

public:
  EventManager(const char *name) {}

  void eventIn_handler(FwIndexType portNum, FwEventIdType id, Fw::Time &timeTag,
                       const Fw::LogSeverity &severity,
                       Fw::LogBuffer &args) override;

  void textEventIn_handler(FwIndexType portNum, FwEventIdType id,
                           Fw::Time &timeTag, const Fw::LogSeverity &severity,
                           Fw::TextLogString &text) override;
};

} // namespace M

#endif
