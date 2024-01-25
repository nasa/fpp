# ----------------------------------------------------------------------
# General ports
# ----------------------------------------------------------------------

@ A typed async input port
async input port noArgsAsync: [3] Ports.NoArgs

@ A typed guarded input
guarded input port noArgsGuarded: Ports.NoArgs

@ A typed sync input port
sync input port noArgsReturnSync: [3] Ports.NoArgsReturn

@ A typed guarded input
guarded input port noArgsReturnGuarded: Ports.NoArgsReturn

@ A typed output port with no arguments
output port noArgsOut: Ports.NoArgs

@ A typed output port with no arguments and a return type
output port noArgsReturnOut: Ports.NoArgsReturn
