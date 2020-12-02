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
