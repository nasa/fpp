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

# Remove local path prefix
remove_path_prefix()
{
  local_path_prefix=`cd ../../../..; echo $PWD`
  sed "s;$local_path_prefix;[ local path prefix ];"
}

# Run a test
run()
{
  printf '%-60s' $1
  $@
  status=$?
  if test $status -eq 0
  then
    echo_green PASSED
  else
    echo_red FAILED
  fi
  return $status
}

# Run a test suite
run_suite()
{

  tests=$@

  num_passed=0
  num_failed=0

  for t in $tests
  do
    if run $t
    then
      num_passed=`expr $num_passed + 1`
    else
      num_failed=`expr $num_failed + 1`
    fi
  done

  printf "$num_passed passed"
  if test $num_failed -gt 0
  then
    printf ", $num_failed failed"
  fi
  echo

  exit $num_failed

}

# Remove generated files
clean()
{
  for file in `find . -name '*.out.txt' -or -name '*.diff.txt' -or -name '*~'`
  do
    rm $file
  done
}
