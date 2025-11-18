#ifndef M_TypedPortsPassiveReceiver_HPP
#define M_TypedPortsPassiveReceiver_HPP

#include "TypedPortsPassiveReceiverComponentAc.hpp"

namespace M {

  class TypedPortsPassiveReceiver final :
    public TypedPortsPassiveReceiverComponentBase
  {

    public:

      TypedPortsPassiveReceiver() {

      }

      TypedPortsPassiveReceiver(const char* name) {

      }

      void p1_handler(
          FwIndexType portNum,
          U32 x1,
          F32 x2,
          bool x3,
          const Fw::StringBase& x4,
          const M::A& x5,
          const M::E& x6,
          const M::S& x7
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

      M::A p6_handler(
          FwIndexType portNum,
          const M::A& x
      ) override {
        return x;
      }

      M::E p7_handler(
          FwIndexType portNum,
          const M::E& x
      ) override {
        return x;
      }

      M::S p8_handler(
          FwIndexType portNum,
          const M::S& x
      ) override {
        return x;
      }

  };

}

#endif
