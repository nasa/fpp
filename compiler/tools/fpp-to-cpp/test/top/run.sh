basic()
{
  run_test "-i builtin.fpp -n basic.names.txt -p $PWD" basic && \
    diff -u basic.names.txt basic.names.ref.txt && \
    diff_cpp BasicTopology
}

commands()
{
  run_test "-i builtin.fpp -p $PWD" commands && \
    diff_cpp CommandsTopology
}

health()
{
  run_test "-p $PWD" health && \
    diff_cpp HealthTopology
}

params()
{
  run_test "-i builtin.fpp -p $PWD" params && \
    diff_cpp ParamsTopology
}

