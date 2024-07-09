# ----------------------------------------------------------------------
# State machine definitions
# ----------------------------------------------------------------------

@ State machine definition S
state machine S1
state machine S2

# ----------------------------------------------------------------------
# State machine instances
# ----------------------------------------------------------------------

@ state machine sm1
state machine instance sm1: S1

@ state machine sm2
state machine instance sm2: S1

@ state machine sm3
state machine instance sm3: S2

@ state machine sm4
state machine instance sm4: S2

