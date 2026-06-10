state machine SM {
  array A = [3] U32
  array B = [3] A
  state S
  initial enter S
}

array A = [3] SM.A
