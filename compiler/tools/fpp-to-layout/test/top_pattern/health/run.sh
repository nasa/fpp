pattern_health()
{
  run_test "" pattern_health && \
    compare TPatternHealth Health && \
    compare_out pattern_health
}
