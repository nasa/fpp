exec 1>&2

redo-ifchange $2.eps
epspdf $2.eps $2.tmp.pdf
mv $2.tmp.pdf $3
