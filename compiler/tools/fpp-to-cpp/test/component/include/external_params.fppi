# ----------------------------------------------------------------------
# Parameters
# ----------------------------------------------------------------------

@ An externally stored parameter with I32 data
@ Second line of annotation
external param ParamI32Ext: I32

@ An externally stored parameter with F64 data
external param ParamF64Ext: F64

@ An externally stored parameter with string data and default value
external param ParamStringExt: string \
  default "external default"

@ An externally stored parameter with enum data
external param ParamEnumExt: E \
  id 0x60

@ An externally stored parameter with array data, default value, and save opcode
external param ParamArrayExt: A \
  default [ 1.0, 2.0, 3.0 ] \
  save opcode 0x65

@ An externally stored parameter with struct data and set/save opcodes
external param ParamStructExt: S \
  set opcode 0x70 \
  save opcode 0x75
