#ifndef SerialPortsPassive_Receiver_HPP
#define SerialPortsPassive_Receiver_HPP

#include "ReceiverComponentAc.hpp"

namespace SerialPortsPassive {

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
          const SerialPortsPassive::A& x5,
          const SerialPortsPassive::E& x6,
          const SerialPortsPassive::S& x7
      ) override {

      }

      void pTypedGuarded_handler(
          FwIndexType portNum,
          U32 x1,
          F32 x2,
          bool x3,
          const Fw::StringBase& x4,
          const SerialPortsPassive::A& x5,
          const SerialPortsPassive::E& x6,
          const SerialPortsPassive::S& x7
      ) override {

      }

      void pSerialSync_handler(
          FwIndexType portNum,
          Fw::LinearBufferBase& buffer
      ) override {

      }

      void pSerialGuarded_handler(
          FwIndexType portNum,
          Fw::LinearBufferBase& buffer
      ) override {

      }

  };

}

#endif
