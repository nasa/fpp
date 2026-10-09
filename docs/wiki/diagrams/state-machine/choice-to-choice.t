.PS
S1: box width 1 height 0.75
"S1" with .nw at S1.nw + (0.1, -0.1)
C1: box invis width 0.1 height 0.1 with .c at S1.c
choice(C1)
"C1" with .w at C1.w + (-0.1,0)
S2: box width 1.5 height 1 with .w at S1.e + (1,0)
"S2" with .nw at S2.nw + (0.1, -0.1)
C2: box invis width 0.1 height 0.1 with .c at S2.c
choice(C2)
"C2" with .w at C2.e + (0.1,0)
T1: arrow from C1.e to C2.w "$A$" above
line dashed down 0.75 from C1.s
line dashed right 2.25 "$\fRconcat\fI(\fRexit\fI(\fRS1\fI), A, \fRentry\fI(\fRS2\fI))$" below
arrow dashed up to C2.s
.PE
