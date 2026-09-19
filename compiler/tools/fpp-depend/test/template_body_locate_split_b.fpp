# Expanding the template here makes its body a source of dependencies,
# even though the body is in another file.
module M {
  expand BodyLocSplitT(constant 1)
}
