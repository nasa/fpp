basic()
{
  update "-i config.fpp -p 1.0.0 -f 3.4.3" basic
  move_json Basic
  move_system_json M_Basic
}

dataProducts()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3" dataProducts
  move_json BasicDp
  move_system_json FppTest_BasicDp
}

dictionaryDefs()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" dictionaryDefs
  move_json DictionaryDefs
  move_system_json DictionaryDefs
}

duplicate()
{
  update '-i config.fpp' duplicate
}

invalidDictDefConstant()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" invalidDictDefConstant
}

invalidDictDefType()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" invalidDictDefType
}

multipleTops()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" multipleTops
  move_json FirstTop
  move_json SecondTop
}

unqualifiedComponentInstances()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" unqualifiedComponentInstances
  move_json QualifiedCompInst
  move_json UnqualifiedCompInst
  move_system_json UnqualifiedCompInst
}

