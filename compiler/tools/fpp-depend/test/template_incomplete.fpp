# The template is defined in another file. fpp-depend loads that file to
# expand the template, so the generated files include the files that the
# expansion generates.
locate template IncompleteT at "template_incomplete_def.fpp"

array IncompleteArr = [3] U32

module M {
  expand IncompleteT(constant 3)
}
