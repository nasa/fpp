.PS
S1: box width 2 height 1.5
"S1" with .nw at S1.nw + (0.1,-0.1)
S2: box width 0.75 height 0.5 with .sw at S1.sw + (0.1, 0.1)
"S2" with .nw at S2.nw + (0.1, -0.1)
S3: box width 0.75 height 0.5 with .ne at S1.ne + (-0.1, -0.1)
"S3" with .nw at S3.nw + (0.1, -0.1)
IL: line right 1.5 from S1.e invis
T1: arrow right 1.975 from S1.e "$A$" above
S4: box width 1.5 height 1 with .w at IL.e
"S4" with .nw at S4.nw + (0.1, -0.1)
S5: box width 0.75 height 0.5 with .c at S4.c + (0.1,-0.1)
"S5" with .nw at S5.nw + (0.1, -0.1)

line dashed down 0.25 from S2.s
line dashed right 3.875 "$\fRconcat\fI(\fRexit\fI(\fRS2\fI), \fRexit\fI(\fRS1\fI), A, \fRentry\fI(\fRS4\fI)$" below
arrow dashed up to S5.s

line dashed up 0.25 from S3.n
line dashed right 2.825 "$\fRconcat\fI(\fRexit\fI(\fRS3\fI), \fRexit\fI(\fRS1\fI), A, \fRentry\fI(\fRS4\fI)$" above
arrow dashed down to S5.n

.PE
