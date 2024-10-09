
// ======================================================================
// \title  ActiveExternalStateMachines_S2.cpp
// \author Auto-generated
// \brief  cpp file for state machine ActiveExternalStateMachines_S2
//
// ======================================================================            
    
#include <Fw/Types/Assert.hpp>
#include "ActiveExternalStateMachines_S2.hpp"


void M::ActiveExternalStateMachines_S2::init(const FwEnumStoreType stateMachineId)
{
    parent->ActiveExternalStateMachines_S2_initLed(stateMachineId);
    parent->ActiveExternalStateMachines_S2_turnLedOn(stateMachineId);
    this->state = ON;

}


void M::ActiveExternalStateMachines_S2::update(
    const FwEnumStoreType stateMachineId, 
    const ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals signal, 
    const Fw::SmSignalBuffer &data
)
{
    switch (this->state) {
    
            /**
            * state ON
            */
            case ON:
            
            switch (signal) {

                case ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals::RTI_SIG:
                        if ( parent->ActiveExternalStateMachines_S2_g1(stateMachineId) ) {
                            parent->ActiveExternalStateMachines_S2_a1(stateMachineId);
                            parent->ActiveExternalStateMachines_S2_turnLedOff(stateMachineId);
                            this->state = OFF;
                        }

                    break;
    
                default:
                    break;
            }
            break;
    
            /**
            * state OFF
            */
            case OFF:
            
            switch (signal) {

                case ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals::RTI_SIG:
                        if (parent->ActiveExternalStateMachines_S2_g2(stateMachineId, signal, data) ) {
                            parent->ActiveExternalStateMachines_S2_a2(stateMachineId, signal, data);
                            parent->ActiveExternalStateMachines_S2_turnLedOn(stateMachineId);
                            this->state = ON;
                        }

                    break;
    
                default:
                    break;
            }
            break;
    
        default:
        FW_ASSERT(0);
    }
}
