/*
 * S1.hpp
 *
 *  Created on: Thursday, 07 July 2022
 *  Author:     tchieu
 *
 */
#ifndef S1_HPP_
#define S1_HPP_

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

namespace M {

class S1 : public Fw::Serializable {


  public:
    class mStringString : public Fw::StringBase {
        public:

            enum {
                SERIALIZED_SIZE = 80 + sizeof(FwBuffSizeType) //!<size of buffer + storage of two size words
            };

            mStringString(const char* src); //!< char array constructor
            mStringString(const Fw::StringBase& src); //!< string base constructor
            mStringString(const mStringString& src); //!< string base constructor
            mStringString(); //!< default constructor
            mStringString& operator=(const mStringString& other); //!< assignment operator
            mStringString& operator=(const Fw::StringBase& other); //!< other string assignment operator
            mStringString& operator=(const char* other); //!< char* assignment operator
            ~mStringString(); //!< destructor

            const char* toChar() const; //!< retrieves char buffer of string
            NATIVE_UINT_TYPE getCapacity() const ;

        private:

            char m_buf[80]; //!< buffer for string storage
    };


public:

    enum {
        SERIALIZED_SIZE =
        sizeof(F32) +
        sizeof(F64) +
        sizeof(I16) +
        sizeof(I32) +
        sizeof(I64) +
        sizeof(I8) +
        sizeof(U16) +
        sizeof(U32) +
        sizeof(U64) +
        sizeof(U8) +
        sizeof(bool) +
        S1::mStringString::SERIALIZED_SIZE
    }; //!< serializable size of S1

    S1(); //!< Default constructor
    S1(const S1* src); //!< pointer copy constructor
    S1(const S1& src); //!< reference copy constructor
    S1(F32 mF32, F64 mF64, I16 mI16, I32 mI32, I64 mI64, I8 mI8, U16 mU16, U32 mU32, U64 mU64, U8 mU8, bool mBool, const S1::mStringString& mString); //!< constructor with arguments
    S1& operator=(const S1& src); //!< equal operator
    bool operator==(const S1& src) const; //!< equality operator
#ifdef BUILD_UT
    // to support GoogleTest framework in unit tests
    friend std::ostream& operator<<(std::ostream& os, const S1& obj);
#endif

    void set(F32 mF32, F64 mF64, I16 mI16, I32 mI32, I64 mI64, I8 mI8, U16 mU16, U32 mU32, U64 mU64, U8 mU8, bool mBool, const S1::mStringString& mString); //!< set values

    F32 getmF32() const; //!< get member mF32
    F64 getmF64() const; //!< get member mF64
    I16 getmI16() const; //!< get member mI16
    I32 getmI32() const; //!< get member mI32
    I64 getmI64() const; //!< get member mI64
    I8 getmI8() const; //!< get member mI8
    U16 getmU16() const; //!< get member mU16
    U32 getmU32() const; //!< get member mU32
    U64 getmU64() const; //!< get member mU64
    U8 getmU8() const; //!< get member mU8
    bool getmBool() const; //!< get member mBool
    const S1::mStringString& getmString() const; //!< get member mString

    void setmF32(F32 val); //!< set member mF32
    void setmF64(F64 val); //!< set member mF64
    void setmI16(I16 val); //!< set member mI16
    void setmI32(I32 val); //!< set member mI32
    void setmI64(I64 val); //!< set member mI64
    void setmI8(I8 val); //!< set member mI8
    void setmU16(U16 val); //!< set member mU16
    void setmU32(U32 val); //!< set member mU32
    void setmU64(U64 val); //!< set member mU64
    void setmU8(U8 val); //!< set member mU8
    void setmBool(bool val); //!< set member mBool
    void setmString(const S1::mStringString& val); //!< set member mString


    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const; //!< serialization function
    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer); //!< deserialization function
#if FW_SERIALIZABLE_TO_STRING || BUILD_UT
    void toString(Fw::StringBase& text) const; //!< generate text from serializable
#endif
protected:

    enum {
        TYPE_ID = 0xC75528FE //!< type id
    };

    F32 m_mF32; //<! mF32 - 
    F64 m_mF64; //<! mF64 - 
    I16 m_mI16; //<! mI16 - 
    I32 m_mI32; //<! mI32 - 
    I64 m_mI64; //<! mI64 - 
    I8 m_mI8; //<! mI8 - 
    U16 m_mU16; //<! mU16 - 
    U32 m_mU32; //<! mU32 - 
    U64 m_mU64; //<! mU64 - 
    U8 m_mU8; //<! mU8 - 
    bool m_mBool; //<! mBool - 
    S1::mStringString m_mString; //<! mString - 
private:

};
} // end namespace M
#endif /* S1_HPP_ */

