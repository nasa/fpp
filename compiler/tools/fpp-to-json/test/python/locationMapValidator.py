import json
import sys

def find_id_in_ast(json_data, target_id):
    # Check if the current level is a list
    if isinstance(json_data, list):
        # Iterate over the list elements
        for item in json_data:
            # Recursively call the function on each item in the list
            result = find_id_in_ast(item, target_id)
            if result:
                return True
    # Check if the current level is a dictionary
    elif isinstance(json_data, dict):
        # Check if the 'id' key is present and matches the target id
        if 'id' in json_data and json_data['id'] == target_id:
            return True
        # Iterate over the dictionary values
        for value in json_data.values():
            # Recursively call the function on each value in the dictionary
            result = find_id_in_ast(value, target_id)
            if result:
                return True
    # Return False if the target id is not found
    return f"ID not found in ast: {target_id}"


def group_ids_by_position(id_pos_list):
    positions = set(entry[1]["pos"] for entry in id_pos_list)
    result = []

    for pos in positions:
        ids = [entry[0] for entry in id_pos_list if entry[1]["pos"] == pos]
        result.append(ids)

    return result



def check_ids_in_dataset(id_list, ast):
    for id_group in id_list:
        found = False
        for id in id_group:
            if(find_id_in_ast(ast, id)):
                found = True
        if not found:
            return id_group
    return True

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

