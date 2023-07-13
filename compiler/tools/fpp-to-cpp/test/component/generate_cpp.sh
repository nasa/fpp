component_dir=`dirname $PWD`
fprime_dir=`dirname $component_dir`/fprime

echo "generating C++"
fpp-to-cpp -d $fprime_dir/Fw -p $fprime_dir `cat ../deps.txt`
for dir in Cmd Log Prm Time Tlm
do
  mv $fprime_dir/Fw/$dir*.hpp $fprime_dir/Fw/$dir*.cpp $fprime_dir/Fw/$dir
done
mv $fprime_dir/Fw/Param*.hpp $fprime_dir/Fw/Param*.cpp $fprime_dir/Fw/Prm
