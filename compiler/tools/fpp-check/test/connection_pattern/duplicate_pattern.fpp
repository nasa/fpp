passive component Health {

}
instance $health: Health base id 0x100
topology T {
  health connections instance $health
  health connections instance $health
}
