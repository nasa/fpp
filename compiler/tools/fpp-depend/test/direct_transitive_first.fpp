# The use of a is visited first and makes direct_transitive_first_b.fpp a
# transitive dependency. The use of b also makes it a direct dependency.
locate constant a at "direct_transitive_first_a.fpp"
locate constant b at "direct_transitive_first_b.fpp"

constant c1 = a
constant c2 = b
