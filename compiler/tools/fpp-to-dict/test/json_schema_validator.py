import json
import argparse
from jsonschema import validate, ValidationError

def load_json_file(path: str):
    f = open(path)
    contents = json.load(f)
    f.close()
    return contents

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--json_dict', type=str, help='JSON dictionary file', required=True)
    parser.add_argument('--schema', type=str, help='JSON schema file', required=True)
    args = parser.parse_args()

    dictionary_json = load_json_file(args.json_dict)
    dictionary_schema = load_json_file(args.schema)


    try:
        validate(instance=dictionary_json, schema=dictionary_schema)
        print(f"Dictionary JSON is valid!")
    except ValidationError as e:
        print(f"Dictionary JSON schema validation failed: {e.message}")
        print(f"JSON Path: {e.json_path}")

main()
