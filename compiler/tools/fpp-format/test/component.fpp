@ Component definition
active component C {

  type T
  array A = [3] U32
  struct S { x: U32, y: F32, z: string }
  enum E { X, Y, Z }

  @ Command specifier
  async command C(a: U32, b: F32) opcode 0x00 priority 10 assert
  @< Command specifier

  @ Parameter specifier
  param P: U32 default 0 id 0x00 set opcode 0x01 save opcode 0x02
  @< Parameter specifier

  @ General port instance specifier
  sync input port p: [10] P priority 10 assert
  @< General port instance specifier

  @ Special port instance specifier
  command recv port cmdIn
  @< Special port instance specifier

  @ Telemetry channel specifier
  telemetry T: U32 id 0x00 update on change format "{} s" \
    low { red 0, orange 1, yellow 2 } \
    high { yellow 10, orange 11, red 12 }
  @< Telemetry channel specifier

  @ Event specifier
  event E(a: U32, b: F32) severity activity low id 0x00 format "{} counts" throttle 10
  @< Event specifier

  @ Internal port specifier
  internal port I(a: U32, b: F32) priority 10 assert
  @< Internal port specifier

}
@< Component definition
