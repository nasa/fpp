module M1 {
  constant a = 0
}

@ Pre annotation
module M2 {
  constant a = 0
} @< Post annotation

module M3 {
  constant a = 0
};

@ Pre annotation
module M3 {
  constant a = 0
}; @< Post annotation
