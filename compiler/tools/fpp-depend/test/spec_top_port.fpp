locate instance i1 at "instances.fpp"
locate instance i2 at "instances.fpp"

topology T1 {
  instance i1
  instance i2

  port p1 = i1.p
  port p2 = i2.p
}
