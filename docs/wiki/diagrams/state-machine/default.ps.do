exec 1>&2

redo-ifchange preamble.t $2.t
cat preamble.t $2.t | pic | eqn | groff > $3
