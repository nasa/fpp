multipleTops()
{
  run_test "-i builtin.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" multipleTops && \
    validate_json_schema FirstTop && \
    validate_json_schema SecondTop && \
    diff_json FirstTop && \
    diff_json SecondTop
}

dataProducts()
{
  run_test "-i builtin.fpp -p 1.0.0 -f 3.4.3" dataProducts && \
    validate_json_schema BasicDp && \
    diff_json BasicDp
}

duplicate()
{
  run_test '' duplicate && \
    compare duplicate
}

unqualifiedComponentInstances()
{
  run_test "-i builtin.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" unqualifiedComponentInstances && \
    validate_json_schema QualifiedCompInst && \
    validate_json_schema UnqualifiedCompInst && \
    diff_json QualifiedCompInst && \
    diff_json UnqualifiedCompInst
}
