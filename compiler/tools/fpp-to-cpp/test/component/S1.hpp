
// ======================================================================
// \title  ActiveStateMachines_S1.h
// \author Auto-generated by STARS
// \brief  header file for state machine ActiveStateMachines_S1
//
// ======================================================================
           
#ifndef ActiveStateMachines_S1_H_
#define ActiveStateMachines_S1_H_

namespace Fw {
  class SMEvents;
}

class ActiveStateMachines_S1If {
  public:
                                                                  
};

class ActiveStateMachines_S1 {
                                 
  private:
    ActiveStateMachines_S1If *parent;
                                 
  public:
                                 
    ActiveStateMachines_S1(ActiveStateMachines_S1If* parent) : parent(parent) {}
  
    enum ActiveStateMachines_S1States {
      OFF,
      ON,
    };

    enum ActiveStateMachines_S1Events {
      RTI_SIG,
    };
    
    enum ActiveStateMachines_S1States state;

    void * extension;

    void init();
    void update(const Fw::SMEvents *e);

};


#endif
