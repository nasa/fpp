// ======================================================================
// \title  StructWithAliasSerializableAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for StructWithAlias struct
// ======================================================================

#ifndef StructWithAliasSerializableAc_HPP
#define StructWithAliasSerializableAc_HPP

#include "AliasAliasArrayAliasAc.hpp"
#include "AliasArrayAliasAc.hpp"
#include "AliasArrayAliasArrayAliasAc.hpp"
#include "AliasPrim1AliasAc.hpp"
#include "AliasStringAliasAc.hpp"
#include "FpConfig.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"

class StructWithAlias :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the serial representation
      SERIALIZED_SIZE =
        sizeof(AliasPrim1) +
        AliasString::SERIALIZED_SIZE +
        AliasArray::SERIALIZED_SIZE +
        AliasAliasArray::SERIALIZED_SIZE +
        AliasArrayAliasArray::SERIALIZED_SIZE
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    StructWithAlias();

    //! Member constructor
    StructWithAlias(
        AliasPrim1 x,
        const Fw::StringBase& y,
        const AliasArray& z,
        const AliasAliasArray& w,
        const AliasArrayAliasArray& q
    );

    //! Copy constructor
    StructWithAlias(
        const StructWithAlias& obj //!< The source object
    );

  public:

    // ----------------------------------------------------------------------
    // Operators
    // ----------------------------------------------------------------------

    //! Copy assignment operator
    StructWithAlias& operator=(
        const StructWithAlias& obj //!< The source object
    );

    //! Equality operator
    bool operator==(
        const StructWithAlias& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const StructWithAlias& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const StructWithAlias& obj //!< The object
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

    //! Get member x
    AliasPrim1 getx() const
    {
      return this->m_x;
    }

    //! Get member y
    Fw::ExternalString& gety()
    {
      return this->m_y;
    }

    //! Get member y (const)
    const Fw::ExternalString& gety() const
    {
      return this->m_y;
    }

    //! Get member z
    AliasArray& getz()
    {
      return this->m_z;
    }

    //! Get member z (const)
    const AliasArray& getz() const
    {
      return this->m_z;
    }

    //! Get member w
    AliasAliasArray& getw()
    {
      return this->m_w;
    }

    //! Get member w (const)
    const AliasAliasArray& getw() const
    {
      return this->m_w;
    }

    //! Get member q
    AliasArrayAliasArray& getq()
    {
      return this->m_q;
    }

    //! Get member q (const)
    const AliasArrayAliasArray& getq() const
    {
      return this->m_q;
    }

    // ----------------------------------------------------------------------
    // Setter functions
    // ----------------------------------------------------------------------

    //! Set all members
    void set(
        AliasPrim1 x,
        const Fw::StringBase& y,
        const AliasArray& z,
        const AliasAliasArray& w,
        const AliasArrayAliasArray& q
    );

    //! Set member x
    void setx(AliasPrim1 x);

    //! Set member y
    void sety(const Fw::StringBase& y);

    //! Set member z
    void setz(const AliasArray& z);

    //! Set member w
    void setw(const AliasAliasArray& w);

    //! Set member q
    void setq(const AliasArrayAliasArray& q);

  protected:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    AliasPrim1 m_x;
    char m___fprime_ac_y_buffer[Fw::StringBase::BUFFER_SIZE(32)];
    Fw::ExternalString m_y;
    AliasArray m_z;
    AliasAliasArray m_w;
    AliasArrayAliasArray m_q;

};

#endif
