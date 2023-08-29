# ----------------------------------------------------------------------
# Generate C++ for compiling component code
# ----------------------------------------------------------------------
#
dir=`pwd`
component_dir=`dirname $dir`
if ! test `basename $component_dir` = component
then
  echo 'generate_cpp.sh must be run from a child of the component directory' 1>&2
  exit 1
fi
fprime_dir=`dirname $component_dir`/fprime

echo "generating C++"

# Generate F Prime C++ files into $fprime_dir/Fw
fpp-to-cpp -d $fprime_dir/Fw -p $fprime_dir `cat $component_dir/deps.txt`
# Move data product files into place
for base in FppConstantsAc ProcIdEnumAc
do
  for suffix in hpp cpp
  do
    mv $fprime_dir/Fw/$base.$suffix $fprime_dir/config
  done
done
# Move files into place by name prefix
for dir in Buffer Cmd Dp Log Prm Time Tlm
do
  mv $fprime_dir/Fw/$dir*.hpp $fprime_dir/Fw/$dir*.cpp $fprime_dir/Fw/$dir
done
mv $fprime_dir/Fw/Param*.hpp $fprime_dir/Fw/Param*.cpp $fprime_dir/Fw/Prm
# Remaining files go into types
mv $fprime_dir/Fw/*.hpp $fprime_dir/Fw/*.cpp $fprime_dir/Fw/Types
