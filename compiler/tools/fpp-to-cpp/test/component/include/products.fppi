# ----------------------------------------------------------------------
# Constants and types
# ----------------------------------------------------------------------

@ Data for a DataRecord
struct Data {
  @ A U16 field
  u16Field: U16
}

# ----------------------------------------------------------------------
# Records
# ----------------------------------------------------------------------

@ Record 1
product record U32Record: U32 id 100

@ Record 2
product record DataRecord: Data id 200

@ Record 3
product record U8ArrayRecord: U8 array id 300

# ----------------------------------------------------------------------
# Containers
# ----------------------------------------------------------------------

@ Container 1
product container Container1 id 100 default priority 10

@ Container 2
product container Container2 id 200 default priority 20

@ Container 3
product container Container3 id 300
