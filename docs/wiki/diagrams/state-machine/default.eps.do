exec 1>&2

redo-ifchange $2.ps
cp $2.ps $2.tmp.ps
ps2eps $2.tmp.ps
mv $2.tmp.eps $3
