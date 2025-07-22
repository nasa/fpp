import_port_num()
{
  run_test "" import_port_num && \
    compare M_S C && \
    compare M_T C && \
    compare_out import_port_num
}
