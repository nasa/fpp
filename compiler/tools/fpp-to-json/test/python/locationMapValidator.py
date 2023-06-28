import json
import sys

def collect_ids(json_data):
    ids = []

    if isinstance(json_data, list):
        for item in json_data:
            ids.extend(collect_ids(item))

    elif isinstance(json_data, dict):
        for key, value in json_data.items():
            if key == 'id':
                # Check if the value is an integer
                if isinstance(value, int):
                    ids.append(value)
                # Check if the value is a string and matches the format 'id : number'
                elif isinstance(value, str) and re.match(r"id\s*:\s*\d+$", value):
                    ids.append(int(value.split(':')[1].strip()))

            if isinstance(value, (dict, list)):
                ids.extend(collect_ids(value))

    return ids


def check_if_ids_in_loc_map(numbers, data):
    
    json_numbers = [item[0] for item in data]
    
    for number in numbers:
        if number not in json_numbers:
            return f"ID in ast not found in location map: {number}"
    
    return True



with open('python/ast.json') as json_file:
    ast = json.load(json_file)

    with open('python/location.json') as json_file:
        location = json.load(json_file)
        loc_map_check_result = check_if_ids_in_loc_map(collect_ids(ast), location)
        if loc_map_check_result is not True:
            print(loc_map_check_result)
            sys.exit(1)
        else:
            sys.exit(0)

