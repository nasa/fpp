default()
{
  run_test '' default && \
    diff DefaultEnumAi.xml DefaultEnumAi.ref.xml
}

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

serialize_type()
{
  run_test '' serialize_type && \
    diff SerializeTypeEnumAi.xml SerializeTypeEnumAi.ref.xml
}
