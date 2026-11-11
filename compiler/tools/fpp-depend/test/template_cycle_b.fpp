# This file expands CycleT, so it depends on the file that defines CycleT
locate template CycleT at "template_cycle_a.fpp"

constant cycleConst = 1

module M {
  expand CycleT(constant 2)
}
