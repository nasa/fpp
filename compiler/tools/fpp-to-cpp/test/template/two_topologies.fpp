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

module template T(instance src: IO, instance snk: II) {

  @ Subtopology from a template, wired through both instance parameters
  topology Sub {
    instance src
    instance snk
    connections C {
      src.pOut -> snk.pIn
    }
  }

}

module M1 {
  expand T(instance a1, instance s1)
}

module M2 {
  expand T(instance a2, instance s2)
}

@ Deployment topology importing both expansions of the same template
deployment topology TwoSubs {
  import M1.Sub
  import M2.Sub
}
