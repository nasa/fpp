
// ======================================================================
// \title  ActiveExternalStateMachines_S1.h
// \author Auto-generated
// \brief  header file for state machine ActiveExternalStateMachines_S1
//
// ======================================================================
           
#ifndef ACTIVESTATEMACHINES_S1_H_
#define ACTIVESTATEMACHINES_S1_H_
                                
#include <Fw/Sm/SmSignalBuffer.hpp>
#include <config/FpConfig.hpp>
                                 
namespace ExternalSm {

class ActiveExternalStateMachines_S1_Interface {
  public:
    enum ActiveExternalStateMachines_S1_Signals {
      RTI_SIG,
      WAIT_SIG,
    };

                                 
    virtual bool ActiveExternalStateMachines_S1_g1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual bool ActiveExternalStateMachines_S1_g2(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals signal, 
        const Fw::SmSignalBuffer &data) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S1_initLed(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S1_turnLedOn(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S1_a1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S1_turnLedOff(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual  void ActiveExternalStateMachines_S1_a3(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals signal, 
        const Fw::SmSignalBuffer &data) = 0;
                                 
                                 
    virtual void ActiveExternalStateMachines_S1_blinkLed(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual  void ActiveExternalStateMachines_S1_a2(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals signal, 
        const Fw::SmSignalBuffer &data) = 0;
                                 
                                 
    virtual  void ActiveExternalStateMachines_S1_a4(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals signal, 
        const Fw::SmSignalBuffer &data) = 0;
                                 
                                                                  
};

class ActiveExternalStateMachines_S1 {
                                 
  private:
    ActiveExternalStateMachines_S1_Interface *parent;
                                 
  public:
                                 
    ActiveExternalStateMachines_S1(ActiveExternalStateMachines_S1_Interface* parent) : parent(parent) {}
  
    enum ActiveExternalStateMachines_S1_States {
      ON,
      OFF,
      WAITING,
    };
    
    enum ActiveExternalStateMachines_S1_States state;

    void init(const FwEnumStoreType stateMachineId);
    void update(
        const FwEnumStoreType stateMachineId, 
        const ActiveExternalStateMachines_S1_Interface::ActiveExternalStateMachines_S1_Signals signal, 
        const Fw::SmSignalBuffer &data
    );
};

}

#endif
