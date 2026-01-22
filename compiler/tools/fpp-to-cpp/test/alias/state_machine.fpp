state machine SM {
  # Provide a minimal state machine
  state S
  initial enter S
  # Provide an alias to generate the header
  type X = U32
  # Provide a use of the alias to compile the header
  array A = [3] X
}
