// ======================================================================
// \title  FppTypePortAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for FppType port
// ======================================================================

#ifndef FppTypePortAc_HPP
#define FppTypePortAc_HPP

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

//! Input FppType port
//! A port with FPP type parameters
class InputFppTypePort :
  public Fw::InputPortBase
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the serial representations of the port arguments
      SERIALIZED_SIZE =
        E::SERIALIZED_SIZE +
        E::SERIALIZED_SIZE +
        A::SERIALIZED_SIZE +
        A::SERIALIZED_SIZE +
        S::SERIALIZED_SIZE +
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
      const E& e,
      E& eRef,
      const A& a,
      A& aRef,
      const S& s,
      S& sRef
    );

  public:

    // ----------------------------------------------------------------------
    // Input Port Member functions
    // ----------------------------------------------------------------------

    //! Constructor
    InputFppTypePort();

    //! Initialization function
    void init();

    //! Register a component
    void addCallComp(
        Fw::PassiveComponentBase* callComp, //!< The containing component
        CompFuncPtr funcPtr //!< The port callback function
    );

    //! Invoke a port interface
    void invoke(
        const E& e, //!< An enum
                    //!< Line 2 of the comment
        E& eRef, //!< An enum ref
                 //!< Line 2 of the comment
        const A& a, //!< An array
        A& aRef, //!< An array ref
        const S& s, //!< A struct
        S& sRef //!< A struct ref
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

//! Output FppType port
//! A port with FPP type parameters
class OutputFppTypePort :
  public Fw::OutputPortBase
{

  public:

    // ----------------------------------------------------------------------
    // Output Port Member functions
    // ----------------------------------------------------------------------

    //! Constructor
    OutputFppTypePort();

    //! Initialization function
    void init();

    //! Register an input port
    void addCallPort(
        InputFppTypePort* callPort //!< The input port
    );

    //! Invoke a port interface
    void invoke(
        const E& e, //!< An enum
                    //!< Line 2 of the comment
        E& eRef, //!< An enum ref
                 //!< Line 2 of the comment
        const A& a, //!< An array
        A& aRef, //!< An array ref
        const S& s, //!< A struct
        S& sRef //!< A struct ref
    ) const;

  private:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    //! The pointer to the input port
    InputFppTypePort* m_port;

};

#endif
