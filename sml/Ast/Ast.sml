(* ----------------------------------------------------------------------
 * Ast.sml
 * FPP abstract syntax tree
 * ----------------------------------------------------------------------*)

 structure Ast =
 struct

   type 'a node = 'a AstNode.t

   type 'a annotated = string list * 'a * string list

   type ident = string

   datatype trans_unit = TransUnit of tu_member list

   and binop =
     Add
   | Div
   | Mul
   | Sub

   and component_kind = 
     ComponentActive
   | ComponentPassive
   | ComponentQueued

   and component_member = ComponentMember of component_member_node annotated

   and component_member_node =
     ComponentDefArray of def_array node
   | ComponentDefConstant of def_constant node
   | ComponentDefEnum of def_enum node
   | ComponentDefPortInstance of def_port_instance node
   | ComponentDefStruct of def_struct node

   and def_abs_type = DefAbsType of ident

   and def_array = DefArray of ident * expr node * type_name node * expr node option

   and def_component = 
     DefComponent of component_kind * ident * component_member list

   and def_constant = DefConstant of ident * expr node

   and def_enum =
     DefEnum of ident * type_name node option * def_enum_constant node annotated list

   and def_enum_constant = DefEnumConstant of ident * expr node option

   and def_module = DefModule of ident * tu_member list

   and def_port = DefPort of ident * formal_param node annotated list * type_name node option

   and def_port_instance =
     DefPortInstanceGeneral of def_port_instance_general_kind * ident * expr node option * 
       ident list * expr node option * queue_full option
   | DefPortInstanceSpecial of def_port_instance_special_kind * ident

   and def_port_instance_general_kind =
     AsyncInput
   | GuardedInput
   | InternalInput
   | Output
   | SyncInput

   and def_port_instance_special_kind =
     Command
   | CommandReg
   | CommandResp
   | Event
   | ParamGet
   | ParamSet
   | Telemetry
   | Time

   and def_struct = DefStruct of ident * struct_type_member node annotated list * expr node option

   and expr =
     ExprArray of expr node list
   | ExprBinop of expr node * binop * expr node
   | ExprDot of expr node * ident
   | ExprIdent of ident
   | ExprLiteralBool of literal_bool
   | ExprLiteralInt of string
   | ExprLiteralFloat of string
   | ExprLiteralString of string
   | ExprParen of expr node
   | ExprStruct of struct_member list
   | ExprUnop of unop * expr node

   and formal_param = FormalParam of formal_param_kind * ident * type_name node * expr node option

   and formal_param_kind = 
     FormalParamRef
   | FormalParamValue

   and literal_bool = True | False

   and queue_full = 
     Assert
   | Block
   | Drop

   and spec_loc = SpecLoc of spec_loc_kind * ident list * string

   and spec_loc_kind =
     SpecLocConstant
   | SpecLocType
   | SpecLocPort
   | SpecLocComponent
   | SpecLocComponentInstance
   | SpecLocTopology

   and struct_member = StructMember of ident * expr node

   and struct_type_member = StructTypeMember of ident * type_name node

   and tu_member = TUMember of tu_member_node annotated
     
   and tu_member_node = 
     TUDefAbsType of def_abs_type node
   | TUDefArray of def_array node
   | TUDefComponent of def_component node
   | TUDefConstant of def_constant node
   | TUDefEnum of def_enum node
   | TUDefModule of def_module node
   | TUDefPort of def_port node
   | TUDefStruct of def_struct node
   | TUSpecLoc of spec_loc node

   and type_float = F32 | F64

   and type_int = I8 | I16 | I32 | I64 | U8 | U16 | U32 | U64

   and type_name = 
     TypeNameBool
   | TypeNameFloat of type_float
   | TypeNameInt of type_int
   | TypeNameQualIdent of ident list
   | TypeNameString

   and unop =
     Minus

 end
