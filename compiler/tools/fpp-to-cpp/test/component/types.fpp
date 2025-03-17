@ An enum
enum E { X, Y, Z }

@ An array
array A = [3] U32

@ An array of arrays
array AA = [3] A

@ A struct
struct S { x: U32, y: string }

@ Alias of a primitive type
type AliasPrim1 = U32

@ Alias of another primtive type
type AliasPrim2 = F32

@ Alias of a struct
type AliasStruct = S

@ Alias of a boolean
type AliasBool = bool

@ Alias of an array
type AliasArray = A
type AliasAliasArray = AliasArray

@ Alias of an enum
type AliasEnum = E

@ Alias of a string
type AliasString = string size 32

struct StructWithAlias {
  x: U32,
  y: AliasString,
  z: AliasArray
  w: AliasAliasArray
}

type AnotherAliasStruct = StructWithAlias

module Ports {

  @ A typed port with no arguments
  port NoArgs

  @ A typed port with no arguments and a return type
  port NoArgsReturn -> U32

  @ A typed port with no arguments and a string return type
  port NoArgsStringReturn -> string

  @ A aliased typed port with no arguments and a aliased string return type
  port NoArgsAliasStringReturn -> AliasString

  @ A typed port
  port Typed(
    u32: U32, @< A U32
    f32: F32, @< An F32
    b: bool, @< A boolean
    str1: string, @< A string
    e: E, @< An enum
    a: A, @< An array
    s: S @< A struct
  )

  @ A typed port with a return type
  port TypedReturn(
    u32: U32, @< A U32
    f32: F32, @< An F32
    b: bool, @< A boolean
    str2: string, @< A string
    e: E, @< An enum
    a: AA, @< An array
    s: S @< A struct
  ) -> F32

  @ An aliased typed port
  port AliasTyped(
    u32: AliasPrim1, @< A primitive
    f32: AliasPrim2, @< Another primtive
    b: AliasBool, @< A boolean
    str2: AliasString, @< A string
    e: AliasEnum, @< An enum
    a: AliasArray, @< An array
    s: AliasStruct @< A struct
  )

  @ An aliased typed port with a return type
  port AliasTypedReturn(
    u32: AliasPrim1, @< A primitive
    f32: AliasPrim2, @< Another primtive
    b: AliasBool, @< A boolean
    str2: AliasString, @< A string
    e: AliasEnum, @< An enum
    a: AliasArray, @< An array
    s: AliasStruct @< A struct
  ) -> AliasPrim2

  @ An aliased typed port with a return type
  port AliasTypedReturnString(
    u32: AliasPrim1, @< A primitive
    f32: AliasPrim2, @< Another primtive
    b: AliasBool, @< A boolean
    str2: AliasString, @< A string
    e: AliasEnum, @< An enum
    a: AliasArray, @< An array
    s: AnotherAliasStruct @< A struct
  ) -> AliasString

}
