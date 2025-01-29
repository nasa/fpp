multiple_tops()
{
  run_test "" multiple_tops && \
    compare T1 C1 && \
    compare T1 C2 && \
    compare T2 C3 && \
    compare_out multiple_tops
}
