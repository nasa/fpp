port P

@ The interface required of the source parameter
interface IO {
  output port pOut: P
}

@ The interface required of the sink parameter
interface II {
  sync input port pIn: P
}

passive component CO {
  output port pOut: P
}

passive component CI {
  sync input port pIn: P
}

instance a1: CO base id 0x100
instance a2: CO base id 0x200
instance s1: CI base id 0x300
instance s2: CI base id 0x400

module M1 {

  @ Subtopology from a template, wired through both instance parameters
  topology Sub {
    instance a1
    instance s1
    connections C {
      a1.pOut -> s1.pIn
    }
  }

}

module M2 {

  @ Subtopology from a template, wired through both instance parameters
  topology Sub {
    instance a2
    instance s2
    connections C {
      a2.pOut -> s2.pIn
    }
  }

}

@ Deployment topology importing both expansions of the same template
deployment topology TwoSubs {
  import M1.Sub
  import M2.Sub
}
