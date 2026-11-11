@ A port for the interface
port P

@ An interface with one output port and one input port
interface I {
  output port pOut: P
  sync input port pIn: P
}

@ A component that implements interface I
passive component C {
  import I
}

instance c1: C base id 0x100
instance c2: C base id 0x200
instance c3: C base id 0x300
instance c4: C base id 0x400

@ A topology that implements interface I by exporting the ports of c3.
@ It is used below as an interface template argument, which makes the
@ analysis output contain an InterfaceTemplateArg whose bound instance
@ is an InterfaceTopology.
topology SubTop implements I {

  instance c3

  @ Export the output port of c3 as the output port of the topology
  port pOut = c3.pOut

  @ Export the input port of c3 as the input port of the topology
  port pIn = c3.pIn

}

@ A module template with one parameter of each kind
module template T(
  constant n: U32, @< A constant parameter
  type Ty, @< A type parameter
  instance x: I @< An interface parameter
) {

  @ A constant that uses the constant parameter.
  @ In the expanded AST this appears twice, once per expansion, with the
  @ same source location but different expandingLoc values.
  constant nPlusOne = n + 1

  @ An array type that uses the type parameter
  array A = [3] Ty

  @ A topology that uses the interface parameter.
  @ The instance specifier for x becomes an InterfaceTemplateArg in the
  @ instance map of this topology in the analysis output.
  topology Top {
    instance x
    instance c4
    connections C {
      x.pOut -> c4.pIn
    }
  }

}

@ An expansion of T that binds the interface parameter to a component
@ instance. The analysis output must show
@ InterfaceTemplateArg { paramDef { name x }, ii InterfaceComponentInstance }
@ in the instance map of MInstArg.Top.
module MInstArg {
  expand T(constant 10, type U32, instance c1)
}

@ An expansion of T that binds the interface parameter to a topology.
@ The analysis output must show
@ InterfaceTemplateArg { paramDef { name x }, ii InterfaceTopology }
@ in the instance map of MTopArg.Top. The two InterfaceTemplateArg keys
@ differ only in the bound instance, so this also pins that the two
@ expansions produce two distinct instance map entries.
module MTopArg {
  expand T(constant 20, type F32, instance SubTop)
}

@ The deployment topology named by the system definition
deployment topology Dep {
  instance c1
  instance c2
  connections C {
    c1.pOut -> c2.pIn
  }
}

@ The system definition. There may be at most one per model, so the
@ systemMap in the analysis output must have exactly one entry.
system Sys: Dep
