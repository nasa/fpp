array A = [3] U32

constant a = 0

dictionary constant a2 = 1

enum E { X, Y, Z }

type T

type Alias = T

dictionary type Alias2 = U32

struct S { x: U32 }

port P

interface I {
  sync input port P: P
}

passive component C {
  type T
  type Alias = T
  array A = [3] U32
  dictionary array A2 = [3] U32
  constant a = 0
  enum E { X, Y, Z }
  struct S { x: U32 }
  import I
}

instance c: C base id 0x100

topology T { 

}
