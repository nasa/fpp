// ======================================================================
// \title  ActiveTelemetry.hpp
// \author [user name]
// \brief  hpp file for ActiveTelemetry component implementation class
// ======================================================================

#ifndef ActiveTelemetry_HPP
#define ActiveTelemetry_HPP

#include "ActiveTelemetryComponentAc.hpp"

class ActiveTelemetry final :
  public ActiveTelemetryComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction test and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveTelemetry object
    ActiveTelemetry(
        const char* const compName //!< The component name
    );

    //! Destroy ActiveTelemetry object
    ~ActiveTelemetry();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for aliasTypedAsync
    //!
    //! An alias typed async input port
    void aliasTypedAsync_handler(
        FwIndexType portNum, //!< The port number
        AliasPrim1 u32, //!< A primitive
        AliasPrim2 f32, //!< Another primtive
        AliasBool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const AliasEnum& e, //!< An enum
        const AliasArray& a, //!< An array
        const AliasStruct& s //!< A struct
    ) override;

    //! Handler implementation for noArgsAliasStringReturnSync
    //!
    //! A typed sync input port with a string return type
    AliasString noArgsAliasStringReturnSync_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsAsync
    //!
    //! A typed async input port
    void noArgsAsync_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsGuarded
    //!
    //! A typed guarded input
    void noArgsGuarded_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsReturnGuarded
    //!
    //! A typed guarded input
    U32 noArgsReturnGuarded_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsReturnSync
    //!
    //! A typed sync input port
    U32 noArgsReturnSync_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsStringReturnSync
    //!
    //! A typed sync input port with a string return type
    Fw::String noArgsStringReturnSync_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for noArgsSync
    //!
    //! A typed sync input port
    void noArgsSync_handler(
        FwIndexType portNum //!< The port number
    ) override;

    //! Handler implementation for typedAliasGuarded
    //!
    //! A typed guarded input
    void typedAliasGuarded_handler(
        FwIndexType portNum, //!< The port number
        AliasPrim1 u32, //!< A primitive
        AliasPrim2 f32, //!< Another primtive
        AliasBool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const AliasEnum& e, //!< An enum
        const AliasArray& a, //!< An array
        const AliasStruct& s //!< A struct
    ) override;

    //! Handler implementation for typedAliasReturnSync
    //!
    //! An alias typed sync input port with a return type
    AliasPrim2 typedAliasReturnSync_handler(
        FwIndexType portNum, //!< The port number
        AliasPrim1 u32, //!< A primitive
        AliasPrim2 f32, //!< Another primtive
        AliasBool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const AliasEnum& e, //!< An enum
        const AliasArray& a, //!< An array
        const AliasStruct& s //!< A struct
    ) override;

    //! Handler implementation for typedAliasStringReturnSync
    //!
    //! A typed sync input port with a return type
    AliasString typedAliasStringReturnSync_handler(
        FwIndexType portNum, //!< The port number
        AliasPrim1 u32, //!< A primitive
        AliasPrim2 f32, //!< Another primtive
        AliasBool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const AliasEnum& e, //!< An enum
        const AliasArray& a, //!< An array
        const AnotherAliasStruct& s //!< A struct
    ) override;

    //! Handler implementation for typedAsync
    //!
    //! A typed async input port
    void typedAsync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedAsyncAssert
    //!
    //! A typed async input port with queue full behavior and priority
    void typedAsyncAssert_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedAsyncBlockPriority
    //!
    //! A typed async input port with queue full behavior and priority
    void typedAsyncBlockPriority_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedAsyncDropPriority
    //!
    //! A typed async input port with queue full behavior and priority
    void typedAsyncDropPriority_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedGuarded
    //!
    //! A typed guarded input
    void typedGuarded_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedReturnGuarded
    //!
    //! A typed guarded input with a return type
    F32 typedReturnGuarded_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedReturnSync
    //!
    //! A typed sync input port with a return type
    F32 typedReturnSync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

    //! Handler implementation for typedSync
    //!
    //! A typed sync input port
    void typedSync_handler(
        FwIndexType portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const Fw::StringBase& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    ) override;

};

#endif
