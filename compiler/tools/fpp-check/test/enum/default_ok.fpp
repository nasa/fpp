enum Status { YES, NO, MAYBE } default MAYBE

module M {
  enum Status { YES, NO, MAYBE } default MAYBE
}

passive component C {
  enum Status { YES, NO, MAYBE } default MAYBE
}
