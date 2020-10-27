array A = [3] U32
constant a = 0
enum E { X, Y }
struct S { x: U32 }
type T

module M {
  array A = [3] U32
  constant a = 0
  enum E { X, Y }
  struct S { x: U32 }
  type T
} 

port P

passive component C1 {
  array A = [3] U32
  constant a = 0
  enum E { X, Y }
  struct S { x: U32 }
}
