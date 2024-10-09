// ======================================================================
// \title  ActiveExternalStateMachines.hpp
// \author [user name]
// \brief  hpp file for ActiveExternalStateMachines component implementation class
// ======================================================================

#ifndef M_ActiveExternalStateMachines_HPP
#define M_ActiveExternalStateMachines_HPP

#include "ActiveExternalStateMachinesComponentAc.hpp"

namespace M {

  class ActiveExternalStateMachines :
    public ActiveExternalStateMachinesComponentBase
  {

    public:

      // ----------------------------------------------------------------------
      // Component construction and destruction
      // ----------------------------------------------------------------------

      //! Construct ActiveExternalStateMachines object
      ActiveExternalStateMachines(
          const char* const compName //!< The component name
      );

      //! Destroy ActiveExternalStateMachines object
      ~ActiveExternalStateMachines();

    PRIVATE:

      // ----------------------------------------------------------------------
      // Overflow hook implementations for state machines
      // ----------------------------------------------------------------------

      //! Overflow hook implementation for sm5
      void sm5_stateMachineOverflowHook(
          const M::ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals signal, //!< The state machine signal
          const Fw::SmSignalBuffer& data //!< The state machine data
      ) override;

  };

}

#endif
