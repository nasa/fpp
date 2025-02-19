// ======================================================================
// \title  QueuedAsyncProductPortsOnly.hpp
// \author [user name]
// \brief  hpp file for QueuedAsyncProductPortsOnly component implementation class
// ======================================================================

#ifndef QueuedAsyncProductPortsOnly_HPP
#define QueuedAsyncProductPortsOnly_HPP

#include "QueuedAsyncProductPortsOnlyComponentAc.hpp"

class QueuedAsyncProductPortsOnly final :
  public QueuedAsyncProductPortsOnlyComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct QueuedAsyncProductPortsOnly object
    QueuedAsyncProductPortsOnly(
        const char* const compName //!< The component name
    );

    //! Destroy QueuedAsyncProductPortsOnly object
    ~QueuedAsyncProductPortsOnly();

};

#endif
