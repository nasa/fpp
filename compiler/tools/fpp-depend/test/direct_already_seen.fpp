# Test direct dependencies already seen at a lower level
# The dependency on b exists in direct_already_seen_a.fpp at level 2
# But it should be reported here as a direct dependency

locate constant a at "direct_already_seen_a.fpp"
locate constant b at "direct_already_seen_b.fpp"

constant c = a
constant d = b
