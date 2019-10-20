# ----------------------------------------------------------------------
# default.do 
# ----------------------------------------------------------------------

. ./defs.sh

if ! echo $2 | grep -q '^bin/'
then
  echo 'no rule to make '$2 1>&2
  exit 1
fi

split "$@"

mkdir -p $dir
ml-build src/$base.cm FPPCheck.main $2 1>&2
suffix=`find $dir -name $base'.*-*' | sed 's/^.*\.//'`

echo '#!/bin/sh

# ----------------------------------------------------------------------
# '$base'
# ----------------------------------------------------------------------

$SMLNJ_HOME/bin/.run/run.'$suffix' @SMLcmdname='$base' @SMLload='`pwd`/$2.$suffix' "$@"' > $3

chmod +x $3
