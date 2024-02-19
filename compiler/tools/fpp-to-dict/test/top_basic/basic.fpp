module Ports {
  port P
}

module FppTest {

    @ A component for testing  data product code gen
    active component DpTest {

        output port pOut: Ports.P

        @ Product get port
        product get port productGetOut

        @ A port for sending data products
        product send port productSendOut

        @ A port for getting the current time
        time get port timeGetOut

        @ Port for sending command registrations
        command reg port cmdRegOut

        @ Port for receiving commands
        command recv port cmdIn

        @ Port for sending command responses
        command resp port cmdResponseOut
  
      # ----------------------------------------------------------------------
      # Types
      # ----------------------------------------------------------------------

        @ Command 1
        async command COMMAND_1(
          a: bool @< description for arguement a
        ) priority 10

        @ Data for a DataRecord
        struct Data {
          @ A U16 field
          u16Field: U16
        }

        @ A single U32 value
        product record Record0: Data


        @ Description of Container 0
        product container Container0

        @ Container 1
        product container Container1 id 0x02

        @ Container 2
        @ Implied id is 0x03
        product container Container2 default priority 10
    }
}

module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
  port PrmGet
  port PrmSet
  port Log
  port LogText
  port Time
  port Tlm
  port DpGet
  port DpSet
  port DpRequest
  port DpResponse
  port DpSend
}

module M {

  enum E1: U32 {
    X = 0
    Y = 1
    Z = 2
  } default X

  enum E2 {
    PASS
    FAIL
  } default PASS

  enum U8Gunfighters: U8 {
    IL_BUONO
    IL_BRUTTO
    IL_CATTIVO
  }

  enum Status: U32 {
    YES
    NO
    MAYBE
  } default YES

  # struct MyStruct1 {
  #   x: [3] U32,
  #   y: B,
  #   z: string size 16 format "The string is {}"
  # } default { z = "hello world"}


  struct A {
    x: U64 format "The value of x is {}"
    y: F32
  } default { x = 1, y = 1.5 }

  @ An array of 3 enum values
  array EnumArray = [3] Status default [Status.YES, Status.YES, Status.YES]

  # @ An array of 3 struct values
  array MyArray = [3] U64

  @ An array of 3 I32 values
  array I32x3 = [3] I32

  @ An array of 4 U32 values
  array U32x4 = [4] U32 default [1, 2, 3, 4]

  @ An array of 2 F64 values
  array F64x2 = [2] F64

  @ An array of 3 F64 values
  array F64x3 = [3] F64

  @ An array of 4 F64 values
  array F64x4 = [4] F64

  @ An array of 2 String values
  array StringArray = [2] string default ["A", "B"]

  # Defines an array type A of 3 U8 elements with default value [ 0, 0, 0 ]
  # array A = [3] U8

  # Defines an array type B of 2 A elements with default value
  # [ [ 0, 0, 0 ], [ 0, 0, 0 ] ]
  # array B = [3] A

  active component C1 { 

    @ Command 1
    async command COMMAND_1(
      a: bool @< description for arguement a
    ) priority 10

    @ Command 2
    sync command COMMAND_2(a: string size 20)

    # @ My parameter 1
    # param PARAM_1: Status default Status.YES

    @ My parameter 2
    param PARAM_1: MyArray

    @ My parameter 1
    param PARAM_3: U32x4

    @ Parameter 3
    @ Its set opcode is 0x12
    @ Its save opcode is 0x20
    param PARAM_4: F32

    @ Parameter 5
    param PARAM_5: E2

    @ Port for sending command registrations
    command reg port cmdRegOut

    @ Port for receiving commands
    command recv port cmdIn

    @ Port for sending command responses
    command resp port cmdResponseOut
    
    output port pOut: Ports.P

    @ Port to return the value of a parameter
    param get port prmGetOut

    @Port to set the value of a parameter
    param set port prmSetOut

    @ Product get port
    product get port productGetOut

    @ A port for sending data products
    product send port productSendOut

    @ A port for getting the current time
    time get port timeGetOut

    @ A single U32 value
    product record Record0: U32

    @ Record 1: A single F64x3 value
    @ Implied id is 0x03
    product record Record1: F64x3

    @ Description of Container 0
    product container Container0

    @ Container 1
    product container Container1 id 0x02

    @ Container 2
    @ Implied id is 0x03
    product container Container2 default priority 10
  }

  passive component C2 { 

    @ Event logged when the LED blink interval is updated
    event Event1(
      arg1: F64x4 @< description of arg1 formal param
    ) \
      severity activity high \
      format "Arg one is {} and there is no arg 2"

    @ Telemetry channel counting LED transitions
    telemetry LedTransitions: I64 \
      low { yellow -1, orange -2, red -3 } \
      high { yellow 1, orange 2, red 3 }

    @ Telemetry channel counting LED transitions
    telemetry MyTlmChannel1: I32x3 \

    @ Command 1
    sync command COMMAND_1(a: string)

    @ Command 2
    sync command COMMAND_2(a: string)

    @ Port for sending command registrations
    command reg port cmdRegOut

    @ Port for receiving commands
    command recv port cmdIn

    @ Port for sending command responses
    command resp port cmdResponseOut

    sync input port pIn: Ports.P

    @ Port for sending events to downlink
    event port logOut

    @ Port for sending textual representation of events
    text event port logTextOut

    @ Port for requesting the current time
    time get port timeCaller

    @ Port for sending telemetry channels to downlink
    telemetry port tlmOut
  }

  # instance c1: FppTest.DpTest base id 0x100
  # instance c2: C2 base id 0x200
  instance c1: C1 base id 0x300
  instance c2: C2 base id 0x400

  topology T { 

    instance c1
    instance c2

    connections C {
      c1.pOut -> c2.pIn
    }

  }

  # topology T2 { 

  #   instance MySecondC1
  #   instance MySecondC2

  #   connections C {
  #     MySecondC1.pOut -> MySecondC2.pIn
  #   }

  # }

}
