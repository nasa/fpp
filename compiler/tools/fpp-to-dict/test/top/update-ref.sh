multipleTops()
{
  update "-i builtin.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" multipleTops
  move_json FirstTop
  move_json SecondTop
}

dataProducts()
{
  update "-i builtin.fpp -p 1.0.0 -f 3.4.3" dataProducts
  move_json BasicDp
}

unqualifiedComponentInstances()
{
  update "-i builtin.fpp -p 1.0.0 -f 3.4.3 -l lib1-1.0.0,lib2-2.0.0" unqualifiedComponentInstances
  move_json QualifiedCompInst
  move_json UnqualifiedCompInst
}
