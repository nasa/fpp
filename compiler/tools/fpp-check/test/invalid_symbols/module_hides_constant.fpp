# a is a constant symbol here
constant a = 0
module M {
  # M.a is a module symbol here.
  # It hides the constant symbol a defined in the outer scope.
  constant b = a
  module a { }
}
