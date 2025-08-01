// ======================================================================
// \title  FormatSerializableAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Format struct
// ======================================================================

#ifndef FormatSerializableAc_HPP
#define FormatSerializableAc_HPP

#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"

class Format :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the serial representation
      SERIALIZED_SIZE =
        sizeof(I32) +
        sizeof(U32) +
        sizeof(I32) +
        sizeof(U32) +
        sizeof(I32) +
        sizeof(U32) +
        sizeof(I32) +
        sizeof(U32) +
        sizeof(I32) +
        sizeof(U32) +
        sizeof(F32) +
        sizeof(F32) +
        sizeof(F32) +
        sizeof(F32) +
        sizeof(F32) +
        sizeof(F32) +
        sizeof(F32)
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    Format();

    //! Member constructor
    Format(
        I32 m1,
        U32 m2,
        I32 m3,
        U32 m4,
        I32 m5,
        U32 m6,
        I32 m7,
        U32 m8,
        I32 m9,
        U32 m10,
        F32 m11,
        F32 m12,
        F32 m13,
        F32 m14,
        F32 m15,
        F32 m16,
        F32 m17
    );

    //! Copy constructor
    Format(
        const Format& obj //!< The source object
    );

  public:

    // ----------------------------------------------------------------------
    // Operators
    // ----------------------------------------------------------------------

    //! Copy assignment operator
    Format& operator=(
        const Format& obj //!< The source object
    );

    //! Equality operator
    bool operator==(
        const Format& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const Format& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const Format& obj //!< The object
    );

#endif

  public:

    // ----------------------------------------------------------------------
    // Member functions
    // ----------------------------------------------------------------------

    //! Serialization
    Fw::SerializeStatus serializeTo(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    ) const;

    //! Deserialization
    Fw::SerializeStatus deserializeFrom(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    );

    //! Get the dynamic serialized size of the struct
    FwSizeType serializedSize() const;

#if FW_SERIALIZABLE_TO_STRING

    //! Convert struct to string
    void toString(
        Fw::StringBase& sb //!< The StringBase object to hold the result
    ) const;

#endif

    // ----------------------------------------------------------------------
    // Getter functions
    // ----------------------------------------------------------------------

    //! Get member m1
    I32 get_m1() const
    {
      return this->m_m1;
    }

    //! Get member m2
    U32 get_m2() const
    {
      return this->m_m2;
    }

    //! Get member m3
    I32 get_m3() const
    {
      return this->m_m3;
    }

    //! Get member m4
    U32 get_m4() const
    {
      return this->m_m4;
    }

    //! Get member m5
    I32 get_m5() const
    {
      return this->m_m5;
    }

    //! Get member m6
    U32 get_m6() const
    {
      return this->m_m6;
    }

    //! Get member m7
    I32 get_m7() const
    {
      return this->m_m7;
    }

    //! Get member m8
    U32 get_m8() const
    {
      return this->m_m8;
    }

    //! Get member m9
    I32 get_m9() const
    {
      return this->m_m9;
    }

    //! Get member m10
    U32 get_m10() const
    {
      return this->m_m10;
    }

    //! Get member m11
    F32 get_m11() const
    {
      return this->m_m11;
    }

    //! Get member m12
    F32 get_m12() const
    {
      return this->m_m12;
    }

    //! Get member m13
    F32 get_m13() const
    {
      return this->m_m13;
    }

    //! Get member m14
    F32 get_m14() const
    {
      return this->m_m14;
    }

    //! Get member m15
    F32 get_m15() const
    {
      return this->m_m15;
    }

    //! Get member m16
    F32 get_m16() const
    {
      return this->m_m16;
    }

    //! Get member m17
    F32 get_m17() const
    {
      return this->m_m17;
    }

    // ----------------------------------------------------------------------
    // Setter functions
    // ----------------------------------------------------------------------

    //! Set all members
    void set(
        I32 m1,
        U32 m2,
        I32 m3,
        U32 m4,
        I32 m5,
        U32 m6,
        I32 m7,
        U32 m8,
        I32 m9,
        U32 m10,
        F32 m11,
        F32 m12,
        F32 m13,
        F32 m14,
        F32 m15,
        F32 m16,
        F32 m17
    );

    //! Set member m1
    void set_m1(I32 m1);

    //! Set member m2
    void set_m2(U32 m2);

    //! Set member m3
    void set_m3(I32 m3);

    //! Set member m4
    void set_m4(U32 m4);

    //! Set member m5
    void set_m5(I32 m5);

    //! Set member m6
    void set_m6(U32 m6);

    //! Set member m7
    void set_m7(I32 m7);

    //! Set member m8
    void set_m8(U32 m8);

    //! Set member m9
    void set_m9(I32 m9);

    //! Set member m10
    void set_m10(U32 m10);

    //! Set member m11
    void set_m11(F32 m11);

    //! Set member m12
    void set_m12(F32 m12);

    //! Set member m13
    void set_m13(F32 m13);

    //! Set member m14
    void set_m14(F32 m14);

    //! Set member m15
    void set_m15(F32 m15);

    //! Set member m16
    void set_m16(F32 m16);

    //! Set member m17
    void set_m17(F32 m17);

  protected:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    I32 m_m1;
    U32 m_m2;
    I32 m_m3;
    U32 m_m4;
    I32 m_m5;
    U32 m_m6;
    I32 m_m7;
    U32 m_m8;
    I32 m_m9;
    U32 m_m10;
    F32 m_m11;
    F32 m_m12;
    F32 m_m13;
    F32 m_m14;
    F32 m_m15;
    F32 m_m16;
    F32 m_m17;

};

#endif
