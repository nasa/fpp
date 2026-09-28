@ Implied by any string type name
type FwSizeStoreType = U16

@ Implied by a string type name with the size omitted
constant FW_FIXED_LENGTH_STRING_SIZE = 80

module template T(type Ty) {

  @ Array of strings from a template type parameter
  array StrArr = [2] Ty

  @ Struct with a string member from a template type parameter
  struct StrStruct {
    s: Ty
  }

}

module M {
  expand T(type string)
}
