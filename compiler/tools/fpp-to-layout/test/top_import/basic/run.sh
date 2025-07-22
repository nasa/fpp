import_basic()
{
  run_test "" import_basic && \
    compare M_S C1 && \
    compare M_T C2 && \
    compare_out import_basic
}
