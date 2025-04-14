// ======================================================================
// \title  PassiveParams.hpp
// \author [user name]
// \brief  hpp file for PassiveParams component implementation class
// ======================================================================

#ifndef PassiveParams_HPP
#define PassiveParams_HPP

#include "PassiveParamsComponentAc.hpp"

class PassiveParams final :
  public PassiveParamsComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction test and destruction
    // ----------------------------------------------------------------------

    //! Construct PassiveParams object
    PassiveParams(
        const char* const compName //!< The component name
    );

    //! Destroy PassiveParams object
    ~PassiveParams();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for noArgsAliasStringReturnSync
    //!
    //! A typed sync input port with a string return type
    Fw::String noArgsAliasStringReturnSync_handler(
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
    Fw::String typedAliasStringReturnSync_handler(
        FwIndexType portNum, //!< The port number
        AliasPrim1 u32, //!< A primitive
        AliasPrim2 f32, //!< Another primtive
        AliasBool b, //!< A boolean
        const Fw::StringBase& str2, //!< A string
        const AliasEnum& e, //!< An enum
        const AliasArray& a, //!< An array
        const AnotherAliasStruct& s //!< A struct
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
