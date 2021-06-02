basic()
{
  run_test "-n basic.names.txt -p $PWD" basic && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
}

health()
{
  run_test "-p $PWD" health && \
    diff_cpp HealthTopology
}
