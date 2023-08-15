import json
import sys
import re
import sys

# Recursively collects the values of the "id" key from a nested JSON structure
# and returns the list of the collected values
def collect_ids(json_data):
    ids = []

    if isinstance(json_data, list):
        for item in json_data:
            ids.extend(collect_ids(item))

    elif isinstance(json_data, dict):
        for key, value in json_data.items():
            if key == 'AstNode':
                ids.append(value["id"])

            if isinstance(value, (dict, list)):
                ids.extend(collect_ids(value))

    return ids


# Checks that each key in numbers is represented in the dictionary data
def check_if_ids_in_loc_map(numbers, data):
    for number in numbers:
        if str(number) not in data:
            sys.stderr.write(f"ID {number} found AST in but not in location map")
            return False
    
    return True


if len(sys.argv) != 3: 
    sys.stderr.write("Invalid Number of arguments")
    sys.exit(1)


with open(str(sys.argv[1])) as ast_json, open(str(sys.argv[2])) as loc_json:
    ast = json.load(ast_json)
    location = json.load(loc_json)
    loc_map_check_result = check_if_ids_in_loc_map(collect_ids(ast), location)
    if loc_map_check_result is not True:
        sys.exit(1)
    else:
        sys.exit(0)

