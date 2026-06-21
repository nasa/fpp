#ifndef TypedPortsQueued_Receiver_HPP
#define TypedPortsQueued_Receiver_HPP

#include "ReceiverComponentAc.hpp"

namespace TypedPortsQueued {

  class Receiver final :
    public ReceiverComponentBase
  {

    public:

      Receiver() {

      }

      Receiver(const char* name) {

      }

      void p1_handler(
          FwIndexType portNum,
          U32 x1,
          F32 x2,
          bool x3,
          const Fw::StringBase& x4,
          const TypedPortsQueued::A& x5,
          const TypedPortsQueued::E& x6,
          const TypedPortsQueued::S& x7
      ) override {

      }

      U32 p2_handler(
          FwIndexType portNum,
          U32 x
      ) override {
        return x;
      }

      F32 p3_handler(
          FwIndexType portNum,
          F32 x
      ) override {
        return x;
      }

      bool p4_handler(
          FwIndexType portNum,
          bool x
      ) override {
        return x;
      }

      Fw::String p5_handler(
          FwIndexType portNum,
          const Fw::StringBase& x
      ) override {
        return x;
      }

      TypedPortsQueued::A p6_handler(
          FwIndexType portNum,
          const TypedPortsQueued::A& x
      ) override {
        return x;
      }

      TypedPortsQueued::E p7_handler(
          FwIndexType portNum,
          const TypedPortsQueued::E& x
      ) override {
        return x;
      }

      TypedPortsQueued::S p8_handler(
          FwIndexType portNum,
          const TypedPortsQueued::S& x
      ) override {
        return x;
      }

  };

}

#endif
