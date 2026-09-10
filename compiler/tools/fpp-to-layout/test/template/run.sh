# template.fpp puts a topology in the body of a module template.
# template_hand.fpp is the same model with the expansion written out by hand.
# The two must produce the same layout, so both are checked against the same
# reference output.
template()
{
  run_test "" template && \
    compare M_Top C && \
    compare_out template
}

template_hand()
{
  run_test "" template_hand && \
    compare M_Top C && \
    compare_out template_hand
}
