#!/bin/sh

# Remove generated files
clean()
{
  echo "cleaning $PWD"
  rm -f num_failed.txt test-output.txt
  for file in `find . -name '*.out.txt' -or -name '*.diff.txt' -or -name '*~' -or -name 'default-*sh'`
  do
    rm $file
  done
}
