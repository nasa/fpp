# A template expansion depends on the file that defines the template, even
# when the template definition is located in a separate file.
locate template T at "template_locate_def.fpp"

module M {
  expand T(constant 10)
}
