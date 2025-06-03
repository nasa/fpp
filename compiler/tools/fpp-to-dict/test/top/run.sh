multipleTops()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" multipleTops && \
    validate_json_schema FirstTop && \
    validate_json_schema SecondTop && \
    diff_json FirstTop && \
    diff_json SecondTop
}

basic()
{
  run_test "-i config.fpp -p 1.0.0 -f 3.4.3" basic && \
    validate_json_schema Basic && \
    diff_json Basic
}

dataProducts()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3" dataProducts && \
    validate_json_schema BasicDp && \
    diff_json BasicDp
}

duplicate()
{
  run_test '-i config.fpp' duplicate && \
    compare duplicate
}

missingFwOpcodeType()
{
  run_test '' missingFwOpcodeType && \
    compare missingFwOpcodeType
}

missingUserDataSizeConstant()
{
  run_test '' missingUserDataSizeConstant && \
    compare missingUserDataSizeConstant
}


arrayFwEventType()
{
  run_test '' arrayFwEventType && \
    compare arrayFwEventType
}

floatFwEventType()
{
  run_test '' floatFwEventType && \
    compare floatFwEventType
}

unqualifiedComponentInstances()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" unqualifiedComponentInstances && \
    validate_json_schema QualifiedCompInst && \
    validate_json_schema UnqualifiedCompInst && \
    diff_json QualifiedCompInst && \
    diff_json UnqualifiedCompInst
}
