import_port_num()
{
  run_test "" import_port_num && \
    compare S C && \
    compare T C && \
    compare_out import_port_num
}
