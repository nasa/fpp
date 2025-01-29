duplicate_port_num()
{
  run_test "" duplicate_port_num && \
    compare_out duplicate_port_num
}

numbering_unmatched()
{
  run_test "" numbering_unmatched && \
    compare T Case1 && \
    compare T Case2 && \
    compare T Case3 && \
    compare T Case4 && \
    compare T Case5 && \
    compare_out numbering_unmatched
}
