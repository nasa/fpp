@ A port with string parameters of duplicate sizes
port String(
  str80: string, @< A string of size 80
  ref str80Ref: string,
  str100: string size 100, @< A string of size 100
  ref str100Ref: string size 100
)

@ A port with string parameters
port String2(
  str80: string, @< A string of size 80
  str100: string size 100, @< A string of size 100
)
