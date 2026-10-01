port P

passive component Worker {
  output port pOut: P
  sync input port pIn: P
  sync input port spare: P
}

passive component Hub {
  output port hOut: [2] P
  sync input port hIn: [2] P
}

instance w1: Worker base id 0x100
instance w2: Worker base id 0x200
instance hub: Hub base id 0x300

interface WorkerI {
  output port pOut: P
  sync input port pIn: P
}

module template T(instance w: WorkerI, constant idx: U32) {
  topology Sub {
    instance w
    instance hub
    connections C {
      w.pOut -> hub.hIn[idx]
      hub.hOut[idx] -> w.pIn
    }
  }
}

module E1 { expand T(instance w1, constant 0) }
module E2 { expand T(instance w2, constant 1) }

deployment topology Dep {
  import E1.Sub
  import E2.Sub
}
