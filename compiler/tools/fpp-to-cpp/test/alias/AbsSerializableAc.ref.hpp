// ======================================================================
// \title  AbsSerializableAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Abs struct
// ======================================================================

#ifndef AbsSerializableAc_HPP
#define AbsSerializableAc_HPP

#include "AbsTypeAliasAc.hpp"
#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"

class Abs :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the serial representation
      SERIALIZED_SIZE =
        AbsType::SERIALIZED_SIZE
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    Abs();

    //! Member constructor
    Abs(const AbsType& A);

    //! Copy constructor
    Abs(
        const Abs& obj //!< The source object
    );

  public:

    // ----------------------------------------------------------------------
    // Operators
    // ----------------------------------------------------------------------

    //! Copy assignment operator
    Abs& operator=(
        const Abs& obj //!< The source object
    );

    //! Equality operator
    bool operator==(
        const Abs& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const Abs& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const Abs& obj //!< The object
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
    AbsType& getA()
    {
      return this->m_A;
    }

    //! Get member A (const)
    const AbsType& getA() const
    {
      return this->m_A;
    }

    // ----------------------------------------------------------------------
    // Setter functions
    // ----------------------------------------------------------------------

    //! Set all members
    void set(const AbsType& A);

    //! Set member A
    void setA(const AbsType& A);

  protected:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    AbsType m_A;

};

#endif
