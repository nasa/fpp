direct()
{
  run_test '' direct && \
    compare TDirect C && \
    compare_out direct
}

duplicate()
{
  run_test '' duplicate && \
    compare_out duplicate
}

multiple_tops()
{
  run_test "" multiple_tops && \
    compare T1 C1 && \
    compare T1 C2 && \
    compare T2 C3 && \
    compare_out multiple_tops
}

pattern()
{
  run_test "" pattern && \
    compare TPattern Health && \
    compare_out pattern
}