for file in `find . -name '*.pdf' -or -name '*~'`
do
  rm $file
done
