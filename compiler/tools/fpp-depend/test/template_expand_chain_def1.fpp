locate template T2 at "template_expand_chain_def2.fpp"

module template T1() {
  struct ChainS1 { x: U32 }
}

struct ChainD1 { x: U32 }

module N {
  expand T2()
}
