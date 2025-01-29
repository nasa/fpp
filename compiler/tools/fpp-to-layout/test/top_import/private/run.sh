import_private()
{
  run_test "" import_private && \
    compare S C && \
    compare T C && \
    compare_out import_private
}
