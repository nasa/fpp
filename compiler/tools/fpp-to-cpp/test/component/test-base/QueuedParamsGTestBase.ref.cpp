// ======================================================================
// \title  QueuedParamsGTestBase.cpp
// \author Generated by fpp-to-cpp
// \brief  cpp file for QueuedParams component Google Test harness base class
// ======================================================================

#include "test-base/QueuedParamsGTestBase.hpp"

// ----------------------------------------------------------------------
// Construction and destruction
// ----------------------------------------------------------------------

QueuedParamsGTestBase ::
  QueuedParamsGTestBase(
      const char* const compName,
      const U32 maxHistorySize
  ) :
    QueuedParamsTesterBase(compName, maxHistorySize)
{

}

QueuedParamsGTestBase ::
  ~QueuedParamsGTestBase()
{

}

// ----------------------------------------------------------------------
// From ports
// ----------------------------------------------------------------------

void QueuedParamsGTestBase ::
  assertFromPortHistory_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistorySize)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Total size of all from port histories\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistorySize << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_noArgsOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistorySize_noArgsOut)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for noArgsOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistorySize_noArgsOut << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_noArgsReturnOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistorySize_noArgsReturnOut)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for noArgsReturnOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistorySize_noArgsReturnOut << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_noArgsStringReturnOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistorySize_noArgsStringReturnOut)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for noArgsStringReturnOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistorySize_noArgsStringReturnOut << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_typedAliasOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistory_typedAliasOut->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for typedAliasOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistory_typedAliasOut->size() << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_typedAliasReturnOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistory_typedAliasReturnOut->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for typedAliasReturnOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistory_typedAliasReturnOut->size() << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_typedAliasReturnStringOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistory_typedAliasReturnStringOut->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for typedAliasReturnStringOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistory_typedAliasReturnStringOut->size() << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_typedOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistory_typedOut->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for typedOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistory_typedOut->size() << "\n";
}

void QueuedParamsGTestBase ::
  assert_from_typedReturnOut_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->fromPortHistory_typedReturnOut->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of history for typedReturnOut\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->fromPortHistory_typedReturnOut->size() << "\n";
}

// ----------------------------------------------------------------------
// Commands
// ----------------------------------------------------------------------

void QueuedParamsGTestBase ::
  assertCmdResponse_size(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 size
  ) const
{
  ASSERT_EQ(size, this->cmdResponseHistory->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Size of command response history\n"
    << "  Expected: " << size << "\n"
    << "  Actual:   " << this->cmdResponseHistory->size() << "\n";
}

void QueuedParamsGTestBase ::
  assertCmdResponse(
      const char* const __callSiteFileName,
      const U32 __callSiteLineNumber,
      const U32 __index,
      FwOpcodeType opCode,
      U32 cmdSeq,
      Fw::CmdResponse response
  ) const
{
  ASSERT_LT(__index, this->cmdResponseHistory->size())
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Index into command response history\n"
    << "  Expected: Less than size of command response history ("
    << this->cmdResponseHistory->size() << ")\n"
    << "  Actual:   " << __index << "\n";
  const CmdResponse& e = this->cmdResponseHistory->at(__index);
  ASSERT_EQ(opCode, e.opCode)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Opcode at index "
    << __index
    << " in command response history\n"
    << "  Expected: " << opCode << "\n"
    << "  Actual:   " << e.opCode << "\n";
  ASSERT_EQ(cmdSeq, e.cmdSeq)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Command sequence number at index "
    << __index
    << " in command response history\n"
    << "  Expected: " << cmdSeq << "\n"
    << "  Actual:   " << e.cmdSeq << "\n";
  ASSERT_EQ(response, e.response)
    << "\n"
    << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
    << "  Value:    Command response at index "
    << __index
    << " in command response history\n"
    << "  Expected: " << response << "\n"
    << "  Actual:   " << e.response << "\n";
}
