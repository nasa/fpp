.PS
S1: box width 2 height 1.5
"S1" with .nw at S1.nw + (0.1,-0.1)
S2: box width 0.75 height 0.5 with .sw at S1.sw + (0.1, 0.1)
"S2" with .nw at S2.nw + (0.1, -0.1)
S3: box width 0.75 height 0.5 with .ne at S1.ne + (-0.1, -0.1)
"S3" with .nw at S3.nw + (0.1, -0.1)
line right 0.5 from 1/3<S1.ne,S1.se>
line down 0.5 " $A$" ljust
arrow left 0.5

line dashed up 0.3 from S3.n
line dashed left 1 "$\fRconcat\fI(\fRexit\fI(\fRS3\fI), \fRexit\fI(\fRS1\fI), A))$" above
arrow dashed down 0.195

line dashed down 0.3 from S2.s
line dashed right 1 "$\fRconcat\fI(\fRexit\fI(\fRS2\fI), \fRexit\fI(\fRS1\fI), A))$" below
arrow dashed up 0.195
.PE
