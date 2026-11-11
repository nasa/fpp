redo-ifchange $2.t
eqn -Tpdf < $2.t | groff -ms -Tpdf > $3
