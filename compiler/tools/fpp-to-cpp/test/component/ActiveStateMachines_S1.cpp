
// ======================================================================
// \title  ActiveStateMachines_S1.cpp
// \author Auto-generated
// \brief  cpp file for state machine ActiveStateMachines_S1
//
// ======================================================================            
    
#include "stdio.h"
#include "assert.h"
#include "Fw/Types/SMSignalsSerializableAc.hpp"
#include "ActiveStateMachines_S1.hpp"


void M::ActiveStateMachines_S1::init(const FwEnumStoreType stateMachineId)
{
    parent->ActiveStateMachines_S1_initLed(stateMachineId);
    parent->ActiveStateMachines_S1_turnLedOn(stateMachineId);
    this->state = ON;

}


void M::ActiveStateMachines_S1::update(
    const FwEnumStoreType stateMachineId, 
    const ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals signal, 
    const Fw::SMSignalBuffer &data
)
{
    switch (this->state) {
    
            /**
            * state ON
            */
            case ON:
            
            switch (signal) {

                case ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals::RTI_SIG:
                        if ( parent->ActiveStateMachines_S1_g1(stateMachineId) ) {
                            parent->ActiveStateMachines_S1_a1(stateMachineId);
                            parent->ActiveStateMachines_S1_turnLedOff(stateMachineId);
                            this->state = OFF;
                        }

                    break;
    
                case ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals::WAIT_SIG:
                        parent->ActiveStateMachines_S1_a3(stateMachineId, signal, data);
                        parent->ActiveStateMachines_S1_blinkLed(stateMachineId);
                        this->state = WAITING;

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

                case ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals::RTI_SIG:
                        if (parent->ActiveStateMachines_S1_g2(stateMachineId, signal, data) ) {
                            parent->ActiveStateMachines_S1_a2(stateMachineId, signal, data);
                            parent->ActiveStateMachines_S1_turnLedOn(stateMachineId);
                            this->state = ON;
                        }

                    break;
    
                case ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals::WAIT_SIG:
                        parent->ActiveStateMachines_S1_a4(stateMachineId, signal, data);
                        parent->ActiveStateMachines_S1_blinkLed(stateMachineId);
                        this->state = WAITING;

                    break;
    
                default:
                    break;
            }
            break;
    
            /**
            * state WAITING
            */
            case WAITING:
            
            switch (signal) {

                default:
                    break;
            }
            break;
    
        default:
        assert(0);
    }
}
