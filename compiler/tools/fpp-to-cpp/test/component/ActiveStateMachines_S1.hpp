
// ======================================================================
// \title  ActiveStateMachines_S1.h
// \author Auto-generated
// \brief  header file for state machine ActiveStateMachines_S1
//
// ======================================================================
           
#ifndef ACTIVESTATEMACHINES_S1_H_
#define ACTIVESTATEMACHINES_S1_H_

namespace Fw {
  class SMSignals;
}

namespace Fw {

class ActiveStateMachines_S1_Interface {
  public:
    virtual void ActiveStateMachines_S1_turnLedOn() = 0;
    virtual void ActiveStateMachines_S1_turnLedOff() = 0;
    virtual void ActiveStateMachines_S1_blinkLed() = 0;
                                                                  
};

class ActiveStateMachines_S1 {
                                 
  private:
    ActiveStateMachines_S1_Interface *parent;
                                 
  public:
                                 
    ActiveStateMachines_S1(ActiveStateMachines_S1_Interface* parent) : parent(parent) {}
  
    enum ActiveStateMachines_S1States {
      ON,
      OFF,
      WAITING,
    };

    enum ActiveStateMachines_S1Events {
      RTI_SIG,
      WAIT_SIG,
    };
    
    enum ActiveStateMachines_S1States state;

    void * extension;

    void init();
    void update(const Fw::SMSignals *e);

};

}

#endif
