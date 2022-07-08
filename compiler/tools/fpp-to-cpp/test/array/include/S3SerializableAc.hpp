/*
 * S3.hpp
 *
 *  Created on: Thursday, 07 July 2022
 *  Author:     tchieu
 *
 */
#ifndef S3_HPP_
#define S3_HPP_

#include <Fw/Types/BasicTypes.hpp>
#include <Fw/Types/Serializable.hpp>
#if FW_SERIALIZABLE_TO_STRING
#include <Fw/Types/StringType.hpp>
#include <cstdio> // snprintf
#ifdef BUILD_UT
#include <iostream>
#include <Fw/Types/String.hpp>
#endif
#endif

namespace S {

class S3 : public Fw::Serializable {


public:

    enum {
        SERIALIZED_SIZE =
        sizeof(U32)*3 +
        sizeof(F64)
    }; //!< serializable size of S3

    S3(); //!< Default constructor
    S3(const S3* src); //!< pointer copy constructor
    S3(const S3& src); //!< reference copy constructor
    S3(const U32* mU32Array, NATIVE_INT_TYPE mU32ArraySize, F64 mF64); //!< constructor with arguments
    S3(const U32 mU32Array, F64 mF64); //!< constructor with arguments with scalars for array arguments
    S3& operator=(const S3& src); //!< equal operator
    bool operator==(const S3& src) const; //!< equality operator
#ifdef BUILD_UT
    // to support GoogleTest framework in unit tests
    friend std::ostream& operator<<(std::ostream& os, const S3& obj);
#endif

    void set(const U32* mU32Array, NATIVE_INT_TYPE mU32ArraySize, F64 mF64); //!< set values

    const U32* getmU32Array(NATIVE_INT_TYPE& size) const; //!< get member mU32Array
    F64 getmF64() const; //!< get member mF64

    void setmU32Array(const U32* val, NATIVE_INT_TYPE size); //!< set member mU32Array
    void setmF64(F64 val); //!< set member mF64


    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const; //!< serialization function
    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer); //!< deserialization function
#if FW_SERIALIZABLE_TO_STRING || BUILD_UT
    void toString(Fw::StringBase& text) const; //!< generate text from serializable
#endif
protected:

    enum {
        TYPE_ID = 0x11A9151E //!< type id
    };

    U32 m_mU32Array[3]; //<! mU32Array - 

    F64 m_mF64; //<! mF64 - 
private:

};
} // end namespace S
#endif /* S3_HPP_ */

