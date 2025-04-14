// ======================================================================
// \title  BasicSerializableAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Basic struct
// ======================================================================

#ifndef BasicSerializableAc_HPP
#define BasicSerializableAc_HPP

#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"
#include "TF32AliasAc.hpp"
#include "TStringAliasAc.hpp"
#include "TStringSizeAliasAc.hpp"
#include "TU32AliasAc.hpp"

class Basic :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the serial representation
      SERIALIZED_SIZE =
        sizeof(TU32) +
        sizeof(TF32) +
        Fw::StringBase::STATIC_SERIALIZED_SIZE(80) +
        Fw::StringBase::STATIC_SERIALIZED_SIZE(2)
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    Basic();

    //! Member constructor
    Basic(
        TU32 A,
        TF32 B,
        const Fw::StringBase& C,
        const Fw::StringBase& D
    );

    //! Copy constructor
    Basic(
        const Basic& obj //!< The source object
    );

  public:

    // ----------------------------------------------------------------------
    // Operators
    // ----------------------------------------------------------------------

    //! Copy assignment operator
    Basic& operator=(
        const Basic& obj //!< The source object
    );

    //! Equality operator
    bool operator==(
        const Basic& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const Basic& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const Basic& obj //!< The object
    );

#endif

  public:

    // ----------------------------------------------------------------------
    // Member functions
    // ----------------------------------------------------------------------

    //! Serialization
    Fw::SerializeStatus serialize(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    ) const;

    //! Deserialization
    Fw::SerializeStatus deserialize(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    );

#if FW_SERIALIZABLE_TO_STRING

    //! Convert struct to string
    void toString(
        Fw::StringBase& sb //!< The StringBase object to hold the result
    ) const;

#endif

    // ----------------------------------------------------------------------
    // Getter functions
    // ----------------------------------------------------------------------

    //! Get member A
    TU32 getA() const
    {
      return this->m_A;
    }

    //! Get member B
    TF32 getB() const
    {
      return this->m_B;
    }

    //! Get member C
    Fw::ExternalString& getC()
    {
      return this->m_C;
    }

    //! Get member C (const)
    const Fw::ExternalString& getC() const
    {
      return this->m_C;
    }

    //! Get member D
    Fw::ExternalString& getD()
    {
      return this->m_D;
    }

    //! Get member D (const)
    const Fw::ExternalString& getD() const
    {
      return this->m_D;
    }

    // ----------------------------------------------------------------------
    // Setter functions
    // ----------------------------------------------------------------------

    //! Set all members
    void set(
        TU32 A,
        TF32 B,
        const Fw::StringBase& C,
        const Fw::StringBase& D
    );

    //! Set member A
    void setA(TU32 A);

    //! Set member B
    void setB(TF32 B);

    //! Set member C
    void setC(const Fw::StringBase& C);

    //! Set member D
    void setD(const Fw::StringBase& D);

  protected:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    TU32 m_A;
    TF32 m_B;
    char m___fprime_ac_C_buffer[Fw::StringBase::BUFFER_SIZE(80)];
    Fw::ExternalString m_C;
    char m___fprime_ac_D_buffer[Fw::StringBase::BUFFER_SIZE(2)];
    Fw::ExternalString m_D;

};

#endif
