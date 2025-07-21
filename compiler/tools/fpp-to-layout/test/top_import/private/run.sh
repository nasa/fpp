import_private()
{
  run_test "" import_private && \
    compare M_S C && \
    compare M_T C && \
    compare_out import_private
}
