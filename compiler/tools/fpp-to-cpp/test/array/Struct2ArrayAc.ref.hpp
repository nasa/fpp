// ======================================================================
// \title  Struct2ArrayAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Struct2 array
// ======================================================================

#ifndef Struct2ArrayAc_HPP
#define Struct2ArrayAc_HPP

#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"
#include "S2SerializableAc.hpp"

//! Array of structs with struct member
class Struct2 :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Types
    // ----------------------------------------------------------------------

    //! The element type
    using ElementType = S2;

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the array
      SIZE = 3,
      //! The serialized size of each element
      ELEMENT_SERIALIZED_SIZE = S2::SERIALIZED_SIZE,
      //! The size of the serial representation
      SERIALIZED_SIZE = SIZE * ELEMENT_SERIALIZED_SIZE
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    Struct2();

    //! Constructor (user-provided value)
    Struct2(
        const ElementType (&a)[SIZE] //!< The array
    );

    //! Constructor (single element)
    Struct2(
        const ElementType& e //!< The element
    );

    //! Constructor (multiple elements)
    Struct2(
        const ElementType& e1, //!< Element 1
        const ElementType& e2, //!< Element 2
        const ElementType& e3 //!< Element 3
    );

    //! Copy Constructor
    Struct2(
        const Struct2& obj //!< The source object
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
    Struct2& operator=(
        const Struct2& obj //!< The source object
    );

    //! Copy assignment operator (raw array)
    Struct2& operator=(
        const ElementType (&a)[SIZE] //!< The source array
    );

    //! Copy assignment operator (single element)
    Struct2& operator=(
        const ElementType& e //!< The element
    );

    //! Equality operator
    bool operator==(
        const Struct2& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const Struct2& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const Struct2& obj //!< The object
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
