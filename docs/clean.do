# ----------------------------------------------------------------------
# clean.do
# ----------------------------------------------------------------------

. ./defs.sh

rm -rf fpp-spec
find . -mindepth 2 -maxdepth 2 -name clean.do | sed 's/\.do$//' | xargs redo
for file in `find . -name '*.tar' -or -name '*.zip'`
do
  rm $file
done
rm_tmp
