import sys

# Expects a list of string arguments representing F Prime guards of the form 
# '-D<name of guard>'
#
# Writes a list of comma-separated lists of compiler flags specifying the 
# values of the F Prime guards to stdout
def gen_guards():
    guards_to_remove = ['-DFW_AMPCS_COMPATIBLE', 
                        '-DFW_CMD_CHECK_RESIDUAL', 
                        '-DFW_ARRAY_TO_STRING']

    # First argument is the name of the script
    guards = sys.argv[1:]
    for guard in guards_to_remove:
        if guard in guards:
            guards.remove(guard)

    guards_list = []
    num_guards = len(guards)
    format_str = f"{num_guards:>03}b"
    i = 0

    while i < (2 ** num_guards):
        values = format(i, format_str)

        new_guards = []
        for j in range(num_guards):
            new_guards.append(f"{guards[j]}={values[j]}")
            
            if guards[j] == '-DFW_SERIALIZABLE_TO_STRING':
                new_guards.append(f"-DFW_ARRAY_TO_STRING={values[j]}")

        guards_list.append(new_guards)
        i = i + 1

    for guards in guards_list:
        guards_str = ','.join(guards)
        sys.stdout.write(f"{guards_str}\n")


if __name__ == '__main__':
    gen_guards()

