// ======================================================================
// \title  DpContainer.hpp
// \author bocchino
// \brief  hpp file for DpContainer
// ======================================================================

#ifndef Fw_DpContainer_HPP
#define Fw_DpContainer_HPP

#include "FpConfig.hpp"
#include "Fw/Buffer/Buffer.hpp"
#include "Fw/Time/Time.hpp"

namespace Fw {

//! A data product Container
class DpContainer {
  public:
    // ----------------------------------------------------------------------
    // Types
    // ----------------------------------------------------------------------

    //! A DpContainer packet header
    struct Header {
        //! The offset for the packet descriptor field
        static constexpr FwSizeType PACKET_DESCRIPTOR_OFFSET = 0;
        //! The offset for the id field
        static constexpr FwSizeType ID_OFFSET = PACKET_DESCRIPTOR_OFFSET + sizeof(FwPacketDescriptorType);
        //! The offset for the priority field
        static constexpr FwDpPriorityType PRIORITY_OFFSET = ID_OFFSET + sizeof(FwDpIdType);
        //! The offset for the time tag field
        static constexpr FwSizeType TIME_TAG_OFFSET = PRIORITY_OFFSET + sizeof(FwDpPriorityType);
        //! The offset for the data size field
        static constexpr FwSizeType DATA_SIZE_OFFSET = TIME_TAG_OFFSET + Fw::Time::SERIALIZED_SIZE;
        //! The header size
        static constexpr FwSizeType SIZE = DATA_SIZE_OFFSET + sizeof(FwSizeType);
    };

  public:
    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    //! Constructor
    DpContainer(FwDpIdType id,            //!< The container id
                const Fw::Buffer& buffer  //!< The buffer
                );

  public:
    // ----------------------------------------------------------------------
    // Public member functions
    // ----------------------------------------------------------------------

    //! Get the container id
    //! \return The id
    FwDpIdType getId() const { return this->id; }

    //! Get the data size
    //! \return The data size
    FwSizeType getDataSize() const { return this->dataSize; }

    //! Get the packet buffer
    //! \return The buffer
    Fw::Buffer getBuffer() const { return this->buffer; }

    //! Get the packet size corresponding to the data size
    FwSizeType getPacketSize() const { return Header::SIZE + this->dataSize; }

    //! Get the priority
    //! \return The priority
    FwDpPriorityType getPriority() const { return this->priority; }

    //! Get the time tag
    //! \return The time tag
    Fw::Time getTimeTag() const { return this->timeTag; }

    //! Move the packet serialization to the specified offset
    //! \return The serialize status
    Fw::SerializeStatus moveSerToOffset(FwSizeType offset  //!< The offset
    );

    //! Serialize the header into the packet buffer
    //! \return The serialize status
    Fw::SerializeStatus serializeHeader();

    //! Set the priority
    void setPriority(FwDpPriorityType priority  //!< The priority
    ) {
        this->priority = priority;
    }

    //! Set the time tag
    void setTimeTag(Fw::Time timeTag  //!< The time tag
    ) {
        this->timeTag = timeTag;
    }

  PROTECTED:
    // ----------------------------------------------------------------------
    // Protected member variables
    // ----------------------------------------------------------------------

    //! The container id
    const FwDpIdType id;

    //! The priority
    FwDpPriorityType priority;

    //! The time tag
    Fw::Time timeTag;

    //! The data size
    FwSizeType dataSize;

    //! The packet buffer
    Fw::Buffer buffer;
};

}  // end namespace Fw

#endif
