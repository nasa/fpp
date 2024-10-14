// ======================================================================
// \title  SmJunctionQueuedComponentAc.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for SmJunctionQueued component base class
// ======================================================================

#include "Fw/Types/Assert.hpp"
#include "Fw/Types/ExternalString.hpp"
#if FW_ENABLE_TEXT_LOGGING
#include "Fw/Types/String.hpp"
#endif
#include "base/SmJunctionQueuedComponentAc.hpp"

namespace FppTest {

  namespace {

    // Constant definitions for the state machine signal buffer
    namespace SmSignalBuffer {

      // The serialized size
      static constexpr FwSizeType SERIALIZED_SIZE =
        2 * sizeof(FwEnumStoreType);

    }

    enum MsgTypeEnum {
      SMJUNCTIONQUEUED_COMPONENT_EXIT = Fw::ActiveComponentBase::ACTIVE_COMPONENT_EXIT,
      INTERNAL_STATE_MACHINE_SIGNAL,
    };

    // Get the max size by constructing a union of the async input, command, and
    // internal port serialization sizes
    union BuffUnion {
      // Size of buffer for internal state machine signals
      // The internal SmSignalBuffer stores the state machine id, the
      // signal id, and the signal data
      BYTE internalSmBufferSize[SmSignalBuffer::SERIALIZED_SIZE];
    };

    // Define a message buffer class large enough to handle all the
    // asynchronous inputs to the component
    class ComponentIpcSerializableBuffer :
      public Fw::SerializeBufferBase
    {

      public:

        enum {
          // Offset into data in buffer: Size of message ID and port number
          DATA_OFFSET = sizeof(FwEnumStoreType) + sizeof(FwIndexType),
          // Max data size
          MAX_DATA_SIZE = sizeof(BuffUnion),
          // Max message size: Size of message id + size of port + max data size
          SERIALIZATION_SIZE = DATA_OFFSET + MAX_DATA_SIZE
        };

        Fw::Serializable::SizeType getBuffCapacity() const {
          return sizeof(m_buff);
        }

        U8* getBuffAddr() {
          return m_buff;
        }

        const U8* getBuffAddr() const {
          return m_buff;
        }

      private:
        // Should be the max of all the input ports serialized sizes...
        U8 m_buff[SERIALIZATION_SIZE];

    };
  }

  // ----------------------------------------------------------------------
  // Types for internal state machines
  // ----------------------------------------------------------------------

  SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic ::
    FppTest_SmJunction_Basic(SmJunctionQueuedComponentBase& component) :
      m_component(component)
  {

  }

  void SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic ::
    init(SmJunctionQueuedComponentBase::SmId smId)
  {
    this->initBase(static_cast<FwEnumStoreType>(smId));
  }

  SmJunctionQueuedComponentBase::SmId SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic ::
    getId() const
  {
    return static_cast<SmJunctionQueuedComponentBase::SmId>(this->m_id);
  }

  void SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic ::
    action_a(Signal signal)
  {
    this->m_component.FppTest_SmJunction_Basic_action_a(this->getId(), signal);
  }

  void SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic ::
    action_b(Signal signal)
  {
    this->m_component.FppTest_SmJunction_Basic_action_b(this->getId(), signal);
  }

  bool SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic ::
    guard_g(Signal signal) const
  {
    return this->m_component.FppTest_SmJunction_Basic_guard_g(this->getId(), signal);
  }

  SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic ::
    FppTest_SmJunctionQueued_Basic(SmJunctionQueuedComponentBase& component) :
      m_component(component)
  {

  }

  void SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic ::
    init(SmJunctionQueuedComponentBase::SmId smId)
  {
    this->initBase(static_cast<FwEnumStoreType>(smId));
  }

  SmJunctionQueuedComponentBase::SmId SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic ::
    getId() const
  {
    return static_cast<SmJunctionQueuedComponentBase::SmId>(this->m_id);
  }

  void SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic ::
    action_a(Signal signal)
  {
    this->m_component.FppTest_SmJunctionQueued_Basic_action_a(this->getId(), signal);
  }

  void SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic ::
    action_b(Signal signal)
  {
    this->m_component.FppTest_SmJunctionQueued_Basic_action_b(this->getId(), signal);
  }

  bool SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic ::
    guard_g(Signal signal) const
  {
    return this->m_component.FppTest_SmJunctionQueued_Basic_guard_g(this->getId(), signal);
  }

  // ----------------------------------------------------------------------
  // Component initialization
  // ----------------------------------------------------------------------

