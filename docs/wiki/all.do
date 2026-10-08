for file in `find . -name '*.asciidoc' | grep -v '\.tmp\.asciidoc'`
do
  echo $file | sed 's/\.asciidoc$/.html/'
done | xargs redo-ifchange
