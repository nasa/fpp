/*
 * S2.hpp
 *
 *  Created on: Thursday, 07 July 2022
 *  Author:     tchieu
 *
 */
#ifndef S2_HPP_
#define S2_HPP_

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
#include <S1SerializableAc.hpp>


class S2 : public Fw::Serializable {


public:

    enum {
        SERIALIZED_SIZE =
        M::S1::SERIALIZED_SIZE
    }; //!< serializable size of S2

    S2(); //!< Default constructor
    S2(const S2* src); //!< pointer copy constructor
    S2(const S2& src); //!< reference copy constructor
    S2(const M::S1& s1); //!< constructor with arguments
    S2& operator=(const S2& src); //!< equal operator
    bool operator==(const S2& src) const; //!< equality operator
#ifdef BUILD_UT
    // to support GoogleTest framework in unit tests
    friend std::ostream& operator<<(std::ostream& os, const S2& obj);
#endif

    void set(const M::S1& s1); //!< set values

    const M::S1& gets1() const; //!< get member s1

    void sets1(const M::S1& val); //!< set member s1


    Fw::SerializeStatus serialize(Fw::SerializeBufferBase& buffer) const; //!< serialization function
    Fw::SerializeStatus deserialize(Fw::SerializeBufferBase& buffer); //!< deserialization function
#if FW_SERIALIZABLE_TO_STRING || BUILD_UT
    void toString(Fw::StringBase& text) const; //!< generate text from serializable
#endif
protected:

    enum {
        TYPE_ID = 0x55243DDA //!< type id
    };

    M::S1 m_s1; //<! s1 - 
private:

};
#endif /* S2_HPP_ */

