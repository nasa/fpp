// ======================================================================
// \title  ActiveNoArgsPortsOnly.hpp
// \author [user name]
// \brief  hpp file for ActiveNoArgsPortsOnly component implementation class
// ======================================================================

#ifndef ActiveNoArgsPortsOnly_HPP
#define ActiveNoArgsPortsOnly_HPP

#include "ActiveNoArgsPortsOnlyComponentAc.hpp"

class ActiveNoArgsPortsOnly final :
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
    // Handler implementations for typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for noArgsAsync
    //!
    //! A typed async input port
    void noArgsAsync_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsGuarded
    //!
    //! A typed guarded input
    void noArgsGuarded_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsReturnGuarded
    //!
    //! A typed guarded input
    U32 noArgsReturnGuarded_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsReturnSync
    //!
    //! A typed sync input port
    U32 noArgsReturnSync_handler(
        FwIndexType portNum //!< The port number
    ) override;

};

#endif
