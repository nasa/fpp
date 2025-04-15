// ======================================================================
// \title  QueuedNoArgsPortsOnly.cpp
// \author [user name]
// \brief  cpp file for QueuedNoArgsPortsOnly component implementation class
// ======================================================================

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
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void QueuedNoArgsPortsOnly ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void QueuedNoArgsPortsOnly ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 QueuedNoArgsPortsOnly ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 QueuedNoArgsPortsOnly ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}
