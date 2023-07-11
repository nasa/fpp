component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

echo "generating C++"

#fpp-to-cpp -d $fprime_dir/Fw -p $fprime_dir `cat ../deps.txt`
#for dir in Cmd Log Prm Time Tlm
#do
#  mv $fprime_dir/Fw/$dir*.hpp $fprime_dir/Fw/$dir*.cpp $fprime_dir/Fw/$dir
#done
#mv $fprime_dir/Fw/Param*.hpp $fprime_dir/Fw/Param*.cpp $fprime_dir/Fw/Prm

# Generate F Prime C++ files into $fprime_dir/Fw
fpp-to-cpp -d $fprime_dir/Fw -p $fprime_dir `cat ../deps.txt`
# Move files into place by name prefix
for dir in Buffer Cmd Dp Log Prm Time Tlm
do
  mv $fprime_dir/Fw/$dir*.hpp $fprime_dir/Fw/$dir*.cpp $fprime_dir/Fw/$dir
done
mv $fprime_dir/Fw/Param*.hpp $fprime_dir/Fw/Param*.cpp $fprime_dir/Fw/Prm
# Remaining files go into types
mv $fprime_dir/Fw/*.hpp $fprime_dir/Fw/*.cpp $fprime_dir/Fw/Types
