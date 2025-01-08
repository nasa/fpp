import_transitive()
{
  run_test "" import_transitive && \
    compare A C && \
    compare B C && \
    compare C C && \
    compare T C && \
    compare_out import_transitive
}
