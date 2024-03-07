import argparse
import json
from jsonschema import validate, ValidationError

def load_json_file(path: str):
    f = open(path)
    contents = json.load(f)
    f.close()
    return contents

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--json_dict', type=str, help='Dictionary JSON file', required=True)
    parser.add_argument('--schema', type=str, help='Dictionary JSON schema to validate against', required=True)
    args = parser.parse_args()
    # Dictionary JSON file
    dictionary_json = load_json_file(args.json_dict)
    # Dictioanry JSON schema to validate against
    dictionary_schema = load_json_file(args.schema)
    try:
        validate(instance=dictionary_json, schema=dictionary_schema)
        print(f"Dictionary JSON is valid!")
    except ValidationError as e:
        print(f"Dictionary JSON schema validation failed: {e.message}")
        print(f"Failure Path: {e.json_path}")

main()
