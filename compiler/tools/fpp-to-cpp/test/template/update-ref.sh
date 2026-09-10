update_expansion()
{
  mode=$1
  model=$2
  if test -n "$3"
  then
    name=$3
  else
    name=$model
  fi
  expand_dir=$name.expand.out.dir
  hand_dir=$name.hand.out.dir
  rm -rf $expand_dir $hand_dir
  mkdir -p $expand_dir $hand_dir
  update "$mode -p $PWD -d $expand_dir -n $name.names.txt" $model $name
  mv $name.names.txt $name.names.ref.txt
  update "$mode -p $PWD -d $hand_dir" $model'_hand' $name'_hand'
}

constant_param()
{
  update "-p $PWD" constant_param
  move_cpp_suffix FppConstants _constant_param
}

expansion()
{
  update_expansion '' expansion
}

expansion_template()
{
  update_expansion '-t' expansion expansion_template
}

expansion_test()
{
  update_expansion '-u' expansion expansion_test
}

imports()
{
  rm -rf imports.out.dir
  mkdir -p imports.out.dir
  update "-p $PWD -d imports.out.dir -i imports_lib.fpp -n imports.names.txt" \
    imports_main imports
  mv imports.names.txt imports.names.ref.txt
}

primitive()
{
  update_expansion '' primitive
}

two_expansions()
{
  update "-p $PWD" two_expansions
  move_cpp_suffix FppConstants _two_expansions
}
