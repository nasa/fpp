for file in `find . -name '*.html' -or -name '*~' -or -name '*.bak'`
do
  rm $file
done
