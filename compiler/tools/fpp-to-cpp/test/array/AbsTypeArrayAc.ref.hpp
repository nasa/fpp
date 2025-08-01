// ======================================================================
// \title  AbsTypeArrayAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for AbsType array
// ======================================================================

#ifndef AbsTypeArrayAc_HPP
#define AbsTypeArrayAc_HPP

#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"
#include "T.hpp"

//! An array of abstract type
class AbsType :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Types
    // ----------------------------------------------------------------------

    //! The element type
    using ElementType = T;

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the array
      SIZE = 3,
      //! The serialized size of each element
      ELEMENT_SERIALIZED_SIZE = T::SERIALIZED_SIZE,
      //! The size of the serial representation
      SERIALIZED_SIZE = SIZE * ELEMENT_SERIALIZED_SIZE
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    AbsType();

    //! Constructor (user-provided value)
    AbsType(
        const ElementType (&a)[SIZE] //!< The array
    );

    //! Constructor (single element)
    AbsType(
        const ElementType& e //!< The element
    );

    //! Constructor (multiple elements)
    AbsType(
        const ElementType& e1, //!< Element 1
        const ElementType& e2, //!< Element 2
        const ElementType& e3 //!< Element 3
    );

    //! Copy Constructor
    AbsType(
        const AbsType& obj //!< The source object
    );

  public:

    // ----------------------------------------------------------------------
    // Operators
    // ----------------------------------------------------------------------

    //! Subscript operator
    ElementType& operator[](
        const U32 i //!< The subscript index
    );

    //! Const subscript operator
    const ElementType& operator[](
        const U32 i //!< The subscript index
    ) const;

    //! Copy assignment operator (object)
    AbsType& operator=(
        const AbsType& obj //!< The source object
    );

    //! Copy assignment operator (raw array)
    AbsType& operator=(
        const ElementType (&a)[SIZE] //!< The source array
    );

    //! Copy assignment operator (single element)
    AbsType& operator=(
        const ElementType& e //!< The element
    );

    //! Equality operator
    bool operator==(
        const AbsType& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const AbsType& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const AbsType& obj //!< The object
    );

#endif

  public:

    // ----------------------------------------------------------------------
    // Public member functions
    // ----------------------------------------------------------------------

    //! Serialization
    Fw::SerializeStatus serializeTo(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    ) const;

    //! Deserialization
    Fw::SerializeStatus deserializeFrom(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    );

    //! Get the dynamic serialized size of the array
    FwSizeType serializedSize() const;

#if FW_SERIALIZABLE_TO_STRING

    //! Convert array to string
    void toString(
        Fw::StringBase& sb //!< The StringBase object to hold the result
    ) const;

#endif

  private:

    // ----------------------------------------------------------------------
    // Member variables
    // ----------------------------------------------------------------------

    //! The array elements
    ElementType elements[SIZE];

};

#endif
