import json
import sys
import argparse
from typing import Union
from jsonschema import validate, ValidationError
from referencing import Resource, Registry


metadata_schema = {
    "type": "object",
    "properties": {
        "deploymentName" : {"type": "string"},
        "frameworkVersion" :  {"type": "string"},
        "projectVersion": {"type": "string"},
        "libraryVersions" : {
            "type": "array",
            "items": {"type": "string"}
        },
        "dictionarySpecVersion" :  {"type": "string"}
    },
    # "required": []
}

type_name_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "kind": {"type": "string", "kind": ["integer", "float", "bool", "string", "qualifiedIdentifier"]},
        "size": {"type": "number"},
        "signed": {"type": "boolean"}
    },
    "required": ["name", "kind"]
}

array_type_definition_schema = {
    "type": "object",
    "properties": {
        "kind" : {"type": "string", "enum": ["array"]},
        "qualifiedName" :  {"type": "string"},
        "size": {"type": "number"},
        "elementType" : type_name_schema,
        "default" :  {"type": "array"}
    }
}

enum_type_definition_schema = {
    "type": "object",
    "properties": {
        "kind" : {"type": "string", "enum": ["enum"]},
        "qualifiedName" :  {"type": "string"},
        "representationType": type_name_schema,
        "identifiers" : {
            "type": "object",
            "properties": {
                "_placeholder": {"type": "number"} # provide a placeholder value which represents the string -> number member in an enum
            },
            "additionalProperties": True # allow additional properties, that way we can validate all key/val pairs in the enum
        },
        "default" : {"type": "string"}
    }
}

struct_member_schema = {
    "type": "object",
    "properties": {
        "type": type_name_schema,
        "index": {"type": "number"},
        "size": {"type": "number"},
        "format": {"type": "string"}
    }
}

struct_default_schema = {
    "type": "object",
    "properties": {
        "_placeholder": {"type": ["string", "number", "boolean", "array"]}# provide a placeholder value which represents the key -> val member in an struct
    },
    "additionalProperties": True # allow additional properties, that way we can validate all key/val pairs in the struct
}

struct_type_defintion_schema = {
    "type": "object",
    "properties": {
        "kind" : {"type": "string", "enum": ["struct"]},
        "qualifiedName" :  {"type": "string"},
        "members": struct_member_schema,
        "default": struct_default_schema
    }
}

formal_param_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "description": {"type": "string"},
        "type": type_name_schema,
        "ref": {"type": "boolean"}
    }
}

command_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "commandKind": {"type": "string"},
        "opcode": {"type": "number"},
        "description": {"type": "string"},
        "formalParams": {"type":"array", "items": formal_param_schema},
        "priority": {"type": "number"},
        "queueFullBehavior": {"type": "string", "enum": ["assert", "block", "drop"]},
    }
}

param_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "description": {"type": "string"},
        "type": type_name_schema,
        "default": {"type": ["number", "string", "boolean", "object", "array"]},
        "identifier": {"type": "number"}
    }
}

telem_channel_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "description": {"type": "string"},
        "type": type_name_schema,
        "identifier": {"type": "number"},
        "telemetryUpdate": {"type": "string", "enum": ["always", "onchange"]},
        "format": {"type": "string"},
        "limit": {
            "type": "object",
            "properties": {
                "high": {
                    "type": "object",
                    "properties": {
                        "yellow": {"type": "number"},
                        "orange": {"type": "number"},
                        "red": {"type": "number"}
                    }
                },
                "low": {
                    "type": "object",
                    "properties": {
                        "yellow": {"type": "number"},
                        "orange": {"type": "number"},
                        "red": {"type": "number"}
                    }
                }
            }
        }
    }
}

event_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "description": {"type": "string"},
        "severity": {"type": "string", "enum": ["ACTIVITY_HIGH", "ACTIVITY_LOW", "COMMAND", "DIAGNOSTIC", "FATAL", "WARNING_HIGH", "WARNING_LOW"]},
        "formalParams": {"type":"array", "items": formal_param_schema},
        "identifier": {"type": "number"},
        "format": {"type": "string"},
        "throttle": {"type": "number"}
    }
}

record_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "description": {"type": "string"},
        "type": type_name_schema,
        "array": {"type": "boolean"},
        "identifier": {"type": "number"}
    }
}

container_schema = {
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "description": {"type": "string"},
        "identifier": {"type": "number"},
        "defaultPriority": {"type": "number"}
    }
}

dictionary_schema = {
    "type": "object",
    "properties": {
        "metadata": metadata_schema,
        "typeDefinitions":  {
            "type": "array", 
            "items": { 
                "anyOf": [
                    array_type_definition_schema, 
                    enum_type_definition_schema, 
                    struct_type_defintion_schema
                ]
            } 
        },
        "commands":  {"type": "array", "items": command_schema},
        "parameters":  {"type": "array", "items": param_schema},
        "events":  {"type": "array", "items": event_schema},
        "telemetryChannels":  {"type": "array", "items": telem_channel_schema},
        "records":  {"type": "array", "items": record_schema},
        "containers":  {"type": "array", "items": container_schema},
    }
}

def load_local_schema(path: str):
    # open JSON file
    f = open(path)
    contents = json.load(f)
    f.close()
    return contents

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--json_dict', type=str, help='JSON dictionary file', required=True)
    parser.add_argument('--schema', type=str, help='JSON schema file', required=True)
    args = parser.parse_args()
    in_file = args.json_dict

    # open JSON file
    f = open(in_file)
    # JSON as dictionary
    dictionary_json = json.load(f)
    # close JSON file
    f.close()
    # load dictionary JSON schema file
    # dictionary_schema = load_local_schema(args.schema)

    try:
        validate(dictionary_json, dictionary_schema)
        print(f"Dictionary JSON is valid!")
    except ValidationError as e:
        print(f"Dictionary JSON schema validation failed: {e.message}")
main()
