// ======================================================================
// \title  DpContainer.cpp
// \author bocchino
// \brief  cpp file for DpContainer
// ======================================================================

#include <cstring>

#include "Fw/Com/ComPacket.hpp"
#include "Fw/Dp/DpContainer.hpp"
#include "Fw/Types/Assert.hpp"

namespace Fw {

// ----------------------------------------------------------------------
// Constructor
// ----------------------------------------------------------------------

DpContainer::DpContainer(FwDpIdType id, const Fw::Buffer& buffer)
    : id(id), priority(0), procTypes(0), dpState(), dataSize(0), buffer() {
    // Initialize the user data field
    this->initUserDataField();
    // Set the buffer
    this->setBuffer(buffer);
}

DpContainer::DpContainer() : id(0), priority(0), procTypes(0), dataSize(0), buffer() {
    // Initialize the user data field
    this->initUserDataField();
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
    // Serialize the header
    Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
    if (Header::SIZE <= serializeRepr.getBuffCapacity()) {
        // Serialize the packet type
        status = serializeRepr.serialize(static_cast<FwPacketDescriptorType>(Fw::ComPacket::FW_PACKET_DP));
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the container id
        status = serializeRepr.serialize(this->id);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the priority
        status = serializeRepr.serialize(this->priority);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the time tag
        status = serializeRepr.serialize(this->timeTag);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the processing types
        status = serializeRepr.serialize(this->procTypes);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the user data
        const bool omitLength = true;
        status = serializeRepr.serialize(this->userData, sizeof userData, omitLength);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the data product state
        status = serializeRepr.serialize(this->dpState);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
        // Serialize the data size
        status = serializeRepr.serialize(this->dataSize);
        FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
    } else {
        status = Fw::FW_SERIALIZE_NO_ROOM_LEFT;
    }
    return status;
}

void DpContainer::setBuffer(const Buffer& buffer) {
    this->buffer = buffer;
    // Move the serialization index to the end of the header
    const auto status = this->moveSerToOffset(Header::SIZE);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
}

// ----------------------------------------------------------------------
// Private member functions
// ----------------------------------------------------------------------

void DpContainer::initUserDataField() {
    (void)::memset(this->userData, 0, sizeof this->userData);
}

}  // namespace Fw
