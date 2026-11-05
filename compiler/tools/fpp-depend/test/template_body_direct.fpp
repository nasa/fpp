# The uses in a template body are direct dependencies of the file that
# defines the template, whether or not the template is expanded
locate component TBodyDirectC at "template_body_direct_comp.fpp"
locate constant tBodyDirectConst at "template_body_direct_const.fpp"

module template TBodyDirect() {
  instance c: TBodyDirectC base id 0x100
  constant c = tBodyDirectConst
}
