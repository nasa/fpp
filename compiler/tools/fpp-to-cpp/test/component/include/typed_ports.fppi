# ----------------------------------------------------------------------
# General ports
# ----------------------------------------------------------------------

@ A typed sync input port
sync input port noArgsSync: [3] Ports.NoArgs

@ A typed guarded input
guarded input port noArgsGuarded: Ports.NoArgs

@ A typed sync input port
sync input port noArgsReturnSync: [3] Ports.NoArgsReturn

@ A typed guarded input
guarded input port noArgsReturnGuarded: Ports.NoArgsReturn

@ A typed sync input port
sync input port typedSync: [3] Ports.Typed

@ A typed guarded input
guarded input port typedGuarded: Ports.Typed

@ A typed sync input port with a return type
sync input port typedReturnSync: [3] Ports.TypedReturn

@ A typed guarded input with a return type
guarded input port typedReturnGuarded: Ports.TypedReturn

@ A typed output port with no arguments
output port noArgsOut: Ports.NoArgs

@ A typed output port with no arguments and a return type
output port noArgsReturnOut: Ports.NoArgsReturn

@ A typed output port
output port typedOut: Ports.Typed

@ A typed output port with a return type
output port typedReturnOut: Ports.TypedReturn
