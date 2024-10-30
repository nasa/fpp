mutliple_tops()
{
  run_test "" multiple_tops && \
    compare T1 C1 && \
    compare T1 C2 && \
    compare T2 C
}

pattern()
{
  run_test "" pattern && \
    compare TPattern Health
}

direct()
{
  run_test '' direct && \
    compare TDirect C
}
