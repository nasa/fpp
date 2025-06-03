@ Interface definition
interface I {

  @ General port instance specifier
  sync input port p: [10] P priority 10 assert
  @< General port instance specifier

  @ Special port instance specifier
  command recv port cmdIn
  @< Special port instance specifier

  @ Interface import specifier
  import J
  @< Interface import specifier

}
@< Interface definition
