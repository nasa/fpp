
// ======================================================================
// \title  ActiveStateMachines_S2.h
// \author Auto-generated
// \brief  header file for state machine ActiveStateMachines_S2
//
// ======================================================================
           
#ifndef ACTIVESTATEMACHINES_S2_H_
#define ACTIVESTATEMACHINES_S2_H_

namespace Fw {
  class SMSignals;
}

namespace Fw {

class ActiveStateMachines_S2_Interface {
  public:
    virtual void ActiveStateMachines_S2_turnLedOn() = 0;
    virtual void ActiveStateMachines_S2_turnLedOff() = 0;
                                                                  
};

class ActiveStateMachines_S2 {
                                 
  private:
    ActiveStateMachines_S2_Interface *parent;
                                 
  public:
                                 
    ActiveStateMachines_S2(ActiveStateMachines_S2_Interface* parent) : parent(parent) {}
  
    enum ActiveStateMachines_S2States {
      ON,
      OFF,
    };

    enum ActiveStateMachines_S2Events {
      RTI_SIG,
    };
    
    enum ActiveStateMachines_S2States state;

    void * extension;

    void init();
    void update(const Fw::SMSignals *e);

};

}

#endif
