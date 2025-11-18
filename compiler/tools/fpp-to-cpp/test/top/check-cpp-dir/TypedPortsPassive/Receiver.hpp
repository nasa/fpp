#ifndef TypedPortsPassive_Receiver_HPP
#define TypedPortsPassive_Receiver_HPP

#include "ReceiverComponentAc.hpp"

namespace TypedPortsPassive {

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
          const TypedPortsPassive::A& x5,
          const TypedPortsPassive::E& x6,
          const TypedPortsPassive::S& x7
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

      TypedPortsPassive::A p6_handler(
          FwIndexType portNum,
          const TypedPortsPassive::A& x
      ) override {
        return x;
      }

      TypedPortsPassive::E p7_handler(
          FwIndexType portNum,
          const TypedPortsPassive::E& x
      ) override {
        return x;
      }

      TypedPortsPassive::S p8_handler(
          FwIndexType portNum,
          const TypedPortsPassive::S& x
      ) override {
        return x;
      }

  };

}

#endif
