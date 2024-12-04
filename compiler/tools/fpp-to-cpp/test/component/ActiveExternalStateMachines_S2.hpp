
// ======================================================================
// \title  ActiveExternalStateMachines_S2.h
// \author Auto-generated
// \brief  header file for state machine ActiveExternalStateMachines_S2
//
// ======================================================================
           
#ifndef ACTIVESTATEMACHINES_S2_H_
#define ACTIVESTATEMACHINES_S2_H_
                                
#include <Fw/Sm/SmSignalBuffer.hpp>
#include <config/FpConfig.hpp>
                                 
namespace ExternalSm {

class ActiveExternalStateMachines_S2_Interface {
  public:
    enum ActiveExternalStateMachines_S2_Signals {
      RTI_SIG,
    };

                                 
    virtual bool ActiveExternalStateMachines_S2_g1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual bool ActiveExternalStateMachines_S2_g2(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals signal, 
        const Fw::SmSignalBuffer &data) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S2_initLed(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S2_turnLedOn(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S2_a1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S2_turnLedOff(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual  void ActiveExternalStateMachines_S2_a2(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals signal, 
        const Fw::SmSignalBuffer &data) = 0;
                                 
                                                                  
};

class ActiveExternalStateMachines_S2 {
                                 
  private:
    ActiveExternalStateMachines_S2_Interface *parent;
                                 
  public:
                                 
    ActiveExternalStateMachines_S2(ActiveExternalStateMachines_S2_Interface* parent) : parent(parent) {}
  
    enum ActiveExternalStateMachines_S2_States {
      ON,
      OFF,
    };
    
    enum ActiveExternalStateMachines_S2_States state;

    void init(const FwEnumStoreType stateMachineId);
    void update(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S2_Interface::ActiveExternalStateMachines_S2_Signals signal, 
        const Fw::SmSignalBuffer &data
    );
};

}

#endif
