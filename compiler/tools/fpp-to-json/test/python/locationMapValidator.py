import json

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
    return False


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
            print(id_group)
            #return id_group
    return True



with open('ast.json') as json_file:
    ast = json.load(json_file)
    with open('location.json') as json_file:
        location = json.load(json_file)
        print(check_ids_in_dataset(group_ids_by_position(location), ast))

