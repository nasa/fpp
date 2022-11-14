enum E { X, Y, Z }

array A = [3] U32

struct S { x: U32, y: string }

@ A port with FPP type parameters
port FppType(
  e: E @< An enum
  ref eRef: E @< An enum ref
  a: A @< An array
  ref aRef: A @< An array ref
  s: S @< A struct
  ref sRef: S @< A struct ref
)
