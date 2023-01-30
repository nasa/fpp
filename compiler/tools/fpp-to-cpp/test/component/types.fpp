@ An enum
enum E { X, Y, Z }

@ An array
array A = [3] U32

@ A struct
struct S { x: U32, y: string }

@ A typed port
port Typed(
  u32: U32,
  f32: F32,
  b: bool,
  str: string,
  e: E,
  a: A,
  s: S
)
