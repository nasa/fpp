multipleTops()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" multipleTops
  move_json FirstTop
  move_json SecondTop
}

basic()
{
  update "-i config.fpp -p 1.0.0 -f 3.4.3" basic
  move_json Basic
}

dataProducts()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3" dataProducts
  move_json BasicDp
}

duplicate()
{
  update '-i config.fpp' duplicate
}

unqualifiedComponentInstances()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" unqualifiedComponentInstances
  move_json QualifiedCompInst
  move_json UnqualifiedCompInst
}

inconsistentSpecLoc()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" inconsistentSpecLoc
}

incorrectSpecLoc()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" incorrectSpecLoc
}

invalidDictDefConstant()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" invalidDictDefConstant
}

invalidDictDefType()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" invalidDictDefType
}

dictionaryDefs()
{
  update "-i builtin.fpp,config.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" dictionaryDefs
  move_json DictionaryDefs
}
