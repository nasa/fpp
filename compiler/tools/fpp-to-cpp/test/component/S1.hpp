
// ======================================================================
// \title  S1.h
// \author Auto-generated
// \brief  header file for state machine S1
//
// ======================================================================
           
#ifndef S1_H_
#define S1_H_

namespace Fw {
  class SMEvents;
}

class S1If {
  public:
                                                                  
};

class S1 {
                                 
  private:
    S1If *parent;
                                 
  public:
                                 
    S1(S1If* parent) : parent(parent) {}
  
    enum S1States {
      OFF,
      ON,
    };

    enum S1Events {
      RTI_SIG,
    };
    
    enum S1States state;

    void * extension;

    void init();
    void update(const Fw::SMEvents *e);

};


#endif
