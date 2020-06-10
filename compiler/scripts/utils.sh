#!/bin/sh

# Remove generated files
clean()
{
  echo "cleaning $PWD"
  rm -f test-output.txt
  for file in `find . -name '*.out.txt' -or -name '*.diff.txt' -or -name '*~'`
  do
    rm $file
  done
}
