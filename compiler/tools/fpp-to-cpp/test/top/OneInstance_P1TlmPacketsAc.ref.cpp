// ======================================================================
// \title  OneInstance_P1TlmPacketsAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for P1 telemetry packets
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "OneInstance_P1TlmPacketsAc.hpp"

namespace M {

  namespace OneInstance_P1TlmPackets {

    // ----------------------------------------------------------------------
    // File-local constants and variables
    // ----------------------------------------------------------------------

    namespace {

      struct SizeBounds {

        // The size of a packet header
        static constexpr FwSizeType packetHeaderSize = Fw::Time::SERIALIZED_SIZE +
          sizeof(FwTlmPacketizeIdType) + sizeof(FwPacketDescriptorType);

        // A packet header must fit in a com buffer
        static_assert(
          packetHeaderSize <= FW_COM_BUFFER_MAX_SIZE,
          "packet header must fit in com buffer"
        );

        // The max data size in a com buffer
        static constexpr FwSizeType packetMaxDataSize = FW_COM_BUFFER_MAX_SIZE - packetHeaderSize;

      };

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

      struct PacketIds {

        //! The identifier for packet P1
        static constexpr FwTlmPacketizeIdType P1 = 0x0;

        //! The identifier for packet P2
        static constexpr FwTlmPacketizeIdType P2 = 0x1;

      };

      struct PacketGroups {

        //! The group for packet P1
        static constexpr FwIndexType P1 = 0;

        //! The group for packet P2
        static constexpr FwIndexType P2 = 0;

      };

      struct PacketDataSizes {

        //! The data size for packet P1
        static constexpr FwSizeType P1 = ChannelSizes::M_c1_T1
          + ChannelSizes::M_c1_T2
          + ChannelSizes::M_c1_T3;

        static_assert(
          P1 <= SizeBounds::packetMaxDataSize,
          "packet data must fit in max data size"
        );

        //! The data size for packet P2
        static constexpr FwSizeType P2 = ChannelSizes::M_c1_T4
          + ChannelSizes::M_c1_T5
          + ChannelSizes::M_c1_T6;

        static_assert(
          P2 <= SizeBounds::packetMaxDataSize,
          "packet data must fit in max data size"
        );

      };

      struct PacketEntryArraySizes {

        // The entry array size for packet P1
        static constexpr FwIndexType P1 = 3;

        // The entry array size for packet P2
        static constexpr FwIndexType P2 = 3;

      };

      namespace PacketEntryArrays {

        // The channel entries for packet P1
        constexpr Svc::TlmPacketizerChannelEntry P1[PacketEntryArraySizes::P1] = {
          { ChannelIds::M_c1_T1, ChannelSizes::M_c1_T1 },
          { ChannelIds::M_c1_T2, ChannelSizes::M_c1_T2 },
          { ChannelIds::M_c1_T3, ChannelSizes::M_c1_T3 },
        };

        // The channel entries for packet P2
        constexpr Svc::TlmPacketizerChannelEntry P2[PacketEntryArraySizes::P2] = {
          { ChannelIds::M_c1_T4, ChannelSizes::M_c1_T4 },
          { ChannelIds::M_c1_T5, ChannelSizes::M_c1_T5 },
          { ChannelIds::M_c1_T6, ChannelSizes::M_c1_T6 },
        };

      }

      namespace Packets {

        // The number of packets
        constexpr FwIndexType numPackets = 2;

        static_assert(
          numPackets <= Svc::MAX_PACKETIZER_PACKETS,
          "number of packets must be less than or equal to the maximum"
        );

        // Packet P1
        constexpr Svc::TlmPacketizerPacket P1 = {
          PacketEntryArrays::P1,
          PacketIds::P1,
          PacketGroups::P1,
          PacketEntryArraySizes::P1
        };

        // Packet P2
        constexpr Svc::TlmPacketizerPacket P2 = {
          PacketEntryArrays::P2,
          PacketIds::P2,
          PacketGroups::P2,
          PacketEntryArraySizes::P2
        };

      }

      // The size of the array of omitted channels
      constexpr FwIndexType omittedArraySize = 0;

    }

    // ----------------------------------------------------------------------
    // Extern variables
    // ----------------------------------------------------------------------

    constexpr Svc::TlmPacketizerPacketList packetList = {
      {
        &Packets::P1,
        &Packets::P2,
      },
      Packets::numPackets
    };


    constexpr Svc::TlmPacketizerPacket omittedChannels = {
      nullptr,
      0,
      0,
      omittedArraySize
    };

  }

}
