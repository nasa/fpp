#ifndef SerialPortsActive_Receiver_HPP
#define SerialPortsActive_Receiver_HPP

#include "ReceiverComponentAc.hpp"

namespace SerialPortsActive {

  class Receiver final :
    public ReceiverComponentBase
  {

    public:

      Receiver() {

      }

      Receiver(const char* name) {

      }

      void pTypedSync_handler(
          FwIndexType portNum,
          U32 x1,
          F32 x2,
          bool x3,
          const Fw::StringBase& x4,
          const SerialPortsActive::A& x5,
          const SerialPortsActive::E& x6,
          const SerialPortsActive::S& x7
      ) override {

      }

      void pTypedAsync_handler(
          FwIndexType portNum,
          U32 x1,
          F32 x2,
          bool x3,
          const Fw::StringBase& x4,
          const SerialPortsActive::A& x5,
          const SerialPortsActive::E& x6,
          const SerialPortsActive::S& x7
      ) override {

      }

      void pSerialSync_handler(
          FwIndexType portNum,
          Fw::LinearBufferBase& buffer
      ) override {

      }

      void pSerialAsync_handler(
          FwIndexType portNum,
          Fw::LinearBufferBase& buffer
      ) override {

      }

  };

}

#endif
