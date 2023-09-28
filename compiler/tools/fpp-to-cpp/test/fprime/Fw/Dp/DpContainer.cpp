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
    : id(id), priority(0), procType(DpCfg::ProcType::NONE), dataSize(0), buffer() {
    // Initialize the user data field
    this->initUserDataField();
    // Set the buffer
    this->setBuffer(buffer);
}

DpContainer::DpContainer()
    : id(0), priority(0), procType(DpCfg::ProcType::NONE), dataSize(0), buffer() {
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
    // Serialize the processing type
    if (status == Fw::FW_SERIALIZE_OK) {
        status = serializeRepr.serialize(this->procType);
    }
    // Serialize the user data
    if (status == Fw::FW_SERIALIZE_OK) {
        const bool omitLength = true;
        status = serializeRepr.serialize(this->userData, sizeof userData, omitLength);
    }
    // Serialize the data size
    if (status == Fw::FW_SERIALIZE_OK) {
        status = serializeRepr.serialize(this->dataSize);
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
