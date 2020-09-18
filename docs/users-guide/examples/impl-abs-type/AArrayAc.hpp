// ====================================================================== 
// \title  A.hpp
// \author Auto-generated
// \brief  hpp file for A
//
// \copyright
// Copyright (C) 2020 California Institute of Technology.
// ALL RIGHTS RESERVED.  United States Government Sponsorship
// acknowledged. Any commercial use must be negotiated with the Office
// of Technology Transfer at the California Institute of Technology.
// 
// This software may be subject to U.S. export control laws and
// regulations.  By accepting this document, the user agrees to comply
// with all U.S. export laws and regulations.  User has the
// responsibility to obtain export licenses, or other export authority
// as may be required before exporting such information to foreign
// countries or providing access to foreign persons.
// ======================================================================

#ifndef _A_HPP
#define _A_HPP

#include "Fw/Types/EightyCharString.hpp"
#include "Fw/Types/BasicTypes.hpp"
#include "Fw/Types/Serializable.hpp"
#include <T.hpp>

  class A : public Fw::Serializable
  {
    public:

    // ----------------------------------------------------------------------
    // Helper Types
    // ----------------------------------------------------------------------

    typedef T ElementType;
    
    enum {
        SIZE=3,
        SERIALIZED_SIZE = SIZE *
        T::SERIALIZED_SIZE
    }; //!< serializable size of A
  
    public:
  
    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------
  
      //! Construct a A with default initialization
      A(void);
  
      //! Construct a A and initialize its elements from an array
      A(
          const ElementType (&a)[SIZE] //!< The array
      );

      //! Construct a A and initialize its elements from a single element
      A(
          const ElementType& e //!< The element
      );
  
      //! Construct a A and initialize its elements from elements
   A(
      const ElementType (&e1), //!< Element 1
      const ElementType (&e2), //!< Element 2
      const ElementType (&e3)
      );
  
      //! Copy constructor
      A(
          const A& other //!< The other object
      );
  
    public:
  
      // ----------------------------------------------------------------------
      // Public operators
      // ----------------------------------------------------------------------
  
      //! Subscript operator
      ElementType& operator[](
          const U32 i //!< The subscript index
      );
  
      //! Const subscript operator
      const ElementType& operator[](
          const U32 i //!< The subscript index
      ) const;
  
      //! Assignment operator
      const A& operator=(
          const A& other //!< The other object
      );
  
      //! Assignment operator from array
      const A& operator=(
          const ElementType (&a)[SIZE] //!< The array
      );
  
      //! Assignment operator from element
      const A& operator=(
          const ElementType& e //!< The element
      );
  
      //! Equality operator
      bool operator==(
          const A& other //!< The other object
      ) const;
  
      //! Inequality operator
      bool operator!=(
          const A& other //!< The other object
      ) const;
  
#if 0
      //! Ostream operator
      friend std::ostream& operator<<(
          std::ostream& os, //!< The ostream
          const A& obj //!< The object
      );
#endif
  
    public:
  
    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------
  
    //! Serialization
    Fw::SerializeStatus serialize(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    ) const;
  
    //! Deserialization
    Fw::SerializeStatus deserialize(
        Fw::SerializeBufferBase& buffer //!< The serial buffer
    );

#if 0
    void toString(Fw::StringBase& text) const; //!< generate text from serializable
#endif

    protected:

    enum {
        TYPE_ID = 0x6CB4E96B //!< type id
    };
  
    private:
  
    // ----------------------------------------------------------------------
    // Private member variables
    // ----------------------------------------------------------------------
  
    //! The array elements
    ElementType elements[SIZE];
  
  };


#endif
