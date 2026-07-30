type U16Alias = U16
type T
type TAlias = T
type SAlias = string size 50
type SAliasZero = string size 0
struct AliasType {
    x: U16Alias,
    y: TAlias,
    z: [10] SAlias
    z0: [10] SAliasZero
}
