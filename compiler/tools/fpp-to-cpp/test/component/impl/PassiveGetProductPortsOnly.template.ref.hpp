// ======================================================================
// \title  PassiveGetProductPortsOnly.hpp
// \author [user name]
// \brief  hpp file for PassiveGetProductPortsOnly component implementation class
// ======================================================================

#ifndef PassiveGetProductPortsOnly_HPP
#define PassiveGetProductPortsOnly_HPP

#include "PassiveGetProductPortsOnlyComponentAc.hpp"

class PassiveGetProductPortsOnly final :
  public PassiveGetProductPortsOnlyComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct PassiveGetProductPortsOnly object
    PassiveGetProductPortsOnly(
        const char* const compName //!< The component name
    );

    //! Destroy PassiveGetProductPortsOnly object
    ~PassiveGetProductPortsOnly();

};

#endif
