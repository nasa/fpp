.EQ
delim $$
include "/usr/share/misc/eqnchar"
.EN

.PS
define choice {
  line from $1.n to $1.e to $1.s to $1.w to $1.n
}
.PE
