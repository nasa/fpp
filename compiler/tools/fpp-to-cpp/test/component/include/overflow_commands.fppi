@ A command with queue full 'hook' behavior
async command CMD_HOOK hook

@ A command with params, priority, and queue full 'hook' behavior
async command CMD_PARAMS_PRIORITY_HOOK(
  u32: U32
) priority 30 hook
