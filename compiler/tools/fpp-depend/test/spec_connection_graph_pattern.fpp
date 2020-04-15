locate instance a at "a.fpp"
locate instance b at "b.fpp"

enum Pattern {
  COMMAND
}

topology T {

  connections instance a { b } pattern Pattern.COMMAND

}
