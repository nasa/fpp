import_pattern()
{
  run_test "" import_pattern && \
    compare M_S Time && \
    compare M_T Time && \
    compare_out import_pattern
}