  void SmJunctionQueuedComponentBase ::
    init(
        FwSizeType queueDepth,
        FwEnumStoreType instance
    )
  {
    // Initialize base class
    Fw::QueuedComponentBase::init(instance);

    this->m_stateMachine_basic.init(SmId::basic);
    this->m_stateMachine_smJunctionBasic.init(SmId::smJunctionBasic);

    Os::Queue::Status qStat = this->createQueue(
      queueDepth,
      static_cast<FwSizeType>(ComponentIpcSerializableBuffer::SERIALIZATION_SIZE)
    );
    FW_ASSERT(
      Os::Queue::Status::OP_OK == qStat,
      static_cast<FwAssertArgType>(qStat)
    );
  }

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  SmJunctionQueuedComponentBase ::
    SmJunctionQueuedComponentBase(const char* compName) :
      Fw::QueuedComponentBase(compName),
      m_stateMachine_basic(*this),
      m_stateMachine_smJunctionBasic(*this)
  {

  }

  SmJunctionQueuedComponentBase ::
    ~SmJunctionQueuedComponentBase()
  {

  }

  // ----------------------------------------------------------------------
  // State getter functions
  // ----------------------------------------------------------------------

  SmJunctionQueuedComponentBase::FppTest_SmJunctionQueued_Basic::State SmJunctionQueuedComponentBase ::
    basic_getState() const
  {
    return this->m_stateMachine_basic.getState();
  }

  SmJunctionQueuedComponentBase::FppTest_SmJunction_Basic::State SmJunctionQueuedComponentBase ::
    smJunctionBasic_getState() const
  {
    return this->m_stateMachine_smJunctionBasic.getState();
  }

  // ----------------------------------------------------------------------
  // Signal send functions
  // ----------------------------------------------------------------------

  void SmJunctionQueuedComponentBase ::
    basic_sendSignal_s()
  {
    ComponentIpcSerializableBuffer buffer;
    // Serialize the message type, port number, state ID, and signal
    this->sendSignalStart(SmId::basic, static_cast<FwEnumStoreType>(FppTest_SmJunctionQueued_Basic::Signal::s), buffer);
    // Send the message and handle overflow
    this->basic_sendSignalFinish(buffer);
  }

  void SmJunctionQueuedComponentBase ::
    smJunctionBasic_sendSignal_s()
  {
    ComponentIpcSerializableBuffer buffer;
    // Serialize the message type, port number, state ID, and signal
    this->sendSignalStart(SmId::smJunctionBasic, static_cast<FwEnumStoreType>(FppTest_SmJunction_Basic::Signal::s), buffer);
    // Send the message and handle overflow
    this->smJunctionBasic_sendSignalFinish(buffer);
  }

  // ----------------------------------------------------------------------
  // Message dispatch functions
  // ----------------------------------------------------------------------

  Fw::QueuedComponentBase::MsgDispatchStatus SmJunctionQueuedComponentBase ::
    doDispatch()
  {
    ComponentIpcSerializableBuffer msg;
    FwQueuePriorityType priority = 0;

    Os::Queue::Status msgStatus = this->m_queue.receive(
      msg,
      Os::Queue::NONBLOCKING,
      priority
    );
    if (Os::Queue::Status::EMPTY == msgStatus) {
      return Fw::QueuedComponentBase::MSG_DISPATCH_EMPTY;
    }
    else {
      FW_ASSERT(
        msgStatus == Os::Queue::OP_OK,
        static_cast<FwAssertArgType>(msgStatus)
      );
    }

    // Reset to beginning of buffer
    msg.resetDeser();

    FwEnumStoreType desMsg = 0;
    Fw::SerializeStatus deserStatus = msg.deserialize(desMsg);
    FW_ASSERT(
      deserStatus == Fw::FW_SERIALIZE_OK,
      static_cast<FwAssertArgType>(deserStatus)
    );

    MsgTypeEnum msgType = static_cast<MsgTypeEnum>(desMsg);

    if (msgType == SMJUNCTIONQUEUED_COMPONENT_EXIT) {
      return MSG_DISPATCH_EXIT;
    }

    FwIndexType portNum = 0;
    deserStatus = msg.deserialize(portNum);
    FW_ASSERT(
      deserStatus == Fw::FW_SERIALIZE_OK,
      static_cast<FwAssertArgType>(deserStatus)
    );

    switch (msgType) {

      // Handle signals to internal state machines
      case INTERNAL_STATE_MACHINE_SIGNAL:
        this->smDispatch(msg);
        break;

      default:
        return MSG_DISPATCH_ERROR;
    }

    return MSG_DISPATCH_OK;
  }

  // ----------------------------------------------------------------------
  // Send signal helper functions
  // ----------------------------------------------------------------------

  void SmJunctionQueuedComponentBase ::
    sendSignalStart(
        SmId smId,
        FwEnumStoreType signal,
        Fw::SerializeBufferBase& buffer
    )
  {
    Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;

    // Serialize the message type
    status = buffer.serialize(static_cast<FwEnumStoreType>(INTERNAL_STATE_MACHINE_SIGNAL));
    FW_ASSERT (status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));

