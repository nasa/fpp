struct S1 {
  x : U32
  y : F32
}

@ Pre annotation
struct S2 {
  @ Pre annotation
  x : U32 @< Post annotation
  @ Pre annotation
  y : F32 @< Post annotation
} default {
  x = 1
  y = 2
} @< Post annotation

@ Pre annotation
struct S2 {
  @ Pre annotation
  x : U32, @< Post annotation
  @ Pre annotation
  y : F32 @< Post annotation
} default {
  x = 1,
  y = 2
} @< Post annotation


struct S4 {
  x : U32
  y : F32
};

@ Pre annotation
struct S5 {
  @ Pre annotation
  x : U32 @< Post annotation
  @ Pre annotation
  y : F32 @< Post annotation
} default {
  x = 1
  y = 2
}; @< Post annotation

@ Pre annotation
struct S6 {
  @ Pre annotation
  x : U32, @< Post annotation
  @ Pre annotation
  y : F32 @< Post annotation
} default {
  x = 1,
  y = 2
}; @< Post annotation

