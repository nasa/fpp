// ======================================================================
// \title  DpContainer.cpp
// \author bocchino
// \brief  cpp file for DpContainer
// ======================================================================

#include "Fw/Dp/DpContainer.hpp"
#include "Fw/Types/Assert.hpp"
#include "Fw/Com/ComPacket.hpp"

namespace Fw {

// ----------------------------------------------------------------------
// Constructor
// ----------------------------------------------------------------------

//! Constructor
DpContainer::DpContainer(FwDpIdType id, const Fw::Buffer& buffer) : id(id), priority(0), dataSize(0), buffer(buffer) {
    // Move the serialization index to the end of the header
    const auto status = this->moveSerToOffset(Header::SIZE);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
}

// ----------------------------------------------------------------------
// Public member functions
// ----------------------------------------------------------------------

Fw::SerializeStatus DpContainer::moveSerToOffset(FwSizeType offset  //!< The offset
) {
    auto& serializeRepr = this->buffer.getSerializeRepr();
    return serializeRepr.moveSerToOffset(offset);
}

Fw::SerializeStatus DpContainer::serializeHeader() {
    auto& serializeRepr = this->buffer.getSerializeRepr();
    // Reset serialization
    serializeRepr.resetSer();
    // Serialize the packet type
    auto status = serializeRepr.serialize(static_cast<FwPacketDescriptorType>(Fw::ComPacket::FW_PACKET_DP));
    // Serialize the container id
    if (status == Fw::FW_SERIALIZE_OK) {
        status = serializeRepr.serialize(this->id);
    }
    // Serialize the priority
    if (status == Fw::FW_SERIALIZE_OK) {
        status = serializeRepr.serialize(this->priority);
    }
    // Serialize the time tag
    if (status == Fw::FW_SERIALIZE_OK) {
        status = serializeRepr.serialize(this->timeTag);
    }
    // Serialize the data size
    if (status == Fw::FW_SERIALIZE_OK) {
        status = serializeRepr.serialize(this->dataSize);
    }
    return status;
}
}  // namespace Fw
