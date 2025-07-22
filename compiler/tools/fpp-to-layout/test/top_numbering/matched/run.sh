numbering_matched()
{
  run_test "" numbering_matched && \
    compare M_T C && \
    compare_out numbering_matched
}
