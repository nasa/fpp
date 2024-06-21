
// ======================================================================
// \title  S2.h
// \author Auto-generated
// \brief  header file for state machine S2
//
// ======================================================================
           
#ifndef S2_H_
#define S2_H_

namespace Fw {
  class SMEvents;
}


class S2If {
  public:
                                                                  
};

class S2 {
                                 
  private:
    S2If *parent;
                                 
  public:
                                 
    S2(S2If* parent) : parent(parent) {}
  
    enum S2States {
      OFF,
      ON,
    };

    enum S2Events {
      RTI_SIG,
    };
    
    enum S2States state;

    void * extension;

    void init();
    void update(const Fw::SMEvents *e);

};


#endif
