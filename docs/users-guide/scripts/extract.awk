#!/usr/bin/env awk -f

# ----------------------------------------------------------------------
# extract.awk
# ----------------------------------------------------------------------

BEGIN {
  if (mode == "") mode = "ok"
  if (mode != "ok" && mode != "err") {
    print "extract.awk: invalid mode " mode > "/dev/stderr"
    exit 1
  }
  OUTSIDE = 0
  HEADER = 1
  BODY = 2
  state = outside
  num_lines = 0
  start_line = 0
}

state == OUTSIDE && /\[source,fpp\]/ { 
  start_line = NR
  state = HEADER
  next 
}

state == HEADER && $1 ~ "^----" { 
  if (mode == "ok" && length($1) == 4)
    state = BODY
  else if (mode == "err" && length($1) > 4)
    state = BODY
  else state = OUTSIDE
  next 
}

state == BODY && $1 ~ "^----" {
  path = path_prefix start_line ".fpp"
  printf("") > path
  for (i = 1; i <= num_lines; ++i)
    print lines[i] >> path
  close(path)
  state = OUTSIDE
  num_lines = 0
  next
}

state == BODY {
  lines[++num_lines] = $0
  next
}

