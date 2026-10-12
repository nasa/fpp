.PS
S1: box width 2.5 height 1.5
"S1" with .nw at S1.nw + (0.1,-0.1)
S2: box width 0.75 height 0.5 with .sw at S1.sw + (0.25, 0.25)
"S2" with .nw at S2.nw + (0.1, -0.1)
S3: box width 0.75 height 0.5 with .ne at S1.ne + (-0.25, -0.25)
"S3" with .nw at S3.nw + (0.1, -0.1)
arrow left 0.5 from 3/4<S1.ne,S1.se> "$A$" above

arrow dashed left 0.5 from 2/3<S3.ne,S3.se> "$A$" above
arrow dashed left 0.5 from 2/3<S2.ne,S2.se> "$A$" above

.PE
