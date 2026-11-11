redo-ifchange $2.asciidoc diagrams/state-machine/*.png
title=`echo $2 | sed 's/-/ /g'`
{
  echo "# $title"
  echo ""
  sed 's;https://github.com/nasa/fpp/wiki/\([^[#]*\);link:\1.html;g' < $2.asciidoc 
} > $2.tmp.asciidoc
asciidoctor $2.tmp.asciidoc -o $3
rm $2.tmp.asciidoc
