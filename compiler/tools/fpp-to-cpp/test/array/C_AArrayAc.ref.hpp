// ======================================================================
// \title  C_AArrayAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for C_A array
// ======================================================================

#ifndef C_C_AArrayAc_HPP
#define C_C_AArrayAc_HPP

#include "Fw/FPrimeBasicTypes.hpp"
#include "Fw/Types/ExternalString.hpp"
#include "Fw/Types/Serializable.hpp"
#include "Fw/Types/String.hpp"

class C_A :
  public Fw::Serializable
{

  public:

    // ----------------------------------------------------------------------
    // Types
    // ----------------------------------------------------------------------

    //! The element type
    using ElementType = U32;

  public:

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    enum {
      //! The size of the array
      SIZE = 3,
      //! The serialized size of each element
      ELEMENT_SERIALIZED_SIZE = sizeof(U32),
      //! The size of the serial representation
      SERIALIZED_SIZE = SIZE * ELEMENT_SERIALIZED_SIZE
    };

  public:

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    //! Constructor (default value)
    C_A();

    //! Constructor (user-provided value)
    C_A(
        const ElementType (&a)[SIZE] //!< The array
    );

    //! Constructor (single element)
    C_A(
        const ElementType& e //!< The element
    );

    //! Constructor (multiple elements)
    C_A(
        const ElementType& e1, //!< Element 1
        const ElementType& e2, //!< Element 2
        const ElementType& e3 //!< Element 3
    );

    //! Copy Constructor
    C_A(
        const C_A& obj //!< The source object
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
    C_A& operator=(
        const C_A& obj //!< The source object
    );

    //! Copy assignment operator (raw array)
    C_A& operator=(
        const ElementType (&a)[SIZE] //!< The source array
    );

    //! Copy assignment operator (single element)
    C_A& operator=(
        const ElementType& e //!< The element
    );

    //! Equality operator
    bool operator==(
        const C_A& obj //!< The other object
    ) const;

    //! Inequality operator
    bool operator!=(
        const C_A& obj //!< The other object
    ) const;

#ifdef BUILD_UT

    //! Ostream operator
    friend std::ostream& operator<<(
        std::ostream& os, //!< The ostream
        const C_A& obj //!< The object
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
