
// ======================================================================
// \title  ActiveStateMachines_S2.cpp
// \author Auto-generated
// \brief  cpp file for state machine ActiveStateMachines_S2
//
// ======================================================================            
    
#include "stdio.h"
#include "assert.h"
#include "Fw/Types/SMSignalsSerializableAc.hpp"
#include "ActiveStateMachines_S2.hpp"


void M::ActiveStateMachines_S2::init(const FwEnumStoreType stateMachineId)
{
    parent->ActiveStateMachines_S2_initLed(stateMachineId);
    parent->ActiveStateMachines_S2_turnLedOn(stateMachineId);
    this->state = ON;

}


void M::ActiveStateMachines_S2::update(
    const FwEnumStoreType stateMachineId, 
    const ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals signal, 
    const Fw::SMSignalBuffer &data
)
{
    switch (this->state) {
    
            /**
            * state ON
            */
            case ON:
            
            switch (signal) {

                case ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals::RTI_SIG:
                        if ( parent->ActiveStateMachines_S2_g1(stateMachineId) ) {
                            parent->ActiveStateMachines_S2_a1(stateMachineId);
                            parent->ActiveStateMachines_S2_turnLedOff(stateMachineId);
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

                case ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals::RTI_SIG:
                        if (parent->ActiveStateMachines_S2_g2(stateMachineId, signal, data) ) {
                            parent->ActiveStateMachines_S2_a2(stateMachineId, signal, data);
                            parent->ActiveStateMachines_S2_turnLedOn(stateMachineId);
                            this->state = ON;
                        }

                    break;
    
                default:
                    break;
            }
            break;
    
        default:
        assert(0);
    }
}
