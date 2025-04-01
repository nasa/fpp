// ======================================================================
// \title  TypedPortAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Typed port
// ======================================================================

#ifndef Ports_TypedPortAc_HPP
#define Ports_TypedPortAc_HPP

#include <cstdio>
#include <cstring>

#include "AArrayAc.hpp"
#include "EEnumAc.hpp"
#include "Fw/Comp/PassiveComponentBase.hpp"
#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Port/InputPortBase.hpp"
#include "Fw/Port/OutputPortBase.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"
#include "SSerializableAc.hpp"

namespace Ports {

  //! Input Typed port
  //! A typed port
  class InputTypedPort :
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
      typedef void (*CompFuncPtr)(
        Fw::PassiveComponentBase* callComp,
        FwIndexType portNum,
        U32 u32,
        F32 f32,
        bool b,
        const Fw::StringBase& str1,
        const E& e,
        const A& a,
        const S& s
      );

    public:

      // ----------------------------------------------------------------------
      // Input Port Member functions
      // ----------------------------------------------------------------------

      //! Constructor
      InputTypedPort();

      //! Initialization function
      void init();

      //! Register a component
      void addCallComp(
          Fw::PassiveComponentBase* callComp, //!< The containing component
          CompFuncPtr funcPtr //!< The port callback function
      );

      //! Invoke a port interface
      void invoke(
          U32 u32, //!< A U32
          F32 f32, //!< An F32
          bool b, //!< A boolean
          const Fw::StringBase& str1, //!< A string
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

  //! Output Typed port
  //! A typed port
  class OutputTypedPort :
    public Fw::OutputPortBase
  {

    public:

      // ----------------------------------------------------------------------
      // Output Port Member functions
      // ----------------------------------------------------------------------

      //! Constructor
      OutputTypedPort();

      //! Initialization function
      void init();

      //! Register an input port
      void addCallPort(
          InputTypedPort* callPort //!< The input port
      );

      //! Invoke a port interface
      void invoke(
          U32 u32, //!< A U32
          F32 f32, //!< An F32
          bool b, //!< A boolean
          const Fw::StringBase& str1, //!< A string
          const E& e, //!< An enum
          const A& a, //!< An array
          const S& s //!< A struct
      ) const;

    private:

      // ----------------------------------------------------------------------
      // Member variables
      // ----------------------------------------------------------------------

      //! The pointer to the input port
      InputTypedPort* m_port;

  };

}

#endif
