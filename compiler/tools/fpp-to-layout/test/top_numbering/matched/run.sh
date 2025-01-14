numbering_matched()
{
  run_test "" numbering_matched && \
    compare T C && \
    compare_out numbering_matched
}
