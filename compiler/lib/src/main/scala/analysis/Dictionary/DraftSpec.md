- [Type Names](#type-names)
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
  - [Enumeration Type Definition](#enumeration-type-definition)
  - [Struct Member](#struct-member)
  - [Struct Type Definition](#struct-type-definition)
- [Values](#values)
  - [Primitive Integer Values](#primitive-integer-values)
  - [Floating-Point Values](#floating-point-values)
  - [Boolean Values](#boolean-values)
  - [String Values](#string-values)
  - [Array Values](#array-values)
  - [Enumeration Values](#enumeration-values)
  - [Struct Values](#struct-values)
- [Parameters, Commands, Telemetry Channels, and Events](#parameters-commands-telemetry-channels-and-events)
  - [Formal Parameters](#formal-parameters)
  - [Parameters](#parameters)
  - [Commands](#commands)
  - [Telemtry Channels](#telemtry-channels)
  - [Events](#events)
- [Data Products](#data-products)
  - [Record](#record)
  - [Container](#container)
- [Dictionary](#dictionary)
  - [Dictionary Metadata](#dictionary-metadata)
  - [Dictionary Content](#dictionary-content)

# Type Names

## Primitive Integer Type Names
| Field | Description | Options | Required | 
| ----- | ----------- | ------- | -------- |
| `name` | **String** representing the FPP type name |  U8, U16, U32, U64, I8, I16, I32, I64 | true |
| `kind` | **String** representing the kind of type | integer | true |
| `size` | **Number** of bits supported by the data type  | 8, 16, 32, 64 | true |
| `signed` | **Boolean** indicating whether the integer is signed or unsigned | true, false | true |

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
    "signed": true,
}
```


Example JSON of I64
```json
{
    "name": "I64",
    "kind": "integer",
    "size": 64,
    "signed": true,
}
```


## Floating-Point Type Names

| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `name` | **String** representing the FPP type name |  F32, F64 | true |
| `kind` | **String** representing the kind of type | float | true |
| `size` | **Number** of bits supported by the data type  | 32, 64 | true |

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

| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `name` | **String** representing the FPP type name | bool | true
| `kind` | **String** representing the kind of type | bool | true |

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
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `name` | **String** representing the FPP type name |  string | true |
| `kind` | **String** representing the kind of type | string | true | 
| `size` | **Number** of bytes supported by the data type | **Number** in the range [0, 2<sup>31</sup>) | true |

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

## Qualified Identifier Type Names
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `name` | **String** representing the FPP type name |  qualifiedIdentifier | true |
| `kind` | **String** representing the kind of type | qualifiedIdentifier | true |


Example JSON of qualified name
```json
{
    "name": "M.a",
    "kind": "qualifiedIdentifier",
}
```

# Type Definitions

## Array Type Definition
| Field | Description | Options | Required | 
| ----- | ----------- | ------- | -------- |
| `kind` | **String** representing the kind of type | array | true |
| `qualifiedName` | **String** representing unique qualified name of element in FPP model | Period seperated **String** | true |
| `size` | Max **Number** of elements that can be in the data structure | Number | true |
| `elementType` | A **JSON dictionary** representing the type of elements in the array | JSON Dictionary | true
| `default` | Default array value | Value of type specified in `elementType` | false |

TODO: update default field description - array value vs. type of elements in array
- think about: is the type conversion we are doing specified enough
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


## Enumeration Type Definition
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `kind` | String representing the kind of type | enum | true |
| `qualifiedName` | String representing unique qualified name of element in FPP model | Period seperated **String** | true |
| `representationType` | The `Type Name` of values in the enumeration | `Type Name` | true |
| `identifiers` | Dictionary of identifiers (keys) and numeric values (values) | true |
| `default` | `Qualified Identifier` enumeration value | `Qualified Identifier` | false | false |

TODO: make defaults fully qualified

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
    "default": "M.Status.MAYBE"
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
    "default": "M.E.NO"
}
```

## Struct Member
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `type` | `Type Name` of member | `Type Name` | true |
| `index` | **Number** index of the struct member | **Number** | true |
| `size` | **Number** representing the size of the struct member | false |
| `formatSpecifier` | **String** format specifier | **String** | false |

## Struct Type Definition
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `kind` | String representing the kind of type | struct | true |
| `qualifiedName` | String representing unique qualified name of element in FPP model | Period seperated **String** | true |
| `members` | JSON dictionary consisting of **String** identifier (key) and `Struct Member` (value) | JSON dictionary | true |
| `default` | JSON dictionary consising of **String** identifier (key) and default value (value) | JSON dictionary | false |

TODO: add order field with member ordering, since there's an ordering in FPP
- note: fields of values are unordered
Example FPP model with JSON representation:
- need to specify the optional size of a member (ex: array member)
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
            "type": {
                "name": "U32",
                "kind": "integer",
                "signed": false,
                "size": 32
            },
            "formatSpecifier": "the count is {}",
            "index": 0
        },
        "y": {
            "type": {
                "name": "F32",
                "kind": "float",
                "size": 32
            },
            "index": 1
        }
    },
    "default": {
        "x": 0,
        "y": 0
    },
}
```

Example JSON of a struct:
```json
{
    "kind": "struct",
    "qualifiedName": "M.myStruct",
    "members": {
        "w": {
            "type": {
                "kind": "qualifiedIdentifier",
                "qualifiedName": "M.A"
            },
            "index": 0
        },
        "x": {
            "type": {
                "name": "U32",
                "kind": "integer",
                "signed": false,
                "size": 32
            },
            "index": 1,
            "formatSpecifier": "The count is {}"
        },
        "y": {
            "type": {
                "name": "string",
                "kind": "string",
                "size": 64
            },
            "index": 2
        },
        "z": {
            "type": {
                "name": "F64",
                "kind": "float",
                "size": 64
            },
            "index": 3
        }
    },
    "default": {
        "w": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        "x": 5,
        "y": "Hello World!",
        "z": 10.0
    }
}
```


# Values

## Primitive Integer Values
**Number** representing integer value

Example JSON of type U8 with a value of 2:
```json
2
```

Example JSON of type I8 with a value of -2:
```json
-2
```

## Floating-Point Values
**Number** representing float value

Example JSON of type F32 with a value of 10.0
```json
10.5
```

## Boolean Values
**Boolean** value

Example JSON of type bool with a value of true

```json
true
```

## String Values
**String** containing sequence of characters

Example JSON of type string with a value of "Hello World!"
```json
"Hello World!"
```


## Array Values
**Array** with elements

Example JSON of an array of type U32 consisting of 10 elements
```json
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```

## Enumeration Values
`Qualified Identifier` enumeration value

Example JSON of an enum
```json
"Status.YES"
```

## Struct Values
**JSON Dictionary** consisting of `Qualified Name` identifier (keys) and values (values)

Example JSON of a struct:
```json
{
    "S.w": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
    "S.x": 20,
    "S.y": "Hello World!",
    "S.z": 15.5
}
```
TODO - account for null, inf, -inf, nan

# Parameters, Commands, Telemetry Channels, and Events

## Formal Parameters
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `identifier` | **String** identifier | **String** | true |
| `description` | **String** annotation of parameter | **String** | true |
| `type` | `Type Name` of parameter | `Type Name` | true |
| `ref` | **Boolean** indicating whether the formal parameter is to be passed by referenced when it is used in a synchronous port invocation | true, false | false |

```json
{
    "identifier": "",
    "description": "",
    "type": {},
    "ref": false
}
```

TODO: get rid of json dicitonaries and just use the type name (can link back to where its defined in the docs)
## Parameters
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `identifier` | **String** identifier | **String** | true |
| `description` | **String** annotation of parameter | **String** | true |
| `type` | `Type Name` of the parameter | `Type Name` | true |
| `default` | Default value (of type specified in `type`)  of the parameter | Value of type specified in `type` | false |
| `numericIdentifier` | **Number** representing the numeric identifier of the parameter | **Number** | false |
| `setOpcode` | **Number** representing the opcode of the command for setting the parameter | **Number** | false |
| `saveOpcode` | **Number** representing the opcode of the command for saving the parameter | **Number** | false |


```json
{
    "identifier": "",
    "description": "",
    "type": {

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
  id 0x100 \
  set opcode 0x101 \
  save opcode 0x102
```

```json
{
    "identifier": "Parameter1",
    "description": "This is the annotation for Parameter 1",
    "type": {
        "name": "U32",
        "kind": "integer",
        "signed": false,
        "size": 32
    },
    "default": 0,
    "numericIdentifier": "256",
    "setOpcode": "257",
    "saveOpcode": "258"
}
```
TODO: could there be a case where the param type is string and the default value is empty string?
TODO: need to look into defaults more, look into what the generated code does
- if invalid, then we make the default optional
TODO: use FPP default in the event the user didn't specify a default value
TODO: identifier should be a qualified identifier

## Commands
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `commandKind` | **String** representing the kind of command | async, guarded, sync | true |
| `opcode` | **Number** command opcode | **Number** | true |
| `identifier` | **String** identifier | **String** | true |
| `description` | **String** annotation of command | string | true |
| `formalParams` | Array of `Formal Parameters` | Array of `Formal Parameters` | false |
| `priority` | **Number** representing the priority for the command on the input queue | **Number** | false |
| `queueFullBehavior` | **String** representing the behavior of the command when the input full is queue | assert, block, drop | false |
TODO: fix parameter to formal
TODO: default queue full is assert
```json
{
    "commandKind": "",
    "opcode": "",
    "identifier": "",
    "description": "",
    "formalParams": [
        {
            "identifier": "",
            "description": "",
            "type": {},
            "ref": false
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
    "opcode": 102,
    "identifier": "SyncParams",
    "description": "A sync command with parameters",
    "formalParams": [
        {
            "identifier": "param1",
            "description": "Param 1",
            "type": {
                "name": "U32",
                "kind": "integer",
                "size": 32,
                "signed": false,
            },
            "ref": false
        },
         {
            "identifier": "param2",
            "description": "Param 2",
            "type": {
                "name": "string",
                "kind": "string",
                "size": ""
            },
            "ref": false
        }
    ],
}
```
TODO: what is the default maximum string length (in the event no size is specified for a string)?
- check into default, maybe 80

## Telemtry Channels
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `identifier` | **String** identifier of telemtry channel | **String** | true |
| `description` | **String** annotation of channel | **String** | true |
| `type` | `Type Name` the telemtry channel | `Type Name` | true |
| `numericIdentifier` | **Number** representing numeric identifier | **Number** | true |
| `telemtryUpdate` | **String** representing when the telemetry channel can update | always, on change | false |
| `formatString` | **String** format with a single argument (the telemtry channel) | **String** | false |
| `limit` | **JSON dictionary** consisting of high and low limits | **JSON dictionary** | false |

```json
{
    "identifier": "",
    "description": "",
    "type": "",
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

## Events
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `identifier` | **String** identifier of the event | **String** | true |
| `description` | **String** annotation of event | **String** | true |
| `severity` | **String** representing severit of the event | activity high, activity low, command, diagnostic, fatal, warning high, warning low | true |
| `formalParams` | Array of `Formal Parameters` | Array `Formal Parameters` | true |
| `numericIdentifier` | **Number** representing the numeric identifier of the event | **Number** | true |
| `formatString` | **String** format with event parameters as arguments | **String** | false |
| `throttle` | **Number** representing the maximum number of times to emit the event before throttling it | **Number** | false |
todo: change key to formalParams (everywhere they appear)
- make formalParams a required field that is empty  (wherever applicable)
- all identifiers should be qualified
```json
{
    "identifier": "",
    "description": "",
    "severity": "",
    "formalParams": [],
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
  id 0x100 \
  format "Event 0 occurred"
```

```json
{
    "identifier": "Event0",
    "description": "This is the annotation for Event 0",
    "severity": "activity low",
    "formalParams": [
         {
            "identifier": "",
            "description": "",
            "type": {},
            "ref": false
        }
    ],
    "numericIdentifier": 256,
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
  id 0x101 \
  format "Event 1 occurred with argument {}"
```
```json
{
    "identifier": "Event1",
    "description": "This is the annotation for Event 1",
    "severity": "activity high",
    "formalParams": [
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
    "numericIdentifier": 257,
    "formatString": "Event 1 occurred with argument {}",
    "throttle": ""
}
```

# Data Products
## Record
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `identifier` | **String** identifier of the record | **String** | true |
| `description` | **String** annotation of record | **String** | true |
| `type` | `Type Name` the record | `Type Name` | true |
| `array` | **Boolean** specifying whether the record stores a variable number of elements | true, false | false |
| `numericIdentifier` | **Number** representing the numeric identifier of the record | **Number** | true |

Example FPP model with JSON representation:
```
@ Record 0: A variable number of F32 values
@ Implied id is 0x100
product record Record0: F32 array

@ Record 1: A single U32 value
product record Record1: U32 id 0x102
```

```json
[
    {
        "identifier": "Record0",
        "description": "Record 0: A variable number of F32 values",
        "type": {
            "name": "F32",
            "kind": "float",
            "size": 32
        },
        "array": true,
        "numericIdentifier": 256 
    },
    {
        "identifier": "Record1",
        "description": "Record 1: A single U32 value",
        "type": {
            "name": "U32",
            "kind": "integer",
            "signed": false,
            "size": 32
        },
        "array": false,
        "numericIdentifier": 258
    }      
]
```

## Container
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `identifier` | **String** identifier of the container | **String** | true |
| `description` | **String** annotation of container | **String** | true |
| `numericIdentifier` | **Number** representing the numeric identifier of the record | **Number** | true |
| `defaultPriority` | **Number** representing the downlink priority for the container | **Number** | false |

Example FPP model with JSON representation:
```
@ Container 0
@ Implied id is 0x100
product container Container0

@ Container 1
product container Container1 id 0x102

@ Container 2
@ Implied id is 0x103
product container Container2 default priority 10
```

```json
[
    {
       "identifier": "Container0",
       "description": "Container 0\nImplied id is 0x100",
       "numericIdentifier": 256,
    },
    {
        "identifier": "Container1",
        "description": "Container 1",
        "numericIdentifier": 258,
    },
    {
        "identifier": "Container2",
        "description": "Container 2\nImplied id is 0x103",
        "numericIdentifier": 3,
        "defaultPriority": 259
    }
]
```

# Dictionary
## Dictionary Metadata
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `deploymentName` | **String** representing the deployment name | **String** | true |
| `frameworkVersion` | **String** representing the F´ framework version (semantic versioning) | **String** | true |
| `projectVersion` | **String** representing the project version (semantic versioning) | **String** | true |
| `libraryVersion` | **Array of Strings** corresponding to the version (semantic versioning) of libraries used by the F´ project | **Array of Strings** | true
TODO: add libariries version, dictionary name to library version
- list of libraries that have the version with it
- add a dictionary spec version (fpp version of tool that generated the JSON)


```json
{
    "deploymentName": "MyDeployment",
    "frameworkVersion": "3.3.2",
    "projectVersion": "1.0.0"
}
```

## Dictionary Content
| Field | Description | Options | Required |
| ----- | ----------- | ------- | -------- |
| `metadata` | `Dictionary Metadata` | `Dictionary Metadata` | true |
| `arrays` | Array of `Arrays` | Array of `Arrays` | true |
| `enums` | Array of `Enums` | Array of `Enums` | true |
| `structs` | Array of `Structs` | Array of `Structs` | true |
| `commands` | Array of `Commands` | Array of `Commands` | true |
| `events` | Array of `Events` | Array of `Events` | true |
| `telemetryChannels` | Array of `Telemetry Channels` | Array of `Telemetry Channels` | true |
| `parameters` | Array of `Parameters` | Array of `Parameters` | true |
TODO: keep emtpy lists instead of removing keys
```json
{
    "metadata": {
        "deploymentName": "MyDeployment",
        "frameworkVersion": "3.3.2",
        "projectVersion": "1.0.0"
    },
    "enums": [
        "MyDeployment"
    ],
    "structs": [

    ],
    "commands": [
        {
            "commandKind": "sync",
            "opcode": 257,
            "identifier": "SyncParams",
            "description": "A sync command with parameters",
            "formalParams": [
                {
                    "identifier": "param1",
                    "description": "Param 1",
                    "type": {
                        "name": "U32",
                        "kind": "integer",
                        "size": 32,
                        "signed": false,
                    },
                    "ref": false
                },
                {
                    "identifier": "param2",
                    "description": "Param 2",
                    "type": {
                        "name": "string",
                        "kind": "string",
                        "size": ""
                    },
                    "ref": false
                }
            ],
        }
    ],
    "events": [
        {
            "identifier": "Event0",
            "description": "This is the annotation for Event 0",
            "severity": "activity low",
            "formalParams": [
                {
                    "identifier": "",
                    "description": "",
                    "type": {},
                    "ref": false
                }
            ],
            "numericIdentifier": 256,
            "formatString": "Event 0 occurred",
            "throttle": ""
        },
        {
            "identifier": "Event1",
            "description": "This is the annotation for Event 1",
            "severity": "activity high",
            "formalParams": [
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
            "numericIdentifier": 257,
            "formatString": "Event 1 occurred with argument {}",
            "throttle": ""
        }
    ],
    "telemetryChannels": [

    ],
    "parameters": [
        {
            "identifier": "Parameter1",
            "description": "This is the annotation for Parameter 1",
            "type": {
                "name": "U32",
                "kind": "integer",
                "signed": false,
                "size": 32
            },
            "default": 0,
            "numericIdentifier": 256,
            "setOpcode": 257,
            "saveOpcode": 258
        }
    ]
}
```


- add to fprime-docs-design
- future: spec for autocoder/autocoded stuff