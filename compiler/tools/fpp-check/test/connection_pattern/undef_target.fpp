passive component Health {

}
passive component C {

}
instance $health: Health base id 0x100
topology T {
  health connections instance $health {
    c
  }
}
