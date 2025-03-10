// ======================================================================
// \title  PassiveTelemetryTesterBase.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for PassiveTelemetry component test harness base class
// ======================================================================

#ifndef PassiveTelemetryTesterBase_HPP
#define PassiveTelemetryTesterBase_HPP

#include <cstdio>

#include "Fw/Comp/PassiveComponentBase.hpp"
#include "Fw/Port/InputSerializePort.hpp"
#include "Fw/Types/Assert.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "test-base/PassiveTelemetryComponentAc.hpp"

//! \class PassiveTelemetryTesterBase
//! \brief Auto-generated base for PassiveTelemetry component test harness
class PassiveTelemetryTesterBase :
  public Fw::PassiveComponentBase
{

  protected:

    // ----------------------------------------------------------------------
    // History class
    // ----------------------------------------------------------------------

    //! \class History
    //! \brief A history of port inputs
    //!
    template<typename T>
    class History
    {

      public:

        //! Create a History
        History(
           const U32 maxSize //!< The maximum history size
        ) :
          m_numEntries(0),
          m_maxSize(maxSize)
        {
          this->m_entries = new T[maxSize];
        }

        //! Destroy a History
        ~History()
        {
          delete[] this->m_entries;
        }

        //! Clear the history
        //!
        void clear()
        {
          this->m_numEntries = 0;
        }

        //! Push an item onto the history
        //!
        void push_back(
            const T& entry //!< The item
        )
        {
          FW_ASSERT(this->m_numEntries < this->m_maxSize);
          this->m_entries[this->m_numEntries++] = entry;
        }

        //! Get an item at an index
        //!
        //! \return The item at index i
        const T& at(
            const U32 i //!< The index
        ) const
        {
          FW_ASSERT(i < this->m_numEntries);
          return this->m_entries[i];
        }

        //! Get the number of entries in the history
        //!
        //! \return The number of entries in the history
        U32 size() const
        {
          return this->m_numEntries;
        }

      private:

        //! The number of entries in the history
        U32 m_numEntries;

        //! The maximum history size
        const U32 m_maxSize;

        //! The entries
        T* m_entries;

    };

  protected:

    // ----------------------------------------------------------------------
    // History types
    // ----------------------------------------------------------------------

    //! A history entry for port from_typedOut
    struct FromPortEntry_typedOut {
      FromPortEntry_typedOut() :
        u32(),
        f32(),
        b(),
        str1(__fprime_ac_str1_buffer, sizeof __fprime_ac_str1_buffer),
        e(),
        a(),
        s()
      {}
      U32 u32;
      F32 f32;
      bool b;
      char __fprime_ac_str1_buffer[Fw::StringBase::BUFFER_SIZE(80)];
      Fw::ExternalString str1;
      E e;
      A a;
      S s;
    };

    //! A history entry for port from_typedReturnOut
    struct FromPortEntry_typedReturnOut {
      FromPortEntry_typedReturnOut() :
        u32(),
        f32(),
        b(),
        str2(__fprime_ac_str2_buffer, sizeof __fprime_ac_str2_buffer),
        e(),
        a(),
        s()
      {}
      U32 u32;
      F32 f32;
      bool b;
      char __fprime_ac_str2_buffer[Fw::StringBase::BUFFER_SIZE(80)];
      Fw::ExternalString str2;
      E e;
      A a;
      S s;
    };

    //! A history entry for telemetry channel ChannelU32Format
    struct TlmEntry_ChannelU32Format {
      Fw::Time timeTag;
      U32 arg;
    };

    //! A history entry for telemetry channel ChannelF32Format
    struct TlmEntry_ChannelF32Format {
      Fw::Time timeTag;
      F32 arg;
    };

    //! A history entry for telemetry channel ChannelStringFormat
    struct TlmEntry_ChannelStringFormat {
      Fw::Time timeTag;
      Fw::TlmString arg;
    };

    //! A history entry for telemetry channel ChannelEnum
    struct TlmEntry_ChannelEnum {
      Fw::Time timeTag;
      E arg;
    };

    //! A history entry for telemetry channel ChannelArrayFreq
    struct TlmEntry_ChannelArrayFreq {
      Fw::Time timeTag;
      A arg;
    };

    //! A history entry for telemetry channel ChannelStructFreq
    struct TlmEntry_ChannelStructFreq {
      Fw::Time timeTag;
      S arg;
    };

    //! A history entry for telemetry channel ChannelU32Limits
    struct TlmEntry_ChannelU32Limits {
      Fw::Time timeTag;
      U32 arg;
    };

    //! A history entry for telemetry channel ChannelF32Limits
    struct TlmEntry_ChannelF32Limits {
      Fw::Time timeTag;
      F32 arg;
    };

    //! A history entry for telemetry channel ChannelF64
    struct TlmEntry_ChannelF64 {
      Fw::Time timeTag;
      F64 arg;
    };

    //! A history entry for telemetry channel ChannelU32OnChange
    struct TlmEntry_ChannelU32OnChange {
      Fw::Time timeTag;
      U32 arg;
    };

    //! A history entry for telemetry channel ChannelEnumOnChange
    struct TlmEntry_ChannelEnumOnChange {
      Fw::Time timeTag;
      E arg;
    };

  public:

    // ----------------------------------------------------------------------
    // Component initialization
    // ----------------------------------------------------------------------

    //! Initialize object PassiveTelemetryTesterBase
    virtual void init(
        FwEnumStoreType instance = 0 //!< The instance number
    );

  public:

    // ----------------------------------------------------------------------
    // Connectors for to ports
    // ----------------------------------------------------------------------

    //! Connect port to cmdIn[portNum]
    void connect_to_cmdIn(
        FwIndexType portNum, //!< The port number
        Fw::InputCmdPort* port //!< The input port
    );

    //! Connect port to noArgsGuarded[portNum]
    void connect_to_noArgsGuarded(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsPort* port //!< The input port
    );

    //! Connect port to noArgsReturnGuarded[portNum]
    void connect_to_noArgsReturnGuarded(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsReturnPort* port //!< The input port
    );

    //! Connect port to noArgsReturnSync[portNum]
    void connect_to_noArgsReturnSync(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsReturnPort* port //!< The input port
    );

    //! Connect port to noArgsStringReturnSync[portNum]
    void connect_to_noArgsStringReturnSync(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsStringReturnPort* port //!< The input port
    );

    //! Connect port to noArgsSync[portNum]
    void connect_to_noArgsSync(
        FwIndexType portNum, //!< The port number
        Ports::InputNoArgsPort* port //!< The input port
    );

    //! Connect port to typedGuarded[portNum]
    void connect_to_typedGuarded(
        FwIndexType portNum, //!< The port number
        Ports::InputTypedPort* port //!< The input port
    );

    //! Connect port to typedReturnGuarded[portNum]
    void connect_to_typedReturnGuarded(
        FwIndexType portNum, //!< The port number
        Ports::InputTypedReturnPort* port //!< The input port
    );

    //! Connect port to typedReturnSync[portNum]
    void connect_to_typedReturnSync(
        FwIndexType portNum, //!< The port number
        Ports::InputTypedReturnPort* port //!< The input port
    );

    //! Connect port to typedSync[portNum]
    void connect_to_typedSync(
        FwIndexType portNum, //!< The port number
        Ports::InputTypedPort* port //!< The input port
    );

  public:

    // ----------------------------------------------------------------------
    // Getters for from ports
    // ----------------------------------------------------------------------

    //! Get from port at index
    //!
    //! \return from_cmdRegOut[portNum]
    Fw::InputCmdRegPort* get_from_cmdRegOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_cmdResponseOut[portNum]
    Fw::InputCmdResponsePort* get_from_cmdResponseOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_eventOut[portNum]
    Fw::InputLogPort* get_from_eventOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_prmGetOut[portNum]
    Fw::InputPrmGetPort* get_from_prmGetOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_prmSetOut[portNum]
    Fw::InputPrmSetPort* get_from_prmSetOut(
        FwIndexType portNum //!< The port number
    );

#if FW_ENABLE_TEXT_LOGGING == 1

    //! Get from port at index
    //!
    //! \return from_textEventOut[portNum]
    Fw::InputLogTextPort* get_from_textEventOut(
        FwIndexType portNum //!< The port number
    );

#endif

    //! Get from port at index
    //!
    //! \return from_timeGetOut[portNum]
    Fw::InputTimePort* get_from_timeGetOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_tlmOut[portNum]
    Fw::InputTlmPort* get_from_tlmOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_noArgsOut[portNum]
    Ports::InputNoArgsPort* get_from_noArgsOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_noArgsReturnOut[portNum]
    Ports::InputNoArgsReturnPort* get_from_noArgsReturnOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_noArgsStringReturnOut[portNum]
    Ports::InputNoArgsStringReturnPort* get_from_noArgsStringReturnOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_typedOut[portNum]
    Ports::InputTypedPort* get_from_typedOut(
        FwIndexType portNum //!< The port number
    );

    //! Get from port at index
    //!
    //! \return from_typedReturnOut[portNum]
    Ports::InputTypedReturnPort* get_from_typedReturnOut(
        FwIndexType portNum //!< The port number
    );

  protected:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct object PassiveTelemetryTesterBase
    PassiveTelemetryTesterBase(
        const char* const compName, //!< The component name
        const U32 maxHistorySize //!< The maximum size of each history
    );

    //! Destroy object PassiveTelemetryTesterBase
    virtual ~PassiveTelemetryTesterBase();

  protected:

    // ----------------------------------------------------------------------
    // Default handler implementations for typed from ports
    // You can override these implementation with more specific behavior
    // ----------------------------------------------------------------------

    //! Default handler implementation for from_noArgsOut
    virtual void from_noArgsOut_handler(
        FwIndexType portNum //!< The port number
    );

    //! Default handler implementation for from_noArgsReturnOut
    virtual U32 from_noArgsReturnOut_handler(
        FwIndexType portNum //!< The port number
    );

    //! Default handler implementation for from_noArgsStringReturnOut
    virtual Fw::String from_noArgsStringReturnOut_handler(
        FwIndexType portNum //!< The port number
    );

    //! Default handler implementation for from_typedOut
    virtual void from_typedOut_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Default handler implementation for from_typedReturnOut
    virtual F32 from_typedReturnOut_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

  protected:

    // ----------------------------------------------------------------------
    // Handler base-class functions for from ports
    // ----------------------------------------------------------------------

    //! Handler base-class function for from_noArgsOut
    void from_noArgsOut_handlerBase(
        FwIndexType portNum //!< The port number
    );

    //! Handler base-class function for from_noArgsReturnOut
    U32 from_noArgsReturnOut_handlerBase(
        FwIndexType portNum //!< The port number
    );

    //! Handler base-class function for from_noArgsStringReturnOut
    Fw::String from_noArgsStringReturnOut_handlerBase(
        FwIndexType portNum //!< The port number
    );

    //! Handler base-class function for from_typedOut
    void from_typedOut_handlerBase(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler base-class function for from_typedReturnOut
    F32 from_typedReturnOut_handlerBase(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

  protected:

    // ----------------------------------------------------------------------
    // Invocation functions for to ports
    // ----------------------------------------------------------------------

    //! Invoke the to port connected to noArgsGuarded
    void invoke_to_noArgsGuarded(
        FwIndexType portNum //!< The port number
    );

    //! Invoke the to port connected to noArgsReturnGuarded
    U32 invoke_to_noArgsReturnGuarded(
        FwIndexType portNum //!< The port number
    );

    //! Invoke the to port connected to noArgsReturnSync
    U32 invoke_to_noArgsReturnSync(
        FwIndexType portNum //!< The port number
    );

    //! Invoke the to port connected to noArgsStringReturnSync
    Fw::String invoke_to_noArgsStringReturnSync(
        FwIndexType portNum //!< The port number
    );

    //! Invoke the to port connected to noArgsSync
    void invoke_to_noArgsSync(
        FwIndexType portNum //!< The port number
    );

    //! Invoke the to port connected to typedGuarded
    void invoke_to_typedGuarded(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Invoke the to port connected to typedReturnGuarded
    F32 invoke_to_typedReturnGuarded(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Invoke the to port connected to typedReturnSync
    F32 invoke_to_typedReturnSync(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Invoke the to port connected to typedSync
    void invoke_to_typedSync(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

  protected:

    // ----------------------------------------------------------------------
    // Getters for port counts
    // ----------------------------------------------------------------------

    //! Get the number of to_cmdIn ports
    //!
    //! \return The number of to_cmdIn ports
    FwIndexType getNum_to_cmdIn() const;

    //! Get the number of to_noArgsGuarded ports
    //!
    //! \return The number of to_noArgsGuarded ports
    FwIndexType getNum_to_noArgsGuarded() const;

    //! Get the number of to_noArgsReturnGuarded ports
    //!
    //! \return The number of to_noArgsReturnGuarded ports
    FwIndexType getNum_to_noArgsReturnGuarded() const;

    //! Get the number of to_noArgsReturnSync ports
    //!
    //! \return The number of to_noArgsReturnSync ports
    FwIndexType getNum_to_noArgsReturnSync() const;

    //! Get the number of to_noArgsStringReturnSync ports
    //!
    //! \return The number of to_noArgsStringReturnSync ports
    FwIndexType getNum_to_noArgsStringReturnSync() const;

    //! Get the number of to_noArgsSync ports
    //!
    //! \return The number of to_noArgsSync ports
    FwIndexType getNum_to_noArgsSync() const;

    //! Get the number of to_typedGuarded ports
    //!
    //! \return The number of to_typedGuarded ports
    FwIndexType getNum_to_typedGuarded() const;

    //! Get the number of to_typedReturnGuarded ports
    //!
    //! \return The number of to_typedReturnGuarded ports
    FwIndexType getNum_to_typedReturnGuarded() const;

    //! Get the number of to_typedReturnSync ports
    //!
    //! \return The number of to_typedReturnSync ports
    FwIndexType getNum_to_typedReturnSync() const;

    //! Get the number of to_typedSync ports
    //!
    //! \return The number of to_typedSync ports
    FwIndexType getNum_to_typedSync() const;

    //! Get the number of from_cmdRegOut ports
    //!
    //! \return The number of from_cmdRegOut ports
    FwIndexType getNum_from_cmdRegOut() const;

    //! Get the number of from_cmdResponseOut ports
    //!
    //! \return The number of from_cmdResponseOut ports
    FwIndexType getNum_from_cmdResponseOut() const;

    //! Get the number of from_eventOut ports
    //!
    //! \return The number of from_eventOut ports
    FwIndexType getNum_from_eventOut() const;

    //! Get the number of from_prmGetOut ports
    //!
    //! \return The number of from_prmGetOut ports
    FwIndexType getNum_from_prmGetOut() const;

    //! Get the number of from_prmSetOut ports
    //!
    //! \return The number of from_prmSetOut ports
    FwIndexType getNum_from_prmSetOut() const;

#if FW_ENABLE_TEXT_LOGGING == 1

    //! Get the number of from_textEventOut ports
    //!
    //! \return The number of from_textEventOut ports
    FwIndexType getNum_from_textEventOut() const;

#endif

    //! Get the number of from_timeGetOut ports
    //!
    //! \return The number of from_timeGetOut ports
    FwIndexType getNum_from_timeGetOut() const;

    //! Get the number of from_tlmOut ports
    //!
    //! \return The number of from_tlmOut ports
    FwIndexType getNum_from_tlmOut() const;

    //! Get the number of from_noArgsOut ports
    //!
    //! \return The number of from_noArgsOut ports
    FwIndexType getNum_from_noArgsOut() const;

    //! Get the number of from_noArgsReturnOut ports
    //!
    //! \return The number of from_noArgsReturnOut ports
    FwIndexType getNum_from_noArgsReturnOut() const;

    //! Get the number of from_noArgsStringReturnOut ports
    //!
    //! \return The number of from_noArgsStringReturnOut ports
    FwIndexType getNum_from_noArgsStringReturnOut() const;

    //! Get the number of from_typedOut ports
    //!
    //! \return The number of from_typedOut ports
    FwIndexType getNum_from_typedOut() const;

    //! Get the number of from_typedReturnOut ports
    //!
    //! \return The number of from_typedReturnOut ports
    FwIndexType getNum_from_typedReturnOut() const;

  protected:

    // ----------------------------------------------------------------------
    // Connection status queries for to ports
    // ----------------------------------------------------------------------

    //! Check whether port to_cmdIn is connected
    //!
    //! \return Whether port to_cmdIn is connected
    bool isConnected_to_cmdIn(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_noArgsGuarded is connected
    //!
    //! \return Whether port to_noArgsGuarded is connected
    bool isConnected_to_noArgsGuarded(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_noArgsReturnGuarded is connected
    //!
    //! \return Whether port to_noArgsReturnGuarded is connected
    bool isConnected_to_noArgsReturnGuarded(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_noArgsReturnSync is connected
    //!
    //! \return Whether port to_noArgsReturnSync is connected
    bool isConnected_to_noArgsReturnSync(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_noArgsStringReturnSync is connected
    //!
    //! \return Whether port to_noArgsStringReturnSync is connected
    bool isConnected_to_noArgsStringReturnSync(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_noArgsSync is connected
    //!
    //! \return Whether port to_noArgsSync is connected
    bool isConnected_to_noArgsSync(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_typedGuarded is connected
    //!
    //! \return Whether port to_typedGuarded is connected
    bool isConnected_to_typedGuarded(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_typedReturnGuarded is connected
    //!
    //! \return Whether port to_typedReturnGuarded is connected
    bool isConnected_to_typedReturnGuarded(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_typedReturnSync is connected
    //!
    //! \return Whether port to_typedReturnSync is connected
    bool isConnected_to_typedReturnSync(
        FwIndexType portNum //!< The port number
    );

    //! Check whether port to_typedSync is connected
    //!
    //! \return Whether port to_typedSync is connected
    bool isConnected_to_typedSync(
        FwIndexType portNum //!< The port number
    );

  protected:

    // ----------------------------------------------------------------------
    // Functions for testing telemetry
    // ----------------------------------------------------------------------

    //! Dispatch telemetry
    void dispatchTlm(
        FwChanIdType id, //!< The channel id
        const Fw::Time& timeTag, //!< The time
        Fw::TlmBuffer& val //!< The channel value
    );

    //! Handle channel ChannelU32Format
    void tlmInput_ChannelU32Format(
        const Fw::Time& timeTag, //!< The time
        const U32 val //!< The channel value
    );

    //! Handle channel ChannelF32Format
    void tlmInput_ChannelF32Format(
        const Fw::Time& timeTag, //!< The time
        const F32 val //!< The channel value
    );

    //! Handle channel ChannelStringFormat
    void tlmInput_ChannelStringFormat(
        const Fw::Time& timeTag, //!< The time
        const Fw::StringBase& val //!< The channel value
    );

    //! Handle channel ChannelEnum
    void tlmInput_ChannelEnum(
        const Fw::Time& timeTag, //!< The time
        const E& val //!< The channel value
    );

    //! Handle channel ChannelArrayFreq
    void tlmInput_ChannelArrayFreq(
        const Fw::Time& timeTag, //!< The time
        const A& val //!< The channel value
    );

    //! Handle channel ChannelStructFreq
    void tlmInput_ChannelStructFreq(
        const Fw::Time& timeTag, //!< The time
        const S& val //!< The channel value
    );

    //! Handle channel ChannelU32Limits
    void tlmInput_ChannelU32Limits(
        const Fw::Time& timeTag, //!< The time
        const U32 val //!< The channel value
    );

    //! Handle channel ChannelF32Limits
    void tlmInput_ChannelF32Limits(
        const Fw::Time& timeTag, //!< The time
        const F32 val //!< The channel value
    );

    //! Handle channel ChannelF64
    void tlmInput_ChannelF64(
        const Fw::Time& timeTag, //!< The time
        const F64 val //!< The channel value
    );

    //! Handle channel ChannelU32OnChange
    void tlmInput_ChannelU32OnChange(
        const Fw::Time& timeTag, //!< The time
        const U32 val //!< The channel value
    );

    //! Handle channel ChannelEnumOnChange
    void tlmInput_ChannelEnumOnChange(
        const Fw::Time& timeTag, //!< The time
        const E& val //!< The channel value
    );

  protected:

    // ----------------------------------------------------------------------
    // Functions to test time
    // ----------------------------------------------------------------------

    //! Set the test time for events and telemetry
    void setTestTime(
        const Fw::Time& timeTag //!< The time
    );

  protected:

    // ----------------------------------------------------------------------
    // History functions
    // ----------------------------------------------------------------------

    //! Clear all history
    void clearHistory();

    //! Clear from port history
    void clearFromPortHistory();

    //! Push an entry on the history for from_noArgsOut
    void pushFromPortEntry_noArgsOut();

    //! Push an entry on the history for from_noArgsReturnOut
    void pushFromPortEntry_noArgsReturnOut();

    //! Push an entry on the history for from_noArgsStringReturnOut
    void pushFromPortEntry_noArgsStringReturnOut();

    //! Push an entry on the history for from_typedOut
    void pushFromPortEntry_typedOut(
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Push an entry on the history for from_typedReturnOut
    void pushFromPortEntry_typedReturnOut(
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Clear telemetry history
    void clearTlm();

  private:

    // ----------------------------------------------------------------------
    // Static functions for output ports
    // ----------------------------------------------------------------------

    //! Static function for port from_timeGetOut
    static void from_timeGetOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum, //!< The port number
        Fw::Time& time //!< The time tag
    );

    //! Static function for port from_tlmOut
    static void from_tlmOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum, //!< The port number
        FwChanIdType id, //!< Telemetry Channel ID
        Fw::Time& timeTag, //!< Time Tag
        Fw::TlmBuffer& val //!< Buffer containing serialized telemetry value
    );

    //! Static function for port from_noArgsOut
    static void from_noArgsOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

    //! Static function for port from_noArgsReturnOut
    static U32 from_noArgsReturnOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

    //! Static function for port from_noArgsStringReturnOut
    static Fw::String from_noArgsStringReturnOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum //!< The port number
    );

    //! Static function for port from_typedOut
    static void from_typedOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Static function for port from_typedReturnOut
    static F32 from_typedReturnOut_static(
        Fw::PassiveComponentBase* const callComp, //!< The component instance
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

  protected:

    // ----------------------------------------------------------------------
    // History member variables
    // ----------------------------------------------------------------------

    //! The total number of port entries
    U32 fromPortHistorySize;

    //! The size of history for from_noArgsOut
    U32 fromPortHistorySize_noArgsOut;

    //! The size of history for from_noArgsReturnOut
    U32 fromPortHistorySize_noArgsReturnOut;

    //! The size of history for from_noArgsStringReturnOut
    U32 fromPortHistorySize_noArgsStringReturnOut;

    //! The history for from_typedOut
    History<FromPortEntry_typedOut>* fromPortHistory_typedOut;

    //! The history for from_typedReturnOut
    History<FromPortEntry_typedReturnOut>* fromPortHistory_typedReturnOut;

    //! The total number of telemetry inputs seen
    U32 tlmSize;

    //! The history of ChannelU32Format values
    History<TlmEntry_ChannelU32Format>* tlmHistory_ChannelU32Format;

    //! The history of ChannelF32Format values
    History<TlmEntry_ChannelF32Format>* tlmHistory_ChannelF32Format;

    //! The history of ChannelStringFormat values
    History<TlmEntry_ChannelStringFormat>* tlmHistory_ChannelStringFormat;

    //! The history of ChannelEnum values
    History<TlmEntry_ChannelEnum>* tlmHistory_ChannelEnum;

    //! The history of ChannelArrayFreq values
    History<TlmEntry_ChannelArrayFreq>* tlmHistory_ChannelArrayFreq;

    //! The history of ChannelStructFreq values
    History<TlmEntry_ChannelStructFreq>* tlmHistory_ChannelStructFreq;

    //! The history of ChannelU32Limits values
    History<TlmEntry_ChannelU32Limits>* tlmHistory_ChannelU32Limits;

    //! The history of ChannelF32Limits values
    History<TlmEntry_ChannelF32Limits>* tlmHistory_ChannelF32Limits;

    //! The history of ChannelF64 values
    History<TlmEntry_ChannelF64>* tlmHistory_ChannelF64;

    //! The history of ChannelU32OnChange values
    History<TlmEntry_ChannelU32OnChange>* tlmHistory_ChannelU32OnChange;

    //! The history of ChannelEnumOnChange values
    History<TlmEntry_ChannelEnumOnChange>* tlmHistory_ChannelEnumOnChange;

  private:

    // ----------------------------------------------------------------------
    // To ports
    // ----------------------------------------------------------------------

    //! To port connected to cmdIn
    Fw::OutputCmdPort m_to_cmdIn[1];

    //! To port connected to noArgsGuarded
    Ports::OutputNoArgsPort m_to_noArgsGuarded[1];

    //! To port connected to noArgsReturnGuarded
    Ports::OutputNoArgsReturnPort m_to_noArgsReturnGuarded[1];

    //! To port connected to noArgsReturnSync
    Ports::OutputNoArgsReturnPort m_to_noArgsReturnSync[3];

    //! To port connected to noArgsStringReturnSync
    Ports::OutputNoArgsStringReturnPort m_to_noArgsStringReturnSync[1];

    //! To port connected to noArgsSync
    Ports::OutputNoArgsPort m_to_noArgsSync[3];

    //! To port connected to typedGuarded
    Ports::OutputTypedPort m_to_typedGuarded[1];

    //! To port connected to typedReturnGuarded
    Ports::OutputTypedReturnPort m_to_typedReturnGuarded[1];

    //! To port connected to typedReturnSync
    Ports::OutputTypedReturnPort m_to_typedReturnSync[3];

    //! To port connected to typedSync
    Ports::OutputTypedPort m_to_typedSync[3];

  private:

    // ----------------------------------------------------------------------
    // From ports
    // ----------------------------------------------------------------------

    //! From port connected to cmdRegOut
    Fw::InputCmdRegPort m_from_cmdRegOut[1];

    //! From port connected to cmdResponseOut
    Fw::InputCmdResponsePort m_from_cmdResponseOut[1];

    //! From port connected to eventOut
    Fw::InputLogPort m_from_eventOut[1];

    //! From port connected to prmGetOut
    Fw::InputPrmGetPort m_from_prmGetOut[1];

    //! From port connected to prmSetOut
    Fw::InputPrmSetPort m_from_prmSetOut[1];

#if FW_ENABLE_TEXT_LOGGING == 1

    //! From port connected to textEventOut
    Fw::InputLogTextPort m_from_textEventOut[1];

#endif

    //! From port connected to timeGetOut
    Fw::InputTimePort m_from_timeGetOut[1];

    //! From port connected to tlmOut
    Fw::InputTlmPort m_from_tlmOut[1];

    //! From port connected to noArgsOut
    Ports::InputNoArgsPort m_from_noArgsOut[1];

    //! From port connected to noArgsReturnOut
    Ports::InputNoArgsReturnPort m_from_noArgsReturnOut[1];

    //! From port connected to noArgsStringReturnOut
    Ports::InputNoArgsStringReturnPort m_from_noArgsStringReturnOut[1];

    //! From port connected to typedOut
    Ports::InputTypedPort m_from_typedOut[1];

    //! From port connected to typedReturnOut
    Ports::InputTypedReturnPort m_from_typedReturnOut[1];

  private:

    // ----------------------------------------------------------------------
    // Time variables
    // ----------------------------------------------------------------------

    //! Test time stamp
    Fw::Time m_testTime;

};

#endif
