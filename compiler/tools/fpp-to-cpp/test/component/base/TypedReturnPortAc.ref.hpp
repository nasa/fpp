// ======================================================================
// \title  TypedReturnPortAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for TypedReturn port
// ======================================================================

#ifndef Ports_TypedReturnPortAc_HPP
#define Ports_TypedReturnPortAc_HPP

#include <cstdio>
#include <cstring>
#include <FpConfig.hpp>

#include "AArrayAc.hpp"
#include "EEnumAc.hpp"
#include "Fw/Comp/PassiveComponentBase.hpp"
#include "Fw/Port/InputPortBase.hpp"
#include "Fw/Port/OutputPortBase.hpp"
#include "Fw/Types/String.hpp"
#include "SSerializableAc.hpp"

namespace Ports {

  //! Input TypedReturn port
  //! A typed port with a return type
  class InputTypedReturnPort :
    public Fw::InputPortBase
  {

    public:

      // ----------------------------------------------------------------------
      // Constants
      // ----------------------------------------------------------------------

      enum {
        //! The size of the serial representations of the port arguments
        SERIALIZED_SIZE =
          sizeof(U32) +
          sizeof(F32) +
          sizeof(U8) +
          Fw::StringBase::STATIC_SERIALIZED_SIZE(80) +
          E::SERIALIZED_SIZE +
          A::SERIALIZED_SIZE +
          S::SERIALIZED_SIZE
      };

    public:

      // ----------------------------------------------------------------------
      // Types
      // ----------------------------------------------------------------------

      //! The port callback function type
      typedef F32 (*CompFuncPtr)(
        Fw::PassiveComponentBase* callComp,
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str2,
        const E& e,
        const A& a,
        const S& s
      );

    public:

      // ----------------------------------------------------------------------
      // Input Port Member functions
      // ----------------------------------------------------------------------

      //! Constructor
      InputTypedReturnPort();

      //! Initialization function
      void init();

      //! Register a component
      void addCallComp(
          Fw::PassiveComponentBase* callComp, //!< The containing component
          CompFuncPtr funcPtr //!< The port callback function
      );

      //! Invoke a port interface
      F32 invoke(
          U32 u32, //!< A U32
          F32 f32, //!< An F32
          bool b, //!< A boolean
          const Fw::StringBase& str2, //!< A string
          const E& e, //!< An enum
          const A& a, //!< An array
          const S& s //!< A struct
      );

    private:

#if FW_PORT_SERIALIZATION == 1

      //! Invoke the port with serialized arguments
      Fw::SerializeStatus invokeSerial(Fw::SerializeBufferBase& _buffer);

#endif

    private:

      // ----------------------------------------------------------------------
      // Member variables
      // ----------------------------------------------------------------------

      //! The pointer to the port callback function
      CompFuncPtr m_func;

  };

  //! Output TypedReturn port
  //! A typed port with a return type
  class OutputTypedReturnPort :
    public Fw::OutputPortBase
  {

    public:

      // ----------------------------------------------------------------------
      // Output Port Member functions
      // ----------------------------------------------------------------------

      //! Constructor
      OutputTypedReturnPort();

      //! Initialization function
      void init();

      //! Register an input port
      void addCallPort(
          InputTypedReturnPort* callPort //!< The input port
      );

      //! Invoke a port interface
      F32 invoke(
          U32 u32, //!< A U32
          F32 f32, //!< An F32
          bool b, //!< A boolean
          const Fw::StringBase& str2, //!< A string
          const E& e, //!< An enum
          const A& a, //!< An array
          const S& s //!< A struct
      ) const;

    private:

      // ----------------------------------------------------------------------
      // Member variables
      // ----------------------------------------------------------------------

      //! The pointer to the input port
      InputTypedReturnPort* m_port;

  };

}

#endif
