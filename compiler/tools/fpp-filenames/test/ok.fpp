port P

array A = [3] U32
constant a = 0
enum E { X, Y }
struct S { x: U32 }

module M {

  passive component C {
    array A = [3] U32
    constant a = 0
    enum E { X, Y }
    struct S { x: U32 }
  }

}

topology T { }
