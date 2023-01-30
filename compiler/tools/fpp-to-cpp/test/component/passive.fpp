@ A passive component
passive component Passive {

  # ----------------------------------------------------------------------
  # General ports
  # ----------------------------------------------------------------------

  @ A typed sync input port
  sync input port typedSync: [3] Typed

  @ A typed guarded input
  guarded input port typedGuarded: Typed

  @ A typed output port
  output port typedOut: Typed

  @ A serial sync input port
  sync input port serialSync: serial

  @ A serial guarded input
  guarded input port serialGuarded: serial

  @ A serial output port
  output port serialOut: [5] serial

  # ----------------------------------------------------------------------
  # Special ports
  # ----------------------------------------------------------------------

  @ A port for receiving commands
  command recv port cmdIn

  @ A port for sending command registration requests
  command reg port cmdRegOut

  @ A port for sending command responses
  command resp port cmdResponseOut

  @ A port for emitting events
  event port eventOut

  @ A port for emitting text events
  text event port textEventOut

  @ A port for emitting telemetry
  telemetry port tlmOut

  @ A port for getting parameter values
  param get port prmGetOut

  @ A port for setting parameter values
  param set port prmSetOut

  @ A port for getting the time
  time get port timeGetOut

  # ----------------------------------------------------------------------
  # Commands
  # ----------------------------------------------------------------------

  @ A sync command with no params
  sync command CMD_SYNC

  @ An async command with primitive params
  sync command CMD_SYNC_PRIMITIVE(
    u32: U32, @< A U32
    f32: F32, @< An F32
    b: bool @< A boolean
  ) opcode 0x10

  @ A sync command with string params
  sync command CMD_SYNC_STRING(
    str1: string, @< A string
    str2: string size 100 @< Another string
  )

  @ A sync command with enum params
  sync command CMD_ASYNC_ENUM(
    e: E @< An enum
  )

  @ A sync command with array params
  sync command CMD_SYNC_ARRAY(
    a: A @< An array
  )

  @ A sync command with struct params
  sync command CMD_SYNC_STRUCT(
    s: S @< A struct
  )

  # ----------------------------------------------------------------------
  # Events
  # ----------------------------------------------------------------------

  @ An activity high event with no params
  event EventActivityHigh \
    severity activity high \
    format "Event Activity High occurred"

  @ An activity low, throttled event with primitive params
  event EventActivityLowThrottled(
    u32: U32, @< A U32
    f32: F32, @< An F32
    b: bool @< A boolean
  ) \
    severity activity low \
    id 0x10 \
    format "Event Activity Low occurred with arguments: {}, {}, {}" \
    throttle 5

  @ A command event with string params
  event EventCommand (
    str1: string, @< A string
    str2: string size 100 @< Another string
  ) \
    severity command \
    format "Event Command occurred with arguments: {}, {}"

  @ A diagnostic event with enum params
  event EventDiagnostic(
    e: E @< An enum
  ) \
    severity diagnostic \
    format "Event Diagnostic occurred with argument: {}"

  @ A fatal, throttled event with array params
  event EventFatalThrottled (
    a: A @< An array
  ) \
    severity fatal \
    format "Event Fatal occurred with argument: {}" \
    throttle 10

  @ A warning high event with struct params
  event EventWarningHigh (
    s: S @< A struct
  ) \
    severity warning high \
    id 0x20 \
    format "Event Warning High occurred with argument: {}"

  @ A warning low, throttled event with no params
  event EventWarningLowThrottled \
    severity warning low \
    format "Event Warning Low occurred" \
    throttle 10

  # ----------------------------------------------------------------------
  # Telemetry
  # ----------------------------------------------------------------------

  @ A telemetry channel with U32 data and format string
  telemetry ChannelU32Format: U32 \
    format "{x}"

  @ A telemetry channel with F32 data and format string
  telemetry ChannelF32Format: F32 \
    format "{.3f}"

  @ A telemetry channel with string data with format string
  telemetry ChannelStringFormat: string \
    format "{}"

  @ A telemetry channel with enum data
  telemetry ChannelEnum: E \
    id 0x10

  @ A telemetry channel with array data and update frequency
  telemetry ChannelArrayFreq: A \
    update on change

  @ A telemetry channel with struct data
  telemetry ChannelStructFreq: S \
    update always

  @ A telemetry channel with U32 data and limits
  telemetry ChannelU32Limits: U32 \
    low { red 0, orange 1, yellow 2 }

  @ A telemetry channel with F32 data and limits
  telemetry ChannelF32Limits: F32 \
    low { red -3, orange -2, yellow -1 } \
    high { red 3, orange 2, yellow 1 }

  @ A telemetry channel F64 data, update frequency, format, and limits
  telemetry ChannelF64: F64 \
    update always \
    format "{e}" \
    high { red 3, orange 2, yellow 1 }

  # ----------------------------------------------------------------------
  # Parameters
  # ----------------------------------------------------------------------

  @ A parameter with U32 data
  param ParamU32: U32

  @ A parameter with F64 data
  param ParamF64: F64

  @ A parameter with string data and default value
  param ParamString: string \
    default "default"

  @ A parameter with enum data
  param ParamEnum: E \
    id 0x30

  @ A parameter with array data, default value, and save opcode
  param ParamArray: A \
    default [ 1.0, 2.0, 3.0 ] \
    save opcode 0x35

  @ A parameter with struct data and set/save opcodes
  param ParamStruct: S \
    set opcode 0x40 \
    save opcode 0x45

}
