# Dictionary Data Structure Construction

## Input
1. Analysis data structure


## Output
1. Dictionary data structure containing dictionary contents
2. If dictionary construction fails, an error and no output


## Procedure
1. Create map for storing baseId -> Component
2. From the analysis, retrieve the componentInstance
   1. Get the instanceMap and for each ComponentInstance
      1. Get the baseId and component and add to map of baseId -> Component
3. Iterate baseId -> Component map
   1. Create map for storing resolvedId -> Component
   2. For each baseId and Component
      1. Get all commands from Component
         1. Add each command opcode to baseId to create the resolvedId of the command
         2. Add resolvedId to resolvedId -> Component map
      2. Get all events from Component
         1. Add each event id to baseId to create the resolvedId of the command
         2. Add resolvedId to resolvedId -> Component map
      3. Get all parameters from Component
         1. Add each command id to baseId to create the resolvedId of the command
         2. Add resolvedId to resolvedId -> Component map
      4. Get all telemtry channels from Component
         1. Add each command id to baseId to create the resolvedId of the command
         2. Add resolvedId to resolvedId -> Component map
      5. Get all records from Component
         1. Add each command id to baseId to create the resolvedId of the command
         2. Add resolvedId to resolvedId -> Component map
      6. Get all collections from Component
         1. Add each command id to baseId to create the resolvedId of the command
         2. Add resolvedId to resolvedId -> Component map