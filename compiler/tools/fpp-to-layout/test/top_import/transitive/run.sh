import_transitive()
{
  run_test "" import_transitive && \
    compare M_A C && \
    compare M_B C && \
    compare M_C C && \
    compare M_T C && \
    compare_out import_transitive
}
