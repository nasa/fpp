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

# Compare the topology implementation generated from the template model
# against a reference. $1 is the directory that diff_expansion created
# for the template model and $2 is the unqualified topology name.
diff_topology()
{
  dir=$1
  top=$2
  diff -u $top'TopologyAc.ref.hpp' $dir/$top'TopologyAc.hpp' && \
    diff -u $top'TopologyAc.ref.cpp' $dir/$top'TopologyAc.cpp'
}

constant_param()
{
  run_test "-p $PWD" constant_param && \
    diff_cpp_suffix FppConstants _constant_param
}

enum_state_machine()
{
  diff_expansion '' enum_state_machine
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

string_type_arg()
{
  diff_expansion '' string_type_arg
}

topology_instance_param()
{
  diff_expansion '' topology_instance_param && \
    diff_topology topology_instance_param.expand.out.dir InstParam
}

topology_subtopology_param()
{
  diff_expansion '' topology_subtopology_param && \
    diff_topology topology_subtopology_param.expand.out.dir SubParam
}

two_expansions()
{
  run_test "-p $PWD" two_expansions && \
    diff_cpp_suffix FppConstants _two_expansions
}

two_topologies()
{
  diff_expansion '' two_topologies && \
    diff_topology two_topologies.expand.out.dir TwoSubs
}
