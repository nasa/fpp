// ======================================================================
// \title  OneInstance_P3TlmPacketsAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for P3 telemetry packets
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "OneInstance_P3TlmPacketsAc.hpp"

namespace M {

  namespace OneInstance_P3TlmPackets {

    // ----------------------------------------------------------------------
    // File-local constants and variables
    // ----------------------------------------------------------------------

    namespace {

      struct ChannelIds {

        //! The identifier for channel M.c1.T1
        static constexpr FwChanIdType M_c1_T1 = 0x100;

        //! The identifier for channel M.c1.T2
        static constexpr FwChanIdType M_c1_T2 = 0x101;

        //! The identifier for channel M.c1.T3
        static constexpr FwChanIdType M_c1_T3 = 0x102;

        //! The identifier for channel M.c1.T4
        static constexpr FwChanIdType M_c1_T4 = 0x103;

        //! The identifier for channel M.c1.T5
        static constexpr FwChanIdType M_c1_T5 = 0x104;

        //! The identifier for channel M.c1.T6
        static constexpr FwChanIdType M_c1_T6 = 0x105;

      };

      struct ChannelSizes {

        //! The serialized size of channel M.c1.T1
        static constexpr FwSizeType M_c1_T1 = Fw::StringBase::STATIC_SERIALIZED_SIZE(80);

        //! The serialized size of channel M.c1.T2
        static constexpr FwSizeType M_c1_T2 = sizeof(U32);

        //! The serialized size of channel M.c1.T3
        static constexpr FwSizeType M_c1_T3 = sizeof(F32);

        //! The serialized size of channel M.c1.T4
        static constexpr FwSizeType M_c1_T4 = sizeof(U8);

        //! The serialized size of channel M.c1.T5
        static constexpr FwSizeType M_c1_T5 = A::SERIALIZED_SIZE;

        //! The serialized size of channel M.c1.T6
        static constexpr FwSizeType M_c1_T6 = S::SERIALIZED_SIZE;

      };

      namespace Packets {

        // The number of packets
        constexpr FwIndexType numPackets = 0;

        static_assert(
          numPackets <= Svc::MAX_PACKETIZER_PACKETS,
          "number of packets must be less than or equal to the maximum"
        );

      }

      // The size of the array of omitted channels
      constexpr FwIndexType omittedArraySize = 6;

      // The omitted channel entries
      constexpr Svc::TlmPacketizerChannelEntry omittedArray[omittedArraySize] = {
        { ChannelIds::M_c1_T1, ChannelSizes::M_c1_T1 },
        { ChannelIds::M_c1_T2, ChannelSizes::M_c1_T2 },
        { ChannelIds::M_c1_T3, ChannelSizes::M_c1_T3 },
        { ChannelIds::M_c1_T4, ChannelSizes::M_c1_T4 },
        { ChannelIds::M_c1_T5, ChannelSizes::M_c1_T5 },
        { ChannelIds::M_c1_T6, ChannelSizes::M_c1_T6 },
      };

    }

    // ----------------------------------------------------------------------
    // Extern variables
    // ----------------------------------------------------------------------

    constexpr Svc::TlmPacketizerPacketList packetList = {
      {},
      Packets::numPackets
    };


    constexpr Svc::TlmPacketizerPacket omittedChannels = {
      omittedArray,
      0,
      0,
      omittedArraySize
    };

  }

}
