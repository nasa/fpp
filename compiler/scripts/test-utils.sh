#!/bin/sh

export NO_COLOR='\033[0m'
export BOLD='\033[1m'
export RED='\033[31m'
export GREEN='\033[32m'

echo_green()
{
  echo "${GREEN}${BOLD}$@${NO_COLOR}"
}

echo_red()
{
  echo "${RED}${BOLD}$@${NO_COLOR}"
}

# Run a test
run()
{
  printf '%-60s' $1
  $@
  if test $? -eq 0
  then
    echo_green PASSED
  else
    echo_red FAILED
  fi
}

# Remove local path prefix
remove_path_prefix()
{
  local_path_prefix=`cd ../../../..; echo $PWD`
  sed "s;$local_path_prefix;[ local path prefix ];"
}
