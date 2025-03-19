// ======================================================================
// \title  QueuedSyncProductsTesterHelpers.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for QueuedSyncProducts component test harness helper functions
// ======================================================================

#include "QueuedSyncProductsTester.hpp"

// ----------------------------------------------------------------------
// Helper functions
// ----------------------------------------------------------------------

void QueuedSyncProductsTester ::
  connectPorts()
{
  // Connect special input ports

  this->connect_to_productRecvIn(
    0,
    this->component.get_productRecvIn_InputPort(0)
  );

  // Connect special output ports

  this->component.set_productRequestOut_OutputPort(
    0,
    this->get_from_productRequestOut(0)
  );

  this->component.set_productSendOut_OutputPort(
    0,
    this->get_from_productSendOut(0)
  );

  this->component.set_timeGetOut_OutputPort(
    0,
    this->get_from_timeGetOut(0)
  );

  // Connect typed input ports

  this->connect_to_aliasTypedAsync(
    0,
    this->component.get_aliasTypedAsync_InputPort(0)
  );

  this->connect_to_noArgsAliasStringReturnSync(
    0,
    this->component.get_noArgsAliasStringReturnSync_InputPort(0)
  );

  this->connect_to_noArgsAsync(
    0,
    this->component.get_noArgsAsync_InputPort(0)
  );

  this->connect_to_noArgsGuarded(
    0,
    this->component.get_noArgsGuarded_InputPort(0)
  );

  this->connect_to_noArgsReturnGuarded(
    0,
    this->component.get_noArgsReturnGuarded_InputPort(0)
  );

  for (FwIndexType i = 0; i < 3; i++) {
    this->connect_to_noArgsReturnSync(
      i,
      this->component.get_noArgsReturnSync_InputPort(i)
    );
  }

  this->connect_to_noArgsStringReturnSync(
    0,
    this->component.get_noArgsStringReturnSync_InputPort(0)
  );

  for (FwIndexType i = 0; i < 3; i++) {
    this->connect_to_noArgsSync(
      i,
      this->component.get_noArgsSync_InputPort(i)
    );
  }

  this->connect_to_typedAliasGuarded(
    0,
    this->component.get_typedAliasGuarded_InputPort(0)
  );

  for (FwIndexType i = 0; i < 3; i++) {
    this->connect_to_typedAliasReturnSync(
      i,
      this->component.get_typedAliasReturnSync_InputPort(i)
    );
  }

  for (FwIndexType i = 0; i < 3; i++) {
    this->connect_to_typedAliasStringReturnSync(
      i,
      this->component.get_typedAliasStringReturnSync_InputPort(i)
    );
  }

  this->connect_to_typedAsync(
    0,
    this->component.get_typedAsync_InputPort(0)
  );

  this->connect_to_typedAsyncAssert(
    0,
    this->component.get_typedAsyncAssert_InputPort(0)
  );

  this->connect_to_typedAsyncBlockPriority(
    0,
    this->component.get_typedAsyncBlockPriority_InputPort(0)
  );

  this->connect_to_typedAsyncDropPriority(
    0,
    this->component.get_typedAsyncDropPriority_InputPort(0)
  );

  this->connect_to_typedGuarded(
    0,
    this->component.get_typedGuarded_InputPort(0)
  );

  this->connect_to_typedReturnGuarded(
    0,
    this->component.get_typedReturnGuarded_InputPort(0)
  );

  for (FwIndexType i = 0; i < 3; i++) {
    this->connect_to_typedReturnSync(
      i,
      this->component.get_typedReturnSync_InputPort(i)
    );
  }

  for (FwIndexType i = 0; i < 3; i++) {
    this->connect_to_typedSync(
      i,
      this->component.get_typedSync_InputPort(i)
    );
  }

  // Connect typed output ports

  this->component.set_noArgsOut_OutputPort(
    0,
    this->get_from_noArgsOut(0)
  );

  this->component.set_noArgsReturnOut_OutputPort(
    0,
    this->get_from_noArgsReturnOut(0)
  );

  this->component.set_noArgsStringReturnOut_OutputPort(
    0,
    this->get_from_noArgsStringReturnOut(0)
  );

  this->component.set_typedAliasOut_OutputPort(
    0,
    this->get_from_typedAliasOut(0)
  );

  this->component.set_typedAliasReturnOut_OutputPort(
    0,
    this->get_from_typedAliasReturnOut(0)
  );

  this->component.set_typedAliasReturnStringOut_OutputPort(
    0,
    this->get_from_typedAliasReturnStringOut(0)
  );

  this->component.set_typedOut_OutputPort(
    0,
    this->get_from_typedOut(0)
  );

  this->component.set_typedReturnOut_OutputPort(
    0,
    this->get_from_typedReturnOut(0)
  );
}

void QueuedSyncProductsTester ::
  initComponents()
{
  this->init();
  this->component.init(QueuedSyncProductsTester::TEST_INSTANCE_QUEUE_DEPTH, QueuedSyncProductsTester::TEST_INSTANCE_ID);
}
