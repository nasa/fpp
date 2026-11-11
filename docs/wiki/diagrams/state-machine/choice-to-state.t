.PS
S1: box width 1 height 0.75
"S1" with .nw at S1.nw + (0.1, -0.1)
C: box invis width 0.1 height 0.1 with .c at S1.c
choice(C)
"C" with .w at C.w + (-0.1,0)
IL: line right 1.5 from C.e invis
T1: arrow right 1.975 from C.e "$A$" above
S2: box width 1.5 height 1 with .w at IL.e
"S2" with .nw at S2.nw + (0.1, -0.1)
S3: box width 0.75 height 0.5 with .c at S2.c + (0.1,-0.1)
"S3" with .nw at S3.nw + (0.1, -0.1)

line dashed down 0.75 from C.s
L: line dashed right 2.4 "$\fRconcat\fI(\fRexit\fI(S1), A, \fRentry\fI(\fRS2\fI))$" below
arrow dashed from L.e to S3.s
.PE
