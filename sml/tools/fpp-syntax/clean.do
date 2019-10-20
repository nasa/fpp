# ----------------------------------------------------------------------
# clean.do: remove generated files
# ----------------------------------------------------------------------

. ./defs.sh

dir=`pwd`
name=`basename $dir`
for file in `ls $name* | egrep -v '\.(cm|do|sml)$'`
do
  rm $file
done
rm_tmp
