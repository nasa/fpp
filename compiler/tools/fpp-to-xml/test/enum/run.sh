explicit()
{
  run_test '' explicit && \
    diff ExplicitEnumAi.xml ExplicitEnumAi.ref.xml
}

implicit()
{
  run_test '' implicit && \
    diff ImplicitEnumAi.xml ImplicitEnumAi.ref.xml
}

default()
{
  run_test '' default && \
    diff DefaultEnumAi.xml DefaultEnumAi.ref.xml
}
