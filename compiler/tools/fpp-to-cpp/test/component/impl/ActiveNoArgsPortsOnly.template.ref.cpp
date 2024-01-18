// ======================================================================
// \title  ActiveNoArgsPortsOnly.cpp
// \author [user name]
// \brief  cpp file for ActiveNoArgsPortsOnly component implementation class
// ======================================================================

#include "ActiveNoArgsPortsOnly.hpp"
#include "FpConfig.hpp"

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
// Handler implementations for user-defined typed input ports
// ----------------------------------------------------------------------

void ActiveNoArgsPortsOnly ::
  noArgsAsync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

void ActiveNoArgsPortsOnly ::
  noArgsGuarded_handler(NATIVE_INT_TYPE portNum)
{
  // TODO
}

U32 ActiveNoArgsPortsOnly ::
  noArgsReturnGuarded_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}

U32 ActiveNoArgsPortsOnly ::
  noArgsReturnSync_handler(NATIVE_INT_TYPE portNum)
{
  // TODO return
}
