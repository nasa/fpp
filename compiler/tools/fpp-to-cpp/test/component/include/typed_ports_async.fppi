@ A typed async input port
async input port noArgsAsync: Ports.NoArgs

@ A typed async input port
async input port typedAsync: Ports.Typed

@ A typed async input port with queue full behavior and priority
async input port typedAsyncAssert: Ports.Typed assert

@ A typed async input port with queue full behavior and priority
async input port typedAsyncBlockPriority: Ports.Typed priority 10 block

@ A typed async input port with queue full behavior and priority
async input port typedAsyncDropPriority: Ports.Typed priority 5 drop
