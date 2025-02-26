// ======================================================================
// \title  ActiveAsyncProductPortsOnly.hpp
// \author [user name]
// \brief  hpp file for ActiveAsyncProductPortsOnly component implementation class
// ======================================================================

#ifndef ActiveAsyncProductPortsOnly_HPP
#define ActiveAsyncProductPortsOnly_HPP

#include "ActiveAsyncProductPortsOnlyComponentAc.hpp"

class ActiveAsyncProductPortsOnly final :
  public ActiveAsyncProductPortsOnlyComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveAsyncProductPortsOnly object
    ActiveAsyncProductPortsOnly(
        const char* const compName //!< The component name
    );

    //! Destroy ActiveAsyncProductPortsOnly object
    ~ActiveAsyncProductPortsOnly();

};

#endif
