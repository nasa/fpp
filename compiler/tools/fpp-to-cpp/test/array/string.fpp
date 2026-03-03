@ An array of strings
array String1 = [3] string

@ An array of strings with specified default value and format string
array String2 = [2] string default ["\"\\", """
abc
def
"""] format "a {} b"

@ An array of arrays of strings
array StringArray = [5] String2
