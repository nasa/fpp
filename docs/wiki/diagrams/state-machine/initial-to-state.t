.PS
S1: box width 2 height 1.5
"S1" with .nw at S1.nw + (0.1, -0.1)
arrow <- up 0.3 from (3/5)<S1.nw,S1.ne> " $A$" ljust
S2: box width 1 height 0.75 with .c at S1.c + (0.1,-0.1)
"S2" with .nw at S2.nw + (0.1, -0.1)
arrow <- up 0.3 from (4/5)<S2.nw,S2.ne> " $A'$" ljust

arrow dashed <- up 0.9 from (2/5)<S2.nw,S2.ne>
"$\fRconcat\fI(A, \fRentry\fI(\fRS1\fI)$, " rjust "$A', \fRentry\fI(\fRS2\fI))$ " rjust with .e at S1.n + (0, 0.2)
.PE
