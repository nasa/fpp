state S1 {
    entry do { a1, a2 }
    exit do { a1, a2 }

    @ Initial transition
    initial do { a1, a2 } enter S3

    @ Choice C
    choice C { if g1 do { a1, a2 } enter S1 else do { a2, a3 } enter S2.S3 }
}
