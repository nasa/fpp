// ======================================================================
// \title  ActiveNoArgsPortsOnly.cpp
// \author [user name]
// \brief  cpp file for ActiveNoArgsPortsOnly component implementation class
// ======================================================================

#include "ActiveNoArgsPortsOnly.hpp"

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

ActiveNoArgsPortsOnly ::
  ActiveNoArgsPortsOnly(const char* const compName) :
    ActiveNoArgsPortsOnlyComponentBase(compName)
{

}

ActiveNoArgsPortsOnly ::
  ~ActiveNoArgsPortsOnly()
{

}

// ----------------------------------------------------------------------
// Handler implementations for typed input ports
// ----------------------------------------------------------------------

void ActiveNoArgsPortsOnly ::
  noArgsAsync_handler(FwIndexType portNum)
{
  // TODO
}

void ActiveNoArgsPortsOnly ::
  noArgsGuarded_handler(FwIndexType portNum)
{
  // TODO
}

U32 ActiveNoArgsPortsOnly ::
  noArgsReturnGuarded_handler(FwIndexType portNum)
{
  // TODO return
}

U32 ActiveNoArgsPortsOnly ::
  noArgsReturnSync_handler(FwIndexType portNum)
{
  // TODO return
}
