#!/usr/bin/env awk -f

# ----------------------------------------------------------------------
# extract.awk
# ----------------------------------------------------------------------

BEGIN {
  if (path_prefix == "") path_prefix = "unknown"
  OUTSIDE = 0
  HEADER_1 = 1
  HEADER_2 = 2
  BODY = 3
  state = outside
  num_lines = 0
  start_line = 0
  found_main = 0
}

state == OUTSIDE && /\[source,fpp\]/ { 
  mode = $0
  sub(/^.*,/, "", mode)
  sub(/].*/, "", mode)
  start_line = NR
  state = HEADER_1
  next 
}

state == HEADER_1 && $1 ~ "^----" { 
  mode = "ok"
  if (length($1) > 4)
    mode = "err"
  state = HEADER_2
  next 
}

state == HEADER_2 && $1 ~ "^----" {
  path = path_prefix "_" start_line "_" mode ".fpp"
  printf("") > path
  for (i = 1; i <= num_lines; ++i)
    print lines[i] >> path
  close(path)
  state = OUTSIDE
  num_lines = 0
  found_main = 0
  next
}

state == HEADER_2 {
  if (/main()/) found_main = 1
  lines[++num_lines] = $0
  next
}

