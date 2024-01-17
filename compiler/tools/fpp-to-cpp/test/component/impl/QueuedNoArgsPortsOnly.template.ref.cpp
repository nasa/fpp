// ======================================================================
// \title  QueuedNoArgsPortsOnly.cpp
// \author [user name]
// \brief  cpp file for QueuedNoArgsPortsOnly component implementation class
// ======================================================================

#include "FpConfig.hpp"
#include "QueuedNoArgsPortsOnly.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedNoArgsPortsOnly ::
  QueuedNoArgsPortsOnly(const char* const compName) :
    QueuedNoArgsPortsOnlyComponentBase(compName)
{

}

QueuedNoArgsPortsOnly ::
  ~QueuedNoArgsPortsOnly()
{

}

// ----------------------------------------------------------------------
// Handler implementations for user-defined typed input ports
// ----------------------------------------------------------------------

void QueuedNoArgsPortsOnly ::
  noArgsAsync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

void QueuedNoArgsPortsOnly ::
  noArgsGuarded_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

U32 QueuedNoArgsPortsOnly ::
  noArgsReturnGuarded_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}

U32 QueuedNoArgsPortsOnly ::
  noArgsReturnSync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}
