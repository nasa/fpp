enum E1 { X = 0, Y = 1 }

@ Pre annotation
enum E2 {
  @ Pre annotation
  X @< Post annotation
  @ Pre annotation
  Y @< Post annotation
} @< Post annotation

@ Pre annotation
enum E3 {
  @ Pre annotation
  X, @< Post annotation
  @ Pre annotation
  Y, @< Post annotation
} @< Post annotation

enum E4 { x = 0, Y = 1 };

@ Pre annotation
enum E5 {
  @ Pre annotation
  X @< Post annotation
  @ Pre annotation
  Y @< Post annotation
}; @< Post annotation

@ Pre annotation
enum E6 {
  @ Pre annotation
  X, @< Post annotation
  @ Pre annotation
  Y, @< Post annotation
}; @< Post annotation
