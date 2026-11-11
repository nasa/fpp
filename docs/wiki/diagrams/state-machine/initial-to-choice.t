.PS
S1: box width 2 height 1.5
"S1" with .nw at S1.nw + (0.1, -0.1)
arrow <- up 0.3 from (4/5)<S1.nw,S1.ne> " $A$" ljust
C: box invis width 0.1 height 0.1 with .c at S1.c
choice(C)
"C" with .w at C.e + (0.1,0)
arrow <- up 0.3 from C.n " $A'$" ljust

A: arrow dashed <- left 0.3 from C.w
L: line dashed up 1
"$\fRconcat\fI(A, \fRentry\fI(\fRS1\fI), A')$ " rjust with .e at L.n + (0, -0.1)
.PE
