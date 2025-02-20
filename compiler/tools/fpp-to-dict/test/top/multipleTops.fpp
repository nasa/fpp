module Module1 {

  @ Alias of type U32
  type AliasT1 = U32
  @ Alias of type AliasT1 (with underlying type U32)
  type AliasT2 = AliasT1
  type AliasT3 = string size 20

  # enums
  enum E1: U32 {
    X = 0
    Y = 1
    Z = 2
  } default X

  enum E2 {
    PASS
    FAIL
  } default PASS

  enum E3: U8 {
    YES
    NO
    MAYBE
  }

  # arrays
  @ An array of 3 enum values
  array EnumArray = [3] E3 default [E3.YES, E3.YES, E3.YES]

  @ An array of 3 I32 values
  array I32x3 = [3] I32

  @ An array of 4 U32 values
  array U32x4 = [4] U32 default [1, 2, 3, 4]

  @ An array of 2 String values
  array StringArray = [2] string default ["A", "B"]

  @ An array of 4 F64 values
  array F64x4 = [4] F64

  # structs
  struct S1 {
    x: U64 format "The value of x is {}"
    y: F32 format "The value of y is {.2f}"
  } default { x = 1, y = 1.5 }

  struct S2 {
    x: E2,
    y: EnumArray
  } default { x = E2.PASS }

  struct ScalarStruct {
    i8: I8,
    i16: I16,
    i32: I32,
    i64: I64,
    u8: U8,
    u16: U16,
    u32: U32,
    u64: U64,
    f32: F32,
    f64: F64
  }

  # Component
  active component Component1 { 

    # Commands
    @ Command with I32 arg
    async command Command1(
      a: I32 @< description for argument a
    ) priority 10 drop

    @ Command with string arg
    sync command Command2(
      a: string size 16
    )

    @ Command with 2 args (array of strings and U32)
    sync command Command3(
      a: Module1.StringArray  @< description for argument a
      b: U32  @< description for argument b
    )

    @ Command with no args
    sync command Command4
  
    # Parameters
    @ Parameter (struct)
    param Param1: S1 \
      default { x = 2, y = 1.5 } \
      id 0x01 \

    @ Parameter of type array (with 4 U32 values)
    param Param2: U32x4 \
      id 0x02 \
      set opcode 0x82 \
      save opcode 0x83

    @ Parameter of type string
    param Param3: AliasT3

    @ Parameter of type F32
    param Param4: F32

    @ Parameter of type enum
    param Param5: E1

    # Events
    @ Event with array arg (containing 4 F32 values)
    event Event1(
      arg1: F64x4 @< description of arg1 formal param
    ) \
      severity activity high \
      format "Arg one is {} and there is no arg 2"

    @ Event with enum arg
    event Event2(
      arg1: E3 @< description of arg1 formal param
    ) \
      severity activity high \
      format "Arg1 is {}"

    @ Event with format specifier
    @ Multiple lines of annotation
    @ And not used on purpose
    event Event3(
      arg1: F64 @< description of arg1 formal param
    ) \
      severity activity high \
      format "Arg1 is {f}"


    # Telemtry Channels
    @ Telemetry channel of type F64 with high and low limits
    telemetry TlmChannel1: F64 \
      low { yellow -1.1, orange -2.2, red -3.3 } \
      high { yellow 1.1, orange 2.2, red 3.3 }

    @ Telemetry channel of type F32
    telemetry TlmChannel2: F32

    @ Telemetry channel of type U64
    telemetry TlmChannel3: U64

    # Records
    @ Record with single U32 value
    product record Record1: AliasT2

    @ Record with a single F64x4 value
    product record Record2: F64x4

    @ Record with array of F32 values
    product record Record3: F32 array

    # Containers
    @ Description of Container 1
    product container Container1 id 0x10

    @ Description Container 2
    product container Container2

    @ Description Container 3 with a default priority of 10
    product container Container3 default priority 10

    # Ports
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

    @ Port for sending events to downlink
    event port logOut

    @ Port for sending textual representation of events
    text event port logTextOut

    @ Port for sending telemetry channels to downlink
    telemetry port tlmOut

    @ Product get port
    product get port productGetOut

    @ A port for sending data products
    product send port productSendOut

    @ A port for getting the current time
    time get port timeGetOut
    
  }

  passive component Component2 { 
    
    # Commands
    @ Command with 1 arg (of type struct)
    sync command Command1(a: S2)

    @ Command with 3 args (of types string, I32, and bool)
    sync command Command2(a: string, b: I32, c: bool)

    @ Send scalars
    sync command SendScalars(s: ScalarStruct)

    # Events
    @ Event with a single U64 arg
    event Event1(
      arg1: U64 @< description of arg1 formal param
    ) \
      severity warning low \
      format "Arg one is {}"

    # Telemtry Channels
    @ Telemetry channel of type U32 with no high/low limits
    telemetry TlmChannel1: U32

    @ Telemetry channel of type F64 with low limits
    telemetry TlmChannel2: F64 \
      low { yellow -1.1, orange -2.2, red -3.3 } \
    
    @ Telemetry channel of type F32 with high limits
    telemetry TlmChannel3: F64 \
      high { yellow 1.0, orange 2.0, red 3.0 } \

    # Ports
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

  instance myFirstC1: Component1 base id 0x300 \
    queue size 10
    
  instance myFirstC2: Component2 base id 0x400

  instance mySecondC1: Component1 base id 0x500 \
    queue size 10

  instance mySecondC2: Component2 base id 0x600

  topology FirstTop { 

    instance myFirstC1
    instance myFirstC2

    connections C {
      myFirstC1.pOut -> myFirstC2.pIn
    }

    telemetry packets Packets {
      packet MyTlmPacket1 id 0 group 0 {
        Module1.myFirstC1.TlmChannel1
        Module1.myFirstC1.TlmChannel2
      }

      packet MyTlmPacket2 id 1 group 1 {
        Module1.myFirstC2.TlmChannel1
      }
    } omit {
      Module1.myFirstC2.TlmChannel2
      Module1.myFirstC1.TlmChannel3
      Module1.myFirstC2.TlmChannel3
    }
  }

  topology SecondTop { 

      instance mySecondC1
      instance mySecondC2

      connections C {
        mySecondC1.pOut -> mySecondC2.pIn
      }

  }

}
