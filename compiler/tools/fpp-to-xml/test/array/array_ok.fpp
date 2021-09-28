module M1 {
  array ArrayOK1 = [5] F32 default 1 format "{.03f}"
}

module M2 {
  array ArrayOK2 = [3] M1.ArrayOK1
}

array ArrayOK3 = [2] string default ["\"\\", """
abc
def
"""]

module M3 {
  array ArrayOK4 = [4] ArrayOK3 @< Array with array arg
}

type T
array ArrayOK5 = [3] T
