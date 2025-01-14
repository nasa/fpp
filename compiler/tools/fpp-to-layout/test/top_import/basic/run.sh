import_basic()
{
  run_test "" import_basic && \
    compare S C1 && \
    compare T C2 && \
    compare_out import_basic
}
