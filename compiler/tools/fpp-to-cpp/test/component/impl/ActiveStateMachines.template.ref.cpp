// ======================================================================
// \title  ActiveStateMachines.cpp
// \author [user name]
// \brief  cpp file for ActiveStateMachines component implementation class
// ======================================================================

#include "ActiveStateMachines.hpp"
#include "FpConfig.hpp"

namespace M {

  // ----------------------------------------------------------------------
  // Component construction and destruction
  // ----------------------------------------------------------------------

  ActiveStateMachines ::
    ActiveStateMachines(const char* const compName) :
      ActiveStateMachinesComponentBase(compName)
  {

  }

  ActiveStateMachines ::
    ~ActiveStateMachines()
  {

  }

  // ----------------------------------------------------------------------
  // Overflow hook implementations for state machines
  // ----------------------------------------------------------------------

  void ActiveStateMachines ::
    sm5_stateMachineOverflowHook(
        const M::ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals signal,
        const Fw::SMSignalBuffer& data
    )
  {
    // TODO
  }

}
