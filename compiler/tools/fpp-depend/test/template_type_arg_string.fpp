locate type FwSizeStoreType at "template_type_arg_string_dep.fpp"
locate constant FW_FIXED_LENGTH_STRING_SIZE at "template_type_arg_string_dep.fpp"

module template StringArgT(type U) {
  struct StringArgS { s: U }
}

module M {
  expand StringArgT(type string)
}
