import_pattern()
{
  run_test "" import_pattern && \
    compare S Time && \
    compare T Time && \
    compare_out import_pattern
}
