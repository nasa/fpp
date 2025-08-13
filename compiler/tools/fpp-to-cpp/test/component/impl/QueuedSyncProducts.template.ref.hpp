// ======================================================================
// \title  QueuedSyncProducts.hpp
// \author [user name]
// \brief  hpp file for QueuedSyncProducts component implementation class
// ======================================================================

#ifndef QueuedSyncProducts_HPP
#define QueuedSyncProducts_HPP

#include "QueuedSyncProductsComponentAc.hpp"

class QueuedSyncProducts final :
  public QueuedSyncProductsComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction and destruction
    // ----------------------------------------------------------------------

    //! Construct QueuedSyncProducts object
    QueuedSyncProducts(
        const char* const compName //!< The component name
    );

    //! Destroy QueuedSyncProducts object
    ~QueuedSyncProducts();

  private:

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
    Fw::String noArgsAliasStringReturnSync_handler(
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

  private:

    // ----------------------------------------------------------------------
    // Handler implementations for data products
    // ----------------------------------------------------------------------

    //! Receive a container of type Container1
    void dpRecv_Container1_handler(
        DpContainer& container, //!< The container
        Fw::Success::T status //!< The container status
    ) override;

    //! Receive a container of type Container2
    void dpRecv_Container2_handler(
        DpContainer& container, //!< The container
        Fw::Success::T status //!< The container status
    ) override;

    //! Receive a container of type Container3
    void dpRecv_Container3_handler(
        DpContainer& container, //!< The container
        Fw::Success::T status //!< The container status
    ) override;

    //! Receive a container of type Container4
    void dpRecv_Container4_handler(
        DpContainer& container, //!< The container
        Fw::Success::T status //!< The container status
    ) override;

    //! Receive a container of type Container5
    void dpRecv_Container5_handler(
        DpContainer& container, //!< The container
        Fw::Success::T status //!< The container status
    ) override;

};

#endif
