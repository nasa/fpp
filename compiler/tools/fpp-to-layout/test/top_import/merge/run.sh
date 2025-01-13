import_merge()
{
  run_test "" import_merge && \
    compare S C && \
    compare T C && \
    compare_out import_merge
}
