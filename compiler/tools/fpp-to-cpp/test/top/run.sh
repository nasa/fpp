basic()
{
  run_test "-n basic.names.txt -p $PWD" basic && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
}
