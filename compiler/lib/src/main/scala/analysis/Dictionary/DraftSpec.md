- [Types Names](#types-names)
  - [Primitive Integer Type Names](#primitive-integer-type-names)
    - [Unsigned Integer Types](#unsigned-integer-types)
    - [Signed Integer Types](#signed-integer-types)
  - [Floating-Point Type Names](#floating-point-type-names)
    - [Floating-Point Types](#floating-point-types)
  - [Boolean Type Name](#boolean-type-name)
    - [Boolean Types](#boolean-types)
  - [String Type Names](#string-type-names)
    - [String Types](#string-types)
  - [Qualified Identifier Type Names](#qualified-identifier-type-names)
- [Type Definitions](#type-definitions)
  - [Array Type Definition](#array-type-definition)
  - [Enum Type Definition](#enum-type-definition)
  - [Struct Types](#struct-types)
- [Values](#values)
  - [Primitive Integer Values](#primitive-integer-values)
  - [Floating-Point Values](#floating-point-values)
  - [Boolean Values](#boolean-values)
  - [String Values](#string-values)
  - [Array Values](#array-values)
  - [Enumeration Values](#enumeration-values)
  - [Struct Values](#struct-values)
- [Formal Parameters](#formal-parameters)
- [Parameters](#parameters)
- [Commands](#commands)
- [Telemtry Channels](#telemtry-channels)
- [Events](#events)

# Types Names

## Primitive Integer Type Names
| Field | Description | Options |
| ----- | ----------- | ------- |
| `name` | String representing the FPP type name |  U8, U16, U32, U64, I8, I16, I32, I64 |
| `kind` | String representing the kind of type | integer |
| `size` | Number of bits supported by the data type  | 8, 16, 32, 64 |
| `signed` | Boolean indicating whether the integer is signed or unsigned | true, false |

### Unsigned Integer Types
- U8
- U16
- U32
- U64

Example JSON of U8
```json
{
    "name": "U8",
    "kind": "integer",
    "size": 8,
    "signed": false,
}
```


Example JSON of U64
```json
{
    "name": "U64",
    "kind": "integer",
    "size": 64,
    "signed": false,
}
```

### Signed Integer Types
- I8
- I16
- I32
- I64

Example JSON of I8
```json
{
    "name": "I8",
    "kind": "integer",
    "size": 8,
    "signed": false,
}
```


Example JSON of I64
```json
{
    "name": "I64",
    "kind": "integer",
    "size": 64,
    "signed": false,
}
```


## Floating-Point Type Names

| Field | Description | Options |
| ----- | ----------- | ------- |
| name | String representing the FPP type name |  F32, F64 |
| kind | String representing the kind of type | float |
| size | Number of bits supported by the data type  | 32, 64 |

### Floating-Point Types
- F32
- F64

Example JSON of F32
```json
{
    "name": "F32",
    "kind": "float",
    "size": 32,
}
```

Example JSON of F64
```json
{
    "name": "F64",
    "kind": "float",
    "size": 64,
}
```


## Boolean Type Name

| Field | Description | Options |
| ----- | ----------- | ------- |
| name | String representing the FPP type name | bool |
| kind | String representing the kind of type | bool |

### Boolean Types
- true
- false

Example JSON of bool
```json
{
    "name": "bool",
    "kind": "bool",
}
```

## String Type Names
| Field | Description | Options |
| ----- | ----------- | ------- |
| name | String representing the FPP type name |  string |
| kind | String representing the kind of type | string |
| size | Number of bytes supported by the data type | Number in the range [0, 2<sup>31</sup>)

### String Types
Any sequence of characters

Example JSON of string
```json
{
    "name": "string",
    "kind": "string",
    "size": 64,
}
```

TODO: Update "size" descriptions

## Qualified Identifier Type Names
| Field | Description | Options |
| ----- | ----------- | ------- |
| name | String representing the FPP type name |  qualifiedIdentifier |
| kind | String representing the kind of type | qualifiedIdentifier |


Example JSON of qualified name
```json
{
    "name": "M.a",
    "kind": "qualifiedIdentifier",
}
```

# Type Definitions

## Array Type Definition
| Field | Description | Options |
| ----- | ----------- | ------- |
| kind | String representing the kind of type | array |
| qualifiedName | String representing unique qualified name of element in FPP model | Period seperated string |
| size | Max number of elements that can be in the data structure |
| elementType | A JSON dictionary representing the type of elements in the array |
| default | Default value of elements in array | 

Example FPP model with JSON representation:
```
module M {
  array A = [3] U8
}
```

```json
{
    "kind": "array",
    "qualifiedName": "M.A",
    "size": 3,
    "elementType": {
        "name": "U8",
        "kind": "integer",
        "signed": false,
        "size": 8
    },
    "default": [0, 0, 0]
}
```
  
Example JSON of array
```json
{
    "kind": "array",
    "qualifiedName": "M.A",
    "size": 10,
    "elementType": {
        "name": "U32",
        "kind": "integer",
        "signed": false,
        "size": 32
    },
    "default": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
}
```


## Enum Type Definition
| Field | Description | Options |
| ----- | ----------- | ------- |
| kind | String representing the kind of type | enum |
| qualifiedName | String representing unique qualified name of element in FPP model | Period seperated string |
| representationType | The type of values in the enum |
| identifiers | Dictionary of identifiers (keys) and numeric values (values) |
| default | Enum default value |

Example FPP model with JSON representation:
```
module M {
    enum Status {
        YES
        NO
        MAYBE
    } default MAYBE
}
```
```json
{
    "kind": "enum",
    "qualifiedName": "M.Status",
    "representationType": {
        "name": "I32",
        "kind": "integer",
        "signed": true,
        "size": 32
    },    
    "identifiers": {
        "YES": 0, 
        "NO": 1,
        "MAYBE": 2
    },
    "default": "MAYBE"
}
```


Example JSON of enum
```json
{
    "kind": "enum",
    "qualifiedName": "M.E",
    "representationType": {
        "name": "U8",
        "kind": "integer",
        "signed": false,
        "size": 8
    },
    "identifiers": {
        "YES": 0, 
        "NO": 1,
        "MAYBE": 2
    },
    "default": "MAYBE"
}
```

## Struct Types
| Field | Description | Options |
| ----- | ----------- | ------- |
| kind | String representing the kind of type | struct |
| qualifiedName | String representing unique qualified name of element in FPP model | Period seperated string |
| members | JSON dictionary consisting of identifier (keys) and type (values) of each member in the struct |
| default | JSON dictionary consising of identifier (key) and default value (value) |
| formatSpecifiers | JSON dictionary consisting of identifier (key) and string format specifier(value)

Example FPP model with JSON representation:

```
module M {
    struct A {
        x: U32
        y: F32
    }
}
```
```json
{
    "kind": "struct",
    "qualifiedName": "M.A",
    "members": {
        "x": {
            "name": "U32",
            "kind": "integer",
            "signed": false,
            "size": 32
        },
        "y": {
            "name": "F32",
            "kind": "float",
            "size": 32,
        }
    },
    "default": {
        "x": 0,
        "y": 0
    },
    "formatSpecifiers": {}
}
```

Example JSON of a struct:
```json
{
    "kind": "struct",
    "qualifiedName": "M.myStruct",
    "members": {
        "w": {
            "kind": "array",
            "qualifiedName": "M.A",
            "size": 10,
            "elementType": {
                "name": "U32",
                "kind": "integer",
                "signed": false,
                "size": 32
            },
            "default": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        },
        "x": {
            "type": "U32",
            "kind": "integer",
            "signed": false,
            "size": 32,
        },
        "y": {
            "type": "string",
            "kind": "string",
            "size": 64
        },
        "z": {
            "type": "F64",
            "kind": "float",
            "size": 64,
        }
    },
    "default": {
        "w": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        "x": 5,
        "y": "Hello World!",
        "z": 10.0
    },
    "formatSpecifiers": {
        "x": "The count is {}"
    }
}
```


# Values

## Primitive Integer Values
| Field | Description | Options |
| ----- | ----------- | ------- |
| type | JSON dictionary representing integer type properties |
| value | Number representing integer value | Mention valid integer values (ie: signed vs. unsigned integer within range?)

Example JSON of type U8 with a value of 2:
```json
2
```

Example JSON of type I8 with a value of -2:
```json
-2
```

## Floating-Point Values

| Field | Description | Options |
| ----- | ----------- | ------- |
| type | JSON dictionary representing float type properties |
| value | String representing float value |


Example JSON of type F32 with a value of 10.0
```json
10.5
```

## Boolean Values

| Field | Description | Options |
| ----- | ----------- | ------- |
| type | JSON dictionary representing bool type properties |
| value | Boolean value |

Example JSON of type bool with a value of true

```json
true
```

## String Values
| Field | Description | Options |
| ----- | ----------- | ------- |
| type | JSON dictionary representing string type properties |
| value | String containing sequence of characters |


Example JSON of type string with a value of "Hello World!"
```json
"Hello World!"
```


## Array Values
| Field | Description | Options |
| ----- | ----------- | ------- |
| type  | JSON dictionary representing array type properties |
| value | Array with elements |

Example JSON of an array of type U32 consisting of 10 elements
```json
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```

## Enumeration Values
| Field | Description | Options |
| ----- | ----------- | ------- |
| type  | JSON dictionary representing enum type definition properties |
| value | Enumeration value |

Example JSON of an enum
```json
"YES"
```

## Struct Values

| Field | Description | Options |
| ----- | ----------- | ------- |
| type  | JSON dictionary representing struct type definition properties |
| value | JSON dictionary consisting of identifier (keys) and values (values) |

Example JSON of a struct:
```json
{
    "w": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
    "x": 20,
    "y": "Hello World!",
    "z": 15.5
}
```

# Formal Parameters
| Field | Description | Options |
| ----- | ----------- | ------- |
| identifier | String identifier | string |
| description | String annotation of parameter | string |
| type | JSON dictionary representing the type of the parameter |
| ref | Boolean indicating whether the format parameter is to be passed by referenced when it is used in a synchronous port invocation | true, false

```json
{
    "identifier": "",
    "description": "",
    "type": {

    },
    "ref": false
}
```

# Parameters
| Field | Description | Options |
| ----- | ----------- | ------- |
| identifier | String identifier | string |
| description | String annotation of command | string |
| typeName | JSON dictionary representing the type of the parameter |
| default | Default value of the parameter | |
| numericIdentifier | String hexidecimal representing the numeric identifier of the parameter | string |
| setOpcode | String hexidecimal representing the opcode of the command for setting the parameter | string |
| saveOpcode | String hexidecimal representing the opcode of the command for saving the parameter | string |

```json
{
    "identifier": "",
    "description": "",
    "typeName": {

    },
    "default": "",
    "numericIdentifier": "",
    "setOpcode": "",
    "saveOpcode": ""
}
```
Example FPP model with JSON representation:
```
@ This is the annotation for Parameter 1
param Parameter1: U32 \
  id 0x00 \
  set opcode 0x80 \
  save opcode 0x81
```

```json
{
    "identifier": "Parameter1",
    "description": "This is the annotation for Parameter 1",
    "typeName": {
        "name": "U32",
        "kind": "integer",
        "signed": false,
        "size": 32
    },
    "default": "",
    "numericIdentifier": "0x00",
    "setOpcode": "0x80",
    "saveOpcode": "0x81"
}
```
TODO: could there be a case where the param type is string and the default value is empty string?

# Commands
| Field | Description | Options |
| ----- | ----------- | ------- |
| commandKind | String representing the kind of command | async, guarded, sync |
| opcode | String hexidecimal command opcode | String hexidecimal |
| identifier | String identifier | string |
| description | String annotation of command | string |
| params | List of JSON parameter dictionaries | JSON dictionary |
| priority | Number representing the priority for the command on the input queue | number |
| queueFullBehavior | String representing the behavior of the command when the input full is queue | assert, block, drop |

```json
{
    "commandKind": "",
    "opcode": "",
    "identifier": "",
    "description": "",
    "params": [
        {
            "identifier": "",
            "description": "",
            "type": {

            }
        }
    ],
    "priority": "",
    "queueFullBehavior": ""
}
```

Example FPP model with JSON representation:
```
@ A sync command with parameters
sync command SyncParams(
    param1: U32 @< Param 1
    param2: string @< Param 2
) opcode 0x02
```

```json
{
    "commandKind": "sync",
    "opcode": "0x02",
    "identifier": "SyncParams",
    "description": "A sync command with parameters",
    "params": [
        {
            "identifier": "param1",
            "description": "Param 1",
            "type": {
                "name": "U32",
                "kind": "integer",
                "size": 32,
                "signed": false,
            }
        },
         {
            "identifier": "param2",
            "description": "Param 2",
            "type": {
                "name": "string",
                "kind": "string",
                "size": ""
            }
        }
    ],
}
```
TODO: what is the default maximum string length (in the event no size is specified for a string)?

# Telemtry Channels
| Field | Description | Options |
| ----- | ----------- | ------- |
| identifier | String identifier of telemtry channel | string |
| description | String annotation of command | string |
| typeName | JSON dictionary representing the type of the telemtry channel |
| numericIdentifier | Numeric identifier | string |
| telemtryUpdate | String representing when the telemetry channel can update | always, on change |
| formatString | String format with a single argument (the telemtry channel) | string
| limit | JSON dictionary consisting of high and low limits | JSON dictionary |

```json
{
    "identifier": "",
    "description": "",
    "typeName": "",
    "numericId": "",
    "telemtryUpdate": "",
    "formatString": "",
    "limit": {
        "high": {
            "yellow": "",
            "orange": "",
            "red": ""
        },
        "low": {
            "yellow": "",
            "orange": "",
            "red": ""
        }
    }
}
```

# Events
| Field | Description | Options |
| ----- | ----------- | ------- |
| identifier | String identifier of the event | string |
| description | String annotation of command | string |
| severity | String representing severit of the event | activity high, activity low, command, diagnostic, fatal, warning high, warning low |
| params | List of JSON parameter dictionaries | JSON dictionary |
| numericIdentifier | String representing the numeric identifier of the event | string |
| formatString | String format with event parameters as arguments | string
| throttle | Number representing the maximum number of times to emit the event before throttling it | number |


```json
{
    "identifier": "",
    "description": "",
    "severity": "",
    "params": [
        {
            "identifier": "",
            "description": "",
            "type": ""
        }
    ],
    "numericIdentifier": "",
    "formatString": "",
    "throttle": ""
}
```

Example FPP model with JSON representation:
```
@ This is the annotation for Event 0
event Event0 \
  severity activity low \
  id 0x00 \
  format "Event 0 occurred"
```

```json
{
    "identifier": "Event0",
    "description": "This is the annotation for Event 0",
    "severity": "activity low",
    "params": [],
    "numericIdentifier": "0x00",
    "formatString": "Event 0 occurred",
    "throttle": ""
}
```

Example FPP model with JSON representation:
```
@ This is the annotation for Event 1
@ Sample output: "Event 1 occurred with argument 42"
event Event1(
  arg1: U32 @< Argument 1
) \
  severity activity high \
  id 0x01 \
  format "Event 1 occurred with argument {}"
```
```json
{
    "identifier": "Event1",
    "description": "This is the annotation for Event 1",
    "severity": "activity high",
    "params": [
        {
           "identifier": "arg1",
            "description": "Argument 1",
            "type": {
                "name": "U32",
                "kind": "integer",
                "size": 32,
                "signed": false,
            },
            "ref": false  
        }
    ],
    "numericIdentifier": "0x01",
    "formatString": "Event 1 occurred with argument {}",
    "throttle": ""
}

# Ports
| Field | Description | Options |
| ----- | ----------- | ------- |
| identifier | String identifier of the port | string |
| description | String annotation of the port | string |
| params | List of JSON parameter dictionaries | JSON dictionary |
| returnType | JSON dictionary representing the return type of the port | JSON dictionary |

```json
{
    "identifier": "",
    "description": "",
    "params": [
        {
            "identifier": "",
            "description": "",
            "type": {

            }
        },
         {
            "identifier": "",
            "description": "",
            "type": {

            }
        }
    ],
    "returnType": {
        
    }
}
```

Example FPP model with JSON representation:
```
@ This is the annotation for Port 1
port Port1(
    a: U32 @< Parameter a
    b: F64 @< Parameter b
) -> U32
```

```json
{
    "identifier": "Port1",
    "description": "This is the annotation for Port 1",
    "params": [
        {
            "identifier": "a",
            "description": "Parameter a",
            "type": {
                "name": "U32",
                "kind": "integer",
                "size": 32,
                "signed": false,
            }
        },
        {
            "identifier": "b",
            "description": "Parameter b",
            "type": {
                "name": "F64",
                "kind": "float",
                "size": 64,
            }
        }
    ],
    "returnType": {
        "name": "U32",
        "kind": "integer",
        "size": 32,
        "signed": false
    }
}
```
