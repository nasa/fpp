// ======================================================================
// \title  ActiveExternalStateMachines.cpp
// \author [user name]
// \brief  cpp file for ActiveExternalStateMachines component implementation class
// ======================================================================

#include "ActiveExternalStateMachines.hpp"
#include "FpConfig.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  ActiveExternalStateMachines ::
    ActiveExternalStateMachines(const char* const compName) :
      ActiveExternalStateMachinesComponentBase(compName)
  {

  }

  ActiveExternalStateMachines ::
    ~ActiveExternalStateMachines()
  {

  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for state machines
  // ----------------------------------------------------------------------

  void ActiveExternalStateMachines ::
    sm5_stateMachineOverflowHook(
        const M::ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals signal,
        const Fw::SmSignalBuffer& data
    )
  {
    // TODO
  }

}
