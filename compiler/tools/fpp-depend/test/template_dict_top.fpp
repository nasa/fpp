# The deployment topology appears only in an unexpanded module template body,
# so there should be no dictionary dependencies

locate dictionary constant c at "dictionary_c.fpp"
locate dictionary type T1 at "dictionary_T1.fpp"

module template DictT(constant p: U32) {
  deployment topology DictTop { }
}
