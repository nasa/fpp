// ======================================================================
// \title  Empty.hpp
// \author [user name]
// \brief  hpp file for Empty component implementation class
// ======================================================================

#ifndef Empty_HPP
#define Empty_HPP

#include "EmptyComponentAc.hpp"

class Empty final :
  public EmptyComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct Empty object
    Empty(
        const char* const compName //!< The component name
    );

    //! Destroy Empty object
    ~Empty();

};

#endif
