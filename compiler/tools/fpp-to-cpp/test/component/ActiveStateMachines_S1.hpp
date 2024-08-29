
// ======================================================================
// \title  ActiveStateMachines_S1.h
// \author Auto-generated
// \brief  header file for state machine ActiveStateMachines_S1
//
// ======================================================================
           
#ifndef ACTIVESTATEMACHINES_S1_H_
#define ACTIVESTATEMACHINES_S1_H_
                                
#include <Fw/SMSignal/SMSignalBuffer.hpp>
#include <config/FpConfig.hpp>
                                 
namespace M {

class ActiveStateMachines_S1_Interface {
  public:
    enum ActiveStateMachines_S1_Signals {
      RTI_SIG,
      WAIT_SIG,
    };

                                 
    virtual bool ActiveStateMachines_S1_g1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual bool ActiveStateMachines_S1_g2(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals signal, 
        const Fw::SMSignalBuffer &data) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S1_initLed(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S1_turnLedOn(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S1_a1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S1_turnLedOff(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual  void ActiveStateMachines_S1_a3(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals signal, 
        const Fw::SMSignalBuffer &data) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S1_blinkLed(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual  void ActiveStateMachines_S1_a2(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals signal, 
        const Fw::SMSignalBuffer &data) = 0;
                                 
                                 
    virtual  void ActiveStateMachines_S1_a4(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals signal, 
        const Fw::SMSignalBuffer &data) = 0;
                                 
                                                                  
};

class ActiveStateMachines_S1 {
                                 
  private:
    ActiveStateMachines_S1_Interface *parent;
                                 
  public:
                                 
    ActiveStateMachines_S1(ActiveStateMachines_S1_Interface* parent) : parent(parent) {}
  
    enum ActiveStateMachines_S1_States {
      ON,
      OFF,
      WAITING,
    };
    
    enum ActiveStateMachines_S1_States state;

    void init(const FwEnumStoreType stateMachineId);
    void update(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S1_Interface::ActiveStateMachines_S1_Signals signal, 
        const Fw::SMSignalBuffer &data
    );
};

}

#endif
