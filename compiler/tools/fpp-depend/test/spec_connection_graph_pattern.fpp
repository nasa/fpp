locate instance a at "a.fpp"
locate instance b at "b.fpp"
locate type Pattern at "Pattern.fpp"

topology T {

  connections instance a { b } pattern Pattern.COMMAND

}
