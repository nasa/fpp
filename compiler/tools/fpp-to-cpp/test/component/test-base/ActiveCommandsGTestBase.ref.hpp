// ======================================================================
// \title  ActiveCommandsGTestBase.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for ActiveCommands component Google Test harness base class
// ======================================================================

#ifndef ActiveCommandsGTestBase_HPP
#define ActiveCommandsGTestBase_HPP

#include "gtest/gtest.h"
#include "test-base/ActiveCommandsTesterBase.hpp"

// ----------------------------------------------------------------------
// Macros for typed user from port history assertions
// ----------------------------------------------------------------------

#define ASSERT_FROM_PORT_HISTORY_SIZE(size) \
  this->assertFromPortHistory_size(__FILE__, __LINE__, size)

#define ASSERT_from_noArgsOut_SIZE(size) \
  this->assert_from_noArgsOut_size(__FILE__, __LINE__, size)

#define ASSERT_from_noArgsOut(index) \
  { \
    ASSERT_GT(this->fromPortHistory_noArgsOut->size(), static_cast<U32>(index)) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Index into history of noArgsOut\n" \
      << "  Expected: Less than size of history (" \
      << this->fromPortHistory_noArgsOut->size() << ")\n" \
      << "  Actual:   " << index << "\n"; \
      const FromPortEntry_noArgsOut& _e = \
        this->fromPortHistory_noArgsOut->at(index); \
  }

#define ASSERT_from_noArgsReturnOut_SIZE(size) \
  this->assert_from_noArgsReturnOut_size(__FILE__, __LINE__, size)

#define ASSERT_from_noArgsReturnOut(index) \
  { \
    ASSERT_GT(this->fromPortHistory_noArgsReturnOut->size(), static_cast<U32>(index)) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Index into history of noArgsReturnOut\n" \
      << "  Expected: Less than size of history (" \
      << this->fromPortHistory_noArgsReturnOut->size() << ")\n" \
      << "  Actual:   " << index << "\n"; \
      const FromPortEntry_noArgsReturnOut& _e = \
        this->fromPortHistory_noArgsReturnOut->at(index); \
  }

#define ASSERT_from_noArgsStringReturnOut_SIZE(size) \
  this->assert_from_noArgsStringReturnOut_size(__FILE__, __LINE__, size)

#define ASSERT_from_noArgsStringReturnOut(index) \
  { \
    ASSERT_GT(this->fromPortHistory_noArgsStringReturnOut->size(), static_cast<U32>(index)) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Index into history of noArgsStringReturnOut\n" \
      << "  Expected: Less than size of history (" \
      << this->fromPortHistory_noArgsStringReturnOut->size() << ")\n" \
      << "  Actual:   " << index << "\n"; \
      const FromPortEntry_noArgsStringReturnOut& _e = \
        this->fromPortHistory_noArgsStringReturnOut->at(index); \
  }

#define ASSERT_from_typedOut_SIZE(size) \
  this->assert_from_typedOut_size(__FILE__, __LINE__, size)

#define ASSERT_from_typedOut(index, _u32, _f32, _b, _str1, _e, _a, _s) \
  { \
    ASSERT_GT(this->fromPortHistory_typedOut->size(), static_cast<U32>(index)) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Index into history of typedOut\n" \
      << "  Expected: Less than size of history (" \
      << this->fromPortHistory_typedOut->size() << ")\n" \
      << "  Actual:   " << index << "\n"; \
      const FromPortEntry_typedOut& _e = \
        this->fromPortHistory_typedOut->at(index); \
    ASSERT_EQ(_u32, _e.u32) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument u32 at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _u32 << "\n" \
      << "  Actual:   " << _e.u32 << "\n"; \
    ASSERT_EQ(_f32, _e.f32) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument f32 at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _f32 << "\n" \
      << "  Actual:   " << _e.f32 << "\n"; \
    ASSERT_EQ(_b, _e.b) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument b at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _b << "\n" \
      << "  Actual:   " << _e.b << "\n"; \
    ASSERT_EQ(_str1, _e.str1) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument str1 at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _str1 << "\n" \
      << "  Actual:   " << _e.str1 << "\n"; \
    ASSERT_EQ(_e, _e.e) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument e at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _e << "\n" \
      << "  Actual:   " << _e.e << "\n"; \
    ASSERT_EQ(_a, _e.a) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument a at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _a << "\n" \
      << "  Actual:   " << _e.a << "\n"; \
    ASSERT_EQ(_s, _e.s) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument s at index " \
      << index \
      << " in history of typedOut\n" \
      << "  Expected: " << _s << "\n" \
      << "  Actual:   " << _e.s << "\n"; \
  }

#define ASSERT_from_typedReturnOut_SIZE(size) \
  this->assert_from_typedReturnOut_size(__FILE__, __LINE__, size)

#define ASSERT_from_typedReturnOut(index, _u32, _f32, _b, _str2, _e, _a, _s) \
  { \
    ASSERT_GT(this->fromPortHistory_typedReturnOut->size(), static_cast<U32>(index)) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Index into history of typedReturnOut\n" \
      << "  Expected: Less than size of history (" \
      << this->fromPortHistory_typedReturnOut->size() << ")\n" \
      << "  Actual:   " << index << "\n"; \
      const FromPortEntry_typedReturnOut& _e = \
        this->fromPortHistory_typedReturnOut->at(index); \
    ASSERT_EQ(_u32, _e.u32) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument u32 at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _u32 << "\n" \
      << "  Actual:   " << _e.u32 << "\n"; \
    ASSERT_EQ(_f32, _e.f32) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument f32 at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _f32 << "\n" \
      << "  Actual:   " << _e.f32 << "\n"; \
    ASSERT_EQ(_b, _e.b) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument b at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _b << "\n" \
      << "  Actual:   " << _e.b << "\n"; \
    ASSERT_EQ(_str2, _e.str2) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument str2 at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _str2 << "\n" \
      << "  Actual:   " << _e.str2 << "\n"; \
    ASSERT_EQ(_e, _e.e) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument e at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _e << "\n" \
      << "  Actual:   " << _e.e << "\n"; \
    ASSERT_EQ(_a, _e.a) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument a at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _a << "\n" \
      << "  Actual:   " << _e.a << "\n"; \
    ASSERT_EQ(_s, _e.s) \
      << "\n" \
      << __FILE__ << ":" << __LINE__ << "\n" \
      << "  Value:    Value of argument s at index " \
      << index \
      << " in history of typedReturnOut\n" \
      << "  Expected: " << _s << "\n" \
      << "  Actual:   " << _e.s << "\n"; \
  }

// ----------------------------------------------------------------------
// Macros for command history assertions
// ----------------------------------------------------------------------

#define ASSERT_CMD_RESPONSE_SIZE(size) \
  this->assertCmdResponse_size(__FILE__, __LINE__, size)

#define ASSERT_CMD_RESPONSE(index, opCode, cmdSeq, response) \
  this->assertCmdResponse(__FILE__, __LINE__, index, opCode, cmdSeq, response)

//! \class ActiveCommandsGTestBase
//! \brief Auto-generated base for ActiveCommands component Google Test harness
class ActiveCommandsGTestBase :
  public ActiveCommandsTesterBase
{

  protected:

    // ----------------------------------------------------------------------
    // Construction and destruction
    // ----------------------------------------------------------------------

    //! Construct object ActiveCommandsGTestBase
    ActiveCommandsGTestBase(
        const char* const compName, //!< The component name
        const U32 maxHistorySize //!< The maximum size of each history
    );

    //! Destroy object ActiveCommandsGTestBase
    ~ActiveCommandsGTestBase();

  protected:

    // ----------------------------------------------------------------------
    // From ports
    // ----------------------------------------------------------------------

    //! From ports
    void assertFromPortHistory_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

    //! From port: noArgsOut
    void assert_from_noArgsOut_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

    //! From port: noArgsReturnOut
    void assert_from_noArgsReturnOut_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

    //! From port: noArgsStringReturnOut
    void assert_from_noArgsStringReturnOut_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

    //! From port: typedOut
    void assert_from_typedOut_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

    //! From port: typedReturnOut
    void assert_from_typedReturnOut_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

  protected:

    // ----------------------------------------------------------------------
    // Commands
    // ----------------------------------------------------------------------

    //! Assert size of command response history
    void assertCmdResponse_size(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 size //!< The asserted size
    ) const;

    //! Assert the command response history at index
    void assertCmdResponse(
        const char* const __callSiteFileName, //!< The name of the file containing the call site
        const U32 __callSiteLineNumber, //!< The line number of the call site
        const U32 __index, //!< The index
        FwOpcodeType opCode, //!< The opcode
        U32 cmdSeq, //!< The command sequence number
        Fw::CmdResponse response //!< The command response
    ) const;

};

#endif
