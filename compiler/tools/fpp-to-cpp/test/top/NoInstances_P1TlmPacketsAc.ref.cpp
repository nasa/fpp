// ======================================================================
// \title  NoInstances_P1TlmPacketsAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for P1 telemetry packets
// ======================================================================

#include "Fw/Time/Time.hpp"
#include "Fw/Types/Assert.hpp"
#include "Fw/Types/StringBase.hpp"
#include "NoInstances_P1TlmPacketsAc.hpp"

namespace NoInstances_P1TlmPackets {

  // ----------------------------------------------------------------------
  // File-local constants and variables
  // ----------------------------------------------------------------------

  namespace {

    namespace Packets {

      // The number of packets
      constexpr FwIndexType numPackets = 0;

      static_assert(
        numPackets <= Svc::MAX_PACKETIZER_PACKETS,
        "number of packets must be less than or equal to the maximum"
      );

    }

    // The size of the array of omitted channels
    constexpr FwIndexType omittedArraySize = 0;

  }

  // ----------------------------------------------------------------------
  // Extern variables
  // ----------------------------------------------------------------------

  constexpr Svc::TlmPacketizerPacketList packetList = {
    {},
    Packets::numPackets
  };


  constexpr Svc::TlmPacketizerPacket omittedChannels = {
    nullptr,
    0,
    0,
    omittedArraySize
  };

}
