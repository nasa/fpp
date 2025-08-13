duplicate()
{
  run_test '' duplicate && \
    compare M_Duplicate C && \
    compare Duplicate C && \
    compare_out duplicate
}
