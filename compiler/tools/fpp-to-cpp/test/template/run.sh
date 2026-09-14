# ----------------------------------------------------------------------
# Differential test helper
#
# Translate $model.fpp, which uses a module template, and
# $model'_hand'.fpp, which is the same model with the expansion written
# out by hand. Put the two sets of generated files in separate
# directories, and require that the directories be identical.
#
# Also check the names of the files generated from the template model
# against a reference. Otherwise the test would pass if both models
# generated nothing, which is exactly how the loss of the definitions
# contributed by an expansion went unnoticed.
# ----------------------------------------------------------------------
diff_expansion()
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
  rm -rf $expand_dir $hand_dir && \
    mkdir -p $expand_dir $hand_dir && \
    run_test "$mode -p $PWD -d $expand_dir -n $name.names.txt" $model $name && \
    run_test "$mode -p $PWD -d $hand_dir" $model'_hand' $name'_hand' && \
    diff -u $name.names.ref.txt $name.names.txt && \
    diff -r $expand_dir $hand_dir
}

constant_param()
{
  run_test "-p $PWD" constant_param && \
    diff_cpp_suffix FppConstants _constant_param
}

expansion()
{
  diff_expansion '' expansion
}

expansion_template()
{
  diff_expansion '-t' expansion expansion_template
}

expansion_test()
{
  diff_expansion '-u' expansion expansion_test
}

imports()
{
  rm -rf imports.out.dir && \
    mkdir -p imports.out.dir && \
    run_test "-p $PWD -d imports.out.dir -i imports_lib.fpp -n imports.names.txt" \
      imports_main imports && \
    diff -u imports.names.ref.txt imports.names.txt
}

primitive()
{
  diff_expansion '' primitive
}

two_expansions()
{
  run_test "-p $PWD" two_expansions && \
    diff_cpp_suffix FppConstants _two_expansions
}
