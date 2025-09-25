numbering_general()
{
  run_test "" numbering_general && \
    compare M_T C && \
    compare_out numbering_general
}
