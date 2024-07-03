module Fw {

  type Time

  @ Time port
  port Time(
             ref $time: Fw.Time @< The U32 cmd argument
           )

}

module Fw {

  type CmdArgBuffer
  type FwOpcodeType

  @ Command registration port
  port CmdReg(
               opCode: FwOpcodeType @< Command Op Code
             )

  @ Port for sending commands
  port Cmd(
            opCode: FwOpcodeType @< Command Op Code
            cmdSeq: U32 @< Command Sequence
            ref args: CmdArgBuffer @< Buffer containing arguments
          )

  @ Enum representing a command response
  enum CmdResponse {
    OK = 0 @< Command successfully executed
    INVALID_OPCODE = 1 @< Invalid opcode dispatched
    VALIDATION_ERROR = 2 @< Command failed validation
    FORMAT_ERROR = 3 @< Command failed to deserialize
    EXECUTION_ERROR = 4 @< Command had execution error
    BUSY = 5 @< Component busy
  }

  @ Port for sending command responses
  port CmdResponse(
                    opCode: FwOpcodeType @< Command Op Code
                    cmdSeq: U32 @< Command Sequence
                    response: CmdResponse @< The command response argument
                  )

}

module Fw {

  type LogBuffer
  type TextLogString
  type FwEventIdType

  @ Enum representing event severity
  enum LogSeverity {
    FATAL = 1 @< A fatal non-recoverable event
    WARNING_HI = 2 @< A serious but recoverable event
    WARNING_LO = 3 @< A less serious but recoverable event
    COMMAND = 4 @< An activity related to commanding
    ACTIVITY_HI = 5 @< Important informational events
    ACTIVITY_LO = 6 @< Less important informational events
    DIAGNOSTIC = 7 @< Software diagnostic events
  }

  @ Event log port
  port Log(
            $id: FwEventIdType @< Log ID
            ref timeTag: Fw.Time @< Time Tag
            $severity: LogSeverity @< The severity argument
            ref args: LogBuffer @< Buffer containing serialized log entry
          )


  @ Text event log port
  @ Use for development and debugging, turn off for flight
  port LogText(
                $id: FwEventIdType @< Log ID
                ref timeTag: Fw.Time @< Time Tag
                $severity: LogSeverity @< The severity argument
                ref $text: Fw.TextLogString @< Text of log message
              )

}

module Svc {

  @ Port for pinging active components
  port Ping(
             key: U32 @< Value to return to pinger
           )

}

module Fw {

  type ParamBuffer
  type FwPrmIdType

  @ Enum representing parameter validity
  enum ParamValid {
    UNINIT = 0
    VALID = 1
    INVALID = 2
    DEFAULT = 3
  }

  @ Port for getting a parameter
  port PrmGet(
               $id: FwPrmIdType @< Parameter ID
               ref val: ParamBuffer @< Buffer containing serialized parameter value
             ) -> ParamValid

  @ Port for setting a parameter
  port PrmSet(
               $id: FwPrmIdType @< Parameter ID
               ref val: ParamBuffer @< Buffer containing serialized parameter value
             )

}

module Fw {

  type TlmBuffer
  type FwChanIdType

  @ Port for sending telemetry
  port Tlm(
            $id: FwChanIdType @< Telemetry Channel ID
            ref timeTag: Fw.Time @< Time Tag
            ref val: TlmBuffer @< Buffer containing serialized telemetry value
          )

  @ Port for getting telemetry
  port TlmGet(
               $id: FwChanIdType @< Telemetry Channel ID
               ref timeTag: Fw.Time @< Time Tag
               ref val: Fw.TlmBuffer @< Buffer containing serialized telemetry value
             )

}