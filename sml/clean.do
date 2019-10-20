# ----------------------------------------------------------------------
# clean.do: remove generated files
# ----------------------------------------------------------------------

. ./defs.sh

rm -rf bin
find . -mindepth 2 -name clean.do | sed 's/\.do$//' | xargs redo
rm_tmp
