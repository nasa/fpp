// ======================================================================
// \title  ActiveStateMachines.hpp
// \author [user name]
// \brief  hpp file for ActiveStateMachines component implementation class
// ======================================================================

#ifndef ActiveStateMachines_HPP
#define ActiveStateMachines_HPP

#include "ActiveStateMachinesComponentAc.hpp"

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
        const Fw::SMSignals& sig //!< The state machine signal
    ) override;

};

#endif
