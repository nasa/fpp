@ A data type T
type T

@ A component that sends data to itself on an internal port,
@ with priority and queue full behavior
active component InternalSelfMessage {

  @ An internal port for sending data of type T
  internal port pInternal(t: T) priority 10 drop

}
