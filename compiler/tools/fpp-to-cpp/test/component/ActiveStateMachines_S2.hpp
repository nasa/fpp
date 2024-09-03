
// ======================================================================
// \title  ActiveStateMachines_S2.h
// \author Auto-generated
// \brief  header file for state machine ActiveStateMachines_S2
//
// ======================================================================
           
#ifndef ACTIVESTATEMACHINES_S2_H_
#define ACTIVESTATEMACHINES_S2_H_
                                
#include <Fw/SMSignal/SMSignalBuffer.hpp>
#include <config/FpConfig.hpp>
                                 
namespace M {

class ActiveStateMachines_S2_Interface {
  public:
    enum ActiveStateMachines_S2_Signals {
      RTI_SIG,
    };

                                 
    virtual bool ActiveStateMachines_S2_g1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual bool ActiveStateMachines_S2_g2(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals signal, 
        const Fw::SMSignalBuffer &data) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S2_initLed(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S2_turnLedOn(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S2_a1(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual void ActiveStateMachines_S2_turnLedOff(const FwEnumStoreType stateMachineId) = 0;
                                 
                                 
    virtual  void ActiveStateMachines_S2_a2(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals signal, 
        const Fw::SMSignalBuffer &data) = 0;
                                 
                                                                  
};

class ActiveStateMachines_S2 {
                                 
  private:
    ActiveStateMachines_S2_Interface *parent;
                                 
  public:
                                 
    ActiveStateMachines_S2(ActiveStateMachines_S2_Interface* parent) : parent(parent) {}
  
    enum ActiveStateMachines_S2_States {
      ON,
      OFF,
    };
    
    enum ActiveStateMachines_S2_States state;

    void init(const FwEnumStoreType stateMachineId);
    void update(
        const FwEnumStoreType stateMachineId, 
        const ActiveStateMachines_S2_Interface::ActiveStateMachines_S2_Signals signal, 
        const Fw::SMSignalBuffer &data
    );
};

}

#endif
