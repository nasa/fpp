duplicate_port_num()
{
  run_test "" duplicate_port_num && \
    compare_out duplicate_port_num
}

numbering_unmatched()
{
  run_test "" numbering_unmatched && \
    compare M_T Case1 && \
    compare M_T Case2 && \
    compare M_T Case3 && \
    compare M_T Case4 && \
    compare M_T Case5 && \
    compare_out numbering_unmatched
}
