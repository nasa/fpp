// ======================================================================
// \title  ActiveNoArgsPortsOnly.hpp
// \author [user name]
// \brief  hpp file for ActiveNoArgsPortsOnly component implementation class
// ======================================================================

#ifndef ActiveNoArgsPortsOnly_HPP
#define ActiveNoArgsPortsOnly_HPP

#include "ActiveNoArgsPortsOnlyComponentAc.hpp"

class ActiveNoArgsPortsOnly :
  public ActiveNoArgsPortsOnlyComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveNoArgsPortsOnly object
    ActiveNoArgsPortsOnly(
        const char* const compName //!< The component name
    );

    //! Destroy ActiveNoArgsPortsOnly object
    ~ActiveNoArgsPortsOnly();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for user-defined typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for noArgsAsync
    //!
    //! A typed async input port
    void noArgsAsync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsGuarded
    //!
    //! A typed guarded input
    void noArgsGuarded_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsReturnGuarded
    //!
    //! A typed guarded input
    U32 noArgsReturnGuarded_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsReturnSync
    //!
    //! A typed sync input port
    U32 noArgsReturnSync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    ) override;

};

#endif
