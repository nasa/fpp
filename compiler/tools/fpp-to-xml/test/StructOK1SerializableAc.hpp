/*
 * StructOK1.hpp
 *
 *  Created on: Thursday, 04 June 2020
 *  Author:     bocchino
 *
 */
#ifndef STRUCTOK1_HPP_
#define STRUCTOK1_HPP_

#include <Fw/Types/BasicTypes.hpp>
#include <Fw/Types/Serializable.hpp>
#if FW_SERIALIZABLE_TO_STRING
#include <Fw/Types/StringType.hpp>
#include <stdio.h> // snprintf
#ifdef BUILD_UT
#include <iostream>
#include <Fw/Types/EightyCharString.hpp>
#endif
#endif


class StructOK1 : public Fw::Serializable {


  public:
    class m_stringString : public Fw::StringBase {
        public:

            enum {
                SERIALIZED_SIZE = 80 + sizeof(FwBuffSizeType) //!<size of buffer + storage of two size words
            };

            m_stringString(const char* src); //!< char array constructor
            m_stringString(const Fw::StringBase& src); //!< string base constructor
            m_stringString(const m_stringString& src); //!< string base constructor
            m_stringString(void); //!< default constructor
            virtual ~m_stringString(void); //!< destructor
            const char* toChar(void) const; //!< retrieves char buffer of string
            NATIVE_UINT_TYPE length(void) const; //!< returns length of string
            bool operator==(const m_stringString& src) const; //!< equality operator

            const m_stringString& operator=(const m_stringString& other); //!< equal operator for other strings

            Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const; //!< serialization function
            Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer); //!< deserialization function

        private:
            void copyBuff(const char* buff, NATIVE_UINT_TYPE size); //!< copy a buffer, overwriting current contents
            NATIVE_UINT_TYPE getCapacity(void) const ;
            void terminate(NATIVE_UINT_TYPE size); //!< terminate the string

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
        StructOK1::m_stringString::SERIALIZED_SIZE
    }; //!< serializable size of StructOK1

    StructOK1(void); //!< Default constructor
    StructOK1(const StructOK1* src); //!< pointer copy constructor
    StructOK1(const StructOK1& src); //!< reference copy constructor
    StructOK1(F32 mF32, F64 mF64, I16 mI16, I32 mI32, I64 mI64, I8 mI8, U16 mU16, U32 mU32, U64 mU64, U8 mU8, bool m_bool, const StructOK1::m_stringString& m_string); //!< constructor with arguments
    const StructOK1& operator=(const StructOK1& src); //!< equal operator
    bool operator==(const StructOK1& src) const; //!< equality operator
#ifdef BUILD_UT
    // to support GoogleTest framework in unit tests
    friend std::ostream& operator<<(std::ostream& os, const StructOK1& obj);
#endif

    void set(F32 mF32, F64 mF64, I16 mI16, I32 mI32, I64 mI64, I8 mI8, U16 mU16, U32 mU32, U64 mU64, U8 mU8, bool m_bool, const StructOK1::m_stringString& m_string); //!< set values

    F32 getmF32(void); //!< get member mF32
    F64 getmF64(void); //!< get member mF64
    I16 getmI16(void); //!< get member mI16
    I32 getmI32(void); //!< get member mI32
    I64 getmI64(void); //!< get member mI64
    I8 getmI8(void); //!< get member mI8
    U16 getmU16(void); //!< get member mU16
    U32 getmU32(void); //!< get member mU32
    U64 getmU64(void); //!< get member mU64
    U8 getmU8(void); //!< get member mU8
    bool getm_bool(void); //!< get member m_bool
    const StructOK1::m_stringString& getm_string(void); //!< get member m_string

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
    void setm_bool(bool val); //!< set member m_bool
    void setm_string(const StructOK1::m_stringString& val); //!< set member m_string


    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const; //!< serialization function
    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer); //!< deserialization function
#if FW_SERIALIZABLE_TO_STRING || BUILD_UT
    void toString(Fw::StringBase& text) const; //!< generate text from serializable
#endif
protected:

    enum {
        TYPE_ID = 0x1D2E51B4 //!< type id
    };

    F32 m_mF32; //<! mF32 - Member annotation, line 1. Member annotation, line 2.
    F64 m_mF64; //<! mF64 - 
    I16 m_mI16; //<! mI16 - 
    I32 m_mI32; //<! mI32 - 
    I64 m_mI64; //<! mI64 - 
    I8 m_mI8; //<! mI8 - 
    U16 m_mU16; //<! mU16 - 
    U32 m_mU32; //<! mU32 - 
    U64 m_mU64; //<! mU64 - 
    U8 m_mU8; //<! mU8 - 
    bool m_m_bool; //<! m_bool - 
    StructOK1::m_stringString m_m_string; //<! m_string - 
private:

};
#endif /* STRUCTOK1_HPP_ */

