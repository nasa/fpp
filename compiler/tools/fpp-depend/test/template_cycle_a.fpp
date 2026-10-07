# The template body uses cycleConst, so this file depends on the file
# that defines cycleConst. That file expands the template, so it depends
# on this file.
locate constant cycleConst at "template_cycle_b.fpp"

module template CycleT(constant p: U32) {
  constant cOut = p + cycleConst
}