    // Serialize the port number
    status = buffer.serialize(static_cast<FwIndexType>(0));
    FW_ASSERT (status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));

    // Serialize the state machine ID
    status = buffer.serialize(static_cast<FwEnumStoreType>(smId));
    FW_ASSERT (status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));

    // Serialize the signal
    status = buffer.serialize(static_cast<FwEnumStoreType>(signal));
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
  }

  void SmJunctionQueuedComponentBase ::
    basic_sendSignalFinish(Fw::SerializeBufferBase& buffer)
  {
    // Send message
    Os::Queue::BlockingType _block = Os::Queue::NONBLOCKING;
    Os::Queue::Status qStatus = this->m_queue.send(buffer, 0, _block);

    FW_ASSERT(
      qStatus == Os::Queue::OP_OK,
      static_cast<FwAssertArgType>(qStatus)
    );
  }

  void SmJunctionQueuedComponentBase ::
    smJunctionBasic_sendSignalFinish(Fw::SerializeBufferBase& buffer)
  {
    // Send message
    Os::Queue::BlockingType _block = Os::Queue::NONBLOCKING;
    Os::Queue::Status qStatus = this->m_queue.send(buffer, 1, _block);

    FW_ASSERT(
      qStatus == Os::Queue::OP_OK,
      static_cast<FwAssertArgType>(qStatus)
    );
  }

  // ----------------------------------------------------------------------
  // Helper functions for state machine dispatch
  // ----------------------------------------------------------------------

  void SmJunctionQueuedComponentBase ::
    smDispatch(Fw::SerializeBufferBase& buffer)
  {
    // Deserialize the state machine ID and signal
    FwEnumStoreType storedSmId;
    FwEnumStoreType storedSignal;
    SmJunctionQueuedComponentBase::deserializeSmIdAndSignal(buffer, storedSmId, storedSignal);

    // Select the target state machine instance
    const SmId smId = static_cast<SmId>(storedSmId);
    switch (smId) {
      case SmId::basic: {
        const FppTest_SmJunctionQueued_Basic::Signal signal = static_cast<FppTest_SmJunctionQueued_Basic::Signal>(storedSignal);
        this->FppTest_SmJunctionQueued_Basic_smDispatch(buffer, this->m_stateMachine_basic, signal);
        break;
      }
      case SmId::smJunctionBasic: {
        const FppTest_SmJunction_Basic::Signal signal = static_cast<FppTest_SmJunction_Basic::Signal>(storedSignal);
        this->FppTest_SmJunction_Basic_smDispatch(buffer, this->m_stateMachine_smJunctionBasic, signal);
        break;
      }
      default:
        FW_ASSERT(0, static_cast<FwAssertArgType>(smId));
        break;
    }
  }

  void SmJunctionQueuedComponentBase ::
    deserializeSmIdAndSignal(
        Fw::SerializeBufferBase& buffer,
        FwEnumStoreType& smId,
        FwEnumStoreType& signal
    )
  {
    // Move deserialization beyond the message type and port number
    Fw::SerializeStatus status =
      buffer.moveDeserToOffset(ComponentIpcSerializableBuffer::DATA_OFFSET);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));

    // Deserialize the state machine ID
    status = buffer.deserialize(smId);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));

    // Deserialize the signal
    status = buffer.deserialize(signal);
    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
  }

  void SmJunctionQueuedComponentBase ::
    FppTest_SmJunction_Basic_smDispatch(
        Fw::SerializeBufferBase& buffer,
        FppTest_SmJunction_Basic& sm,
        FppTest_SmJunction_Basic::Signal signal
    )
  {
    switch (signal) {
      case FppTest_SmJunction_Basic::Signal::s: {
        // Assert no data left in buffer
        FW_ASSERT(buffer.getBuffLeft() == 0, static_cast<FwAssertArgType>(buffer.getBuffLeft()));
        // Call the sendSignal function for sm and s
        sm.sendSignal_s();
        break;
      }
      default:
        FW_ASSERT(0, static_cast<FwAssertArgType>(signal));
        break;
    }
  }

  void SmJunctionQueuedComponentBase ::
    FppTest_SmJunctionQueued_Basic_smDispatch(
        Fw::SerializeBufferBase& buffer,
        FppTest_SmJunctionQueued_Basic& sm,
        FppTest_SmJunctionQueued_Basic::Signal signal
    )
  {
    switch (signal) {
      case FppTest_SmJunctionQueued_Basic::Signal::s: {
        // Assert no data left in buffer
        FW_ASSERT(buffer.getBuffLeft() == 0, static_cast<FwAssertArgType>(buffer.getBuffLeft()));
        // Call the sendSignal function for sm and s
        sm.sendSignal_s();
        break;
      }
      default:
        FW_ASSERT(0, static_cast<FwAssertArgType>(signal));
        break;
    }
  }

}
