import_merge()
{
  run_test "" import_merge && \
    compare M_S C && \
    compare M_T C && \
    compare_out import_merge
}
