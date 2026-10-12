
for file in `find . -name '*.pdf' -or -name '*.ps' -or -name '*.eps' -or -name '*~'`
do
  rm $file
done
