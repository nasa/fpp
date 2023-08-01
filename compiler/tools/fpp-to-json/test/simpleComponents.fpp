@ A port for carrying an F32 value
port F32Value(value: F32)

@ A passive component for adding two F32 values
passive component PassiveF32Adder {

  @ Input 1
  sync input port f32ValueIn1: F32Value

  @ Input 2
  sync input port f32ValueIn2: F32Value

  @ Output
  output port f32ValueOut: F32Value

}

@ An active component for adding two F32 values
active component ActiveF32Adder {

  @ Input 1
  async input port f32ValueIn1: F32Value

  @ Input 2
  async input port f32ValueIn2: F32Value

  @ Output
  output port f32ValueOut: F32Value

}


@ An active component for adding two F32 values
@ Uses specified priorities
active component PriorityActiveF32Adder {

  @ Input 1 at priority 10
  async input port f32ValueIn1: F32Value priority 10 block

  @ Input 2 at priority 20
  async input port f32ValueIn2: F32Value priority 20 drop

  @ Output
  output port f32ValueOut: F32Value

}


@ Split factor
constant splitFactor = 10

@ Component for splitting a serial data stream
passive component SerialSplitter {

  @ Input
  sync input port serialIn: serial

  @ Output
  output port serialOut: [splitFactor] serial

}
