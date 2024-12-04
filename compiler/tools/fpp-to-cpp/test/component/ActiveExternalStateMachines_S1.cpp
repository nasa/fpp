
// ======================================================================
// \title  ActiveExternalStateMachines_S1.cpp
// \author Auto-generated
// \brief  cpp file for state machine ActiveExternalStateMachines_S1
//
// ======================================================================            
    
#include <Fw/Types/Assert.hpp>
#include "ActiveExternalStateMachines_S1.hpp"


void M::ActiveExternalStateMachines_S1::init(const FwEnumStoreType stateMachineId)
{
    parent->ActiveExternalStateMachines_S1_initLed(stateMachineId);
    parent->ActiveExternalStateMachines_S1_turnLedOn(stateMachineId);
    this->state = ON;

}


void M::ActiveExternalStateMachines_S1::update(
    const FwEnumStoreType stateMachineId, 
    const ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals signal, 
    const Fw::SmSignalBuffer &data
)
{
    switch (this->state) {
    
            /**
            * state ON
            */
            case ON:
            
            switch (signal) {

                case ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals::RTI_SIG:
                        if ( parent->ActiveExternalStateMachines_S1_g1(stateMachineId) ) {
                            parent->ActiveExternalStateMachines_S1_a1(stateMachineId);
                            parent->ActiveExternalStateMachines_S1_turnLedOff(stateMachineId);
                            this->state = OFF;
                        }

                    break;
    
                case ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals::WAIT_SIG:
                        parent->ActiveExternalStateMachines_S1_a3(stateMachineId, signal, data);
                        parent->ActiveExternalStateMachines_S1_blinkLed(stateMachineId);
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

                case ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals::RTI_SIG:
                        if (parent->ActiveExternalStateMachines_S1_g2(stateMachineId, signal, data) ) {
                            parent->ActiveExternalStateMachines_S1_a2(stateMachineId, signal, data);
                            parent->ActiveExternalStateMachines_S1_turnLedOn(stateMachineId);
                            this->state = ON;
                        }

                    break;
    
                case ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals::WAIT_SIG:
                        parent->ActiveExternalStateMachines_S1_a4(stateMachineId, signal, data);
                        parent->ActiveExternalStateMachines_S1_blinkLed(stateMachineId);
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
        FW_ASSERT(0);
    }
}
