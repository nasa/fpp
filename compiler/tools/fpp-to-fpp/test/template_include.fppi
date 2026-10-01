@ A module template defined in an included file
module template Included(
    constant n: U32 @< A constant parameter
) {
    @ A constant defined by the included template
    constant m = n + 1
}
