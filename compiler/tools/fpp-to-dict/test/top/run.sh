basic()
{
  run_test "-i config.fpp -p 1.0.0 -f 3.4.3" basic && \
    validate_json_schema Basic && \
    diff_json Basic && \
    diff_system_json M_Basic
}

dataProducts()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3" dataProducts && \
    validate_json_schema BasicDp && \
    diff_json BasicDp && \
    diff_system_json FppTest_BasicDp
}

dictionaryDefs()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" dictionaryDefs && \
    validate_json_schema DictionaryDefs && \
    diff_json DictionaryDefs && \
    diff_system_json DictionaryDefs
}

duplicate()
{
  run_test '-i config.fpp' duplicate && \
    compare duplicate
}

invalidDictDefConstant()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" invalidDictDefConstant && \
    compare invalidDictDefConstant
}

invalidDictDefType()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" invalidDictDefType && \
    compare invalidDictDefType
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

multipleTops()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" multipleTops && \
    validate_json_schema FirstTop && \
    validate_json_schema SecondTop && \
    diff_json FirstTop && \
    diff_json SecondTop
}

unqualifiedComponentInstances()
{
  run_test "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" unqualifiedComponentInstances && \
    validate_json_schema QualifiedCompInst && \
    validate_json_schema UnqualifiedCompInst && \
    diff_json QualifiedCompInst && \
    diff_json UnqualifiedCompInst && \
    diff_system_json UnqualifiedCompInst
}

