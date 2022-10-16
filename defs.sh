# ----------------------------------------------------------------------
# defs.sh: definitions common to all subdirectories
# ----------------------------------------------------------------------

# Users of this file should set LEVEL first
if test -z "$LEVEL"; then
  export LEVEL=.
fi

export LEVEL=`cd $LEVEL; echo $PWD`

redo-ifchange $LEVEL/defs.sh

# Pick up the FPP version
. $LEVEL/version.sh

# Echo args to stderr
echoerr()
{
  echo $@ 1>&2
}

# Do command on all files matching regex
doall()
{
  if ! test "$#" = 2; then
    echoerr 'usage: doall command regex'
    return 1
  fi
  for file in `find . -mindepth 1 -maxdepth 1 -name "$2"`; do
    $1 $file
  done
}

# Split path into dir and base
split() 
{
  export dir=`dirname $2`
  export base=`basename $2`
}

# Remove temporary files
rm_tmp()
{
  doall rm '*~'
  doall rm '*redo*tmp'
}
