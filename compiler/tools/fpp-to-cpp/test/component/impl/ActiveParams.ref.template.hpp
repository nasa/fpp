// ======================================================================
// \title  ActiveParams.template.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for ActiveParams component implementation class
// ======================================================================

#ifndef ActiveParams_HPP
#define ActiveParams_HPP

#include "ActiveParamsComponentAc.hpp"

class ActiveParams :
  public ActiveParamsComponentBase
{

  public:

    // ----------------------------------------------------------------------
    // Component construction, initialization, and destruction
    // ----------------------------------------------------------------------

    //! Construct ActiveParams object
    ActiveParams(
        const char* const compName //!< The component name
    );

    //! Initialize ActiveParams object
    void init(
        NATIVE_INT_TYPE queueDepth, //!< The queue depth
        NATIVE_INT_TYPE instance = 0 //!< The instance number
    );

    //! Destroy ActiveParams object
    ~ActiveParams();

  PRIVATE:

    // ----------------------------------------------------------------------
    // Handler implementations for user-defined typed input ports
    // ----------------------------------------------------------------------

    //! Handler implementation for noArgsAsync
    void noArgsAsync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsGuarded
    void noArgsGuarded_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsReturnGuarded
    U32 noArgsReturnGuarded_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsReturnSync
    U32 noArgsReturnSync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for noArgsSync
    void noArgsSync_handler(
        NATIVE_INT_TYPE portNum //!< The port number
    );

    //! Handler implementation for typedAsync
    void typedAsync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedAsyncAssert
    void typedAsyncAssert_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedAsyncBlockPriority
    void typedAsyncBlockPriority_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedAsyncDropPriority
    void typedAsyncDropPriority_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedGuarded
    void typedGuarded_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedReturnGuarded
    F32 typedReturnGuarded_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedReturnPortStrings::StringSize80& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedReturnSync
    F32 typedReturnSync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedReturnPortStrings::StringSize80& str2, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

    //! Handler implementation for typedSync
    void typedSync_handler(
        NATIVE_INT_TYPE portNum, //!< The port number
        U32 u32, //!< A U32
        F32 f32, //!< An F32
        bool b, //!< A boolean
        const TypedPortStrings::StringSize80& str1, //!< A string
        const E& e, //!< An enum
        const A& a, //!< An array
        const S& s //!< A struct
    );

};

#endif
