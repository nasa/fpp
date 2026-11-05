# The absolute template name resolves to the template at the top level,
# not to the template M.T
locate template T at "template_expand_absolute_top.fpp"
locate template M.T at "template_expand_absolute_m.fpp"

module M {
  expand .T()
}
