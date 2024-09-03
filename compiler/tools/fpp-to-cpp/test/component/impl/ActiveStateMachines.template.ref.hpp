// ======================================================================
// \title  ActiveStateMachines.hpp
// \author [user name]
// \brief  hpp file for ActiveStateMachines component implementation class
// ======================================================================

#ifndef M_ActiveStateMachines_HPP
#define M_ActiveStateMachines_HPP

#include "ActiveStateMachinesComponentAc.hpp"

namespace M {

  class ActiveStateMachines :
    public ActiveStateMachinesComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct ActiveStateMachines object
      ActiveStateMachines(
          const char* const compName //!< The component name
      );

      //! Destroy ActiveStateMachines object
      ~ActiveStateMachines();

    PRIVATE:

      // ----------------------------------------------------------------------
      // Overflow hook implementations for state machines
      // ----------------------------------------------------------------------

      //! Overflow hook implementation for sm5
      void sm5_stateMachineOverflowHook(
          const M::ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals signal, //!< The state machine signal
          const Fw::SMSignalBuffer& data //!< The state machine data
      ) override;

  };

}

#endif
