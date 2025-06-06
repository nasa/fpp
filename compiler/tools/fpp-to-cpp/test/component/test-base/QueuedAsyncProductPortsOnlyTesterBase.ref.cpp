// ======================================================================
// \title  QueuedAsyncProductPortsOnlyTesterBase.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for QueuedAsyncProductPortsOnly component test harness base class
// ======================================================================

#include <cstdlib>
#include <cstring>

#include "test-base/QueuedAsyncProductPortsOnlyTesterBase.hpp"

// ----------------------------------------------------------------------
// Component initialization
// ----------------------------------------------------------------------

void QueuedAsyncProductPortsOnlyTesterBase ::
  init(FwEnumStoreType instance)
{
  // Initialize base class
  Fw::PassiveComponentBase::init(instance);
}

// ----------------------------------------------------------------------
// Connectors for to ports
// ----------------------------------------------------------------------

void QueuedAsyncProductPortsOnlyTesterBase ::
  connect_to_productRecvIn(
      FwIndexType portNum,
      Fw::InputDpResponsePort* port
  )
{
  FW_ASSERT(
    (0 <= portNum) && (portNum < this->getNum_to_productRecvIn()),
    static_cast<FwAssertArgType>(portNum)
  );

  this->m_to_productRecvIn[portNum].addCallPort(port);
}

// ----------------------------------------------------------------------
// Getters for from ports
// ----------------------------------------------------------------------

Fw::InputDpRequestPort* QueuedAsyncProductPortsOnlyTesterBase ::
  get_from_productRequestOut(FwIndexType portNum)
{
  FW_ASSERT(
    (0 <= portNum) && (portNum < this->getNum_from_productRequestOut()),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_from_productRequestOut[portNum];
}

Fw::InputDpSendPort* QueuedAsyncProductPortsOnlyTesterBase ::
  get_from_productSendOut(FwIndexType portNum)
{
  FW_ASSERT(
    (0 <= portNum) && (portNum < this->getNum_from_productSendOut()),
    static_cast<FwAssertArgType>(portNum)
  );

  return &this->m_from_productSendOut[portNum];
}

// ----------------------------------------------------------------------
// Component construction and destruction
// ----------------------------------------------------------------------

QueuedAsyncProductPortsOnlyTesterBase ::
  QueuedAsyncProductPortsOnlyTesterBase(
      const char* const compName,
      const U32 maxHistorySize
  ) :
    Fw::PassiveComponentBase(compName)
{

}

QueuedAsyncProductPortsOnlyTesterBase ::
  ~QueuedAsyncProductPortsOnlyTesterBase()
{

}

// ----------------------------------------------------------------------
// Getters for port counts
// ----------------------------------------------------------------------

FwIndexType QueuedAsyncProductPortsOnlyTesterBase ::
  getNum_to_productRecvIn() const
{
  return static_cast<FwIndexType>(FW_NUM_ARRAY_ELEMENTS(this->m_to_productRecvIn));
}

FwIndexType QueuedAsyncProductPortsOnlyTesterBase ::
  getNum_from_productRequestOut() const
{
  return static_cast<FwIndexType>(FW_NUM_ARRAY_ELEMENTS(this->m_from_productRequestOut));
}

FwIndexType QueuedAsyncProductPortsOnlyTesterBase ::
  getNum_from_productSendOut() const
{
  return static_cast<FwIndexType>(FW_NUM_ARRAY_ELEMENTS(this->m_from_productSendOut));
}

// ----------------------------------------------------------------------
// Connection status queries for to ports
// ----------------------------------------------------------------------

bool QueuedAsyncProductPortsOnlyTesterBase ::
  isConnected_to_productRecvIn(FwIndexType portNum)
{
  FW_ASSERT(
    (0 <= portNum) && (portNum < this->getNum_to_productRecvIn()),
    static_cast<FwAssertArgType>(portNum)
  );

  return this->m_to_productRecvIn[portNum].isConnected();
}
