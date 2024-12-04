# ----------------------------------------------------------------------
# State machine definitions
# ----------------------------------------------------------------------
@ State machine definition S1
state machine S1

@ State machine definition S2
state machine S2

# ----------------------------------------------------------------------
# State machine instances
# ----------------------------------------------------------------------

@ state machine sm1
state machine instance sm1: S1 priority 1 block

@ state machine sm2
state machine instance sm2: S1 priority 2 assert

@ state machine sm3
state machine instance sm3: S2 priority 2+1 drop

@ state machine sm4
state machine instance sm4: S2 priority 5-1

@ state machine sm5
state machine instance sm5: S2 hook

@ state machine sm6
state machine instance sm6: S2

