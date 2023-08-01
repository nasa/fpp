constant numElements = 3
array A = [numElements] string size 40 default [ "1", "2", "3" ] format "{} RPM"
array B = [3] A

@ This is a pre annotation for struct S
struct S {
  @ This is a pre annotation for member x
  x: U32 @< This is a post annotation for member x
  @ This is a pre annotation for member y
  y: string @< This is a post annotation for member y
} @< This is a post annotation for struct S

constant s = { x = 1, y = "abc" }
struct S1 { x: U8, y: string } default s
struct S2 { x: U32, y: string } default s

type T # T is an abstract type
array arr = [3] T # A is an array of 3 values of type T
