/*
 * StructOK2.hpp
 *
 *  Created on: Thursday, 04 June 2020
 *  Author:     bocchino
 *
 */
#ifndef STRUCTOK2_HPP_
#define STRUCTOK2_HPP_

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
#include </Users/bocchino/JPL/Tools/fpp/compiler/tools/fpp-to-xml/test/StructOK1SerializableAc.hpp>


class StructOK2 : public Fw::Serializable {


public:

    enum {
        SERIALIZED_SIZE =
        StructOK1::SERIALIZED_SIZE
    }; //!< serializable size of StructOK2

    StructOK2(void); //!< Default constructor
    StructOK2(const StructOK2* src); //!< pointer copy constructor
    StructOK2(const StructOK2& src); //!< reference copy constructor
    StructOK2(const StructOK1& s1); //!< constructor with arguments
    const StructOK2& operator=(const StructOK2& src); //!< equal operator
    bool operator==(const StructOK2& src) const; //!< equality operator
#ifdef BUILD_UT
    // to support GoogleTest framework in unit tests
    friend std::ostream& operator<<(std::ostream& os, const StructOK2& obj);
#endif

    void set(const StructOK1& s1); //!< set values

    StructOK1 gets1(void); //!< get member s1

    void sets1(StructOK1 val); //!< set member s1


    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const; //!< serialization function
    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer); //!< deserialization function
#if FW_SERIALIZABLE_TO_STRING || BUILD_UT
    void toString(Fw::StringBase& text) const; //!< generate text from serializable
#endif
protected:

    enum {
        TYPE_ID = 0x42FD26E3 //!< type id
    };

    StructOK1 m_s1; //<! s1 - 
private:

};
#endif /* STRUCTOK2_HPP_ */

