// ======================================================================
// \title  PassiveSyncProductPortsOnly.hpp
// \author [user name]
// \brief  hpp file for PassiveSyncProductPortsOnly component implementation class
// ======================================================================

#ifndef PassiveSyncProductPortsOnly_HPP
#define PassiveSyncProductPortsOnly_HPP

#include "PassiveSyncProductPortsOnlyComponentAc.hpp"

class PassiveSyncProductPortsOnly final :
  public PassiveSyncProductPortsOnlyComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct PassiveSyncProductPortsOnly object
    PassiveSyncProductPortsOnly(
        const char* const compName //!< The component name
    );

    //! Destroy PassiveSyncProductPortsOnly object
    ~PassiveSyncProductPortsOnly();

};

#endif
