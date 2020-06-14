module A {

  module B {

    @ Annotation line 1
    @ Annotation line 2
    enum OK {
      X @< Annotation line 1
        @< Annotation line 2
      Y
      Z
    }

  }

}
