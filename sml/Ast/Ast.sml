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

   and def_array = DefArray of ident * expr node * type_name node * expr node option

   and def_constant = DefConstant of ident * expr node

   and def_enum =
     DefEnum of ident * type_name node option * enumerator node annotated list

   and def_module = DefModule of ident * tu_member list

   and def_struct = DefStruct of ident * struct_type_member node annotated list * expr option

   and def_abs_type = DefAbsType of ident

   and enumerator = Enumerator of ident * expr node option

   and expr =
     ExprArray of ident list * expr node list
   | ExprDot of expr node * ident
   | ExprIdent of ident
   | ExprLiteral of literal_kind * string
   | ExprStruct of ident list * struct_member node list
   | ExprUnop of unop * expr node

   and literal_kind = LiteralBool | LiteralFloat | LiteralInt | LiteralString

   and spec_loc = SpecLoc of spec_loc_kind * expr node * string

   and spec_loc_kind =
     SpecLocConstant
   | SpecLocType
   | SpecLocPort
   | SpecLocComponent
   | SpecLocComponentInstance
   | SpecLocTopology

   and struct_type_member = StructTypeMember of ident * type_name node

   and struct_member = StructMember of ident * expr node

   and tu_member = TUMember of tu_member_node annotated
     
   and tu_member_node = 
     TUDefAbsType of def_abs_type node
   | TUDefArray of def_array node
   | TUDefConstant of def_constant node
   | TUDefEnum of def_enum node
   | TUDefModule of def_module node
   | TUDefStruct of def_struct node

   and type_bool = true | false

   and type_float = F32 | F64

   and type_int = I8 | I16 | I32 | I64 | U8 | U16 | U32 | U64

   and type_name = 
     TypeNameBool of type_bool
   | TypeNameFloat of type_float
   | TypeNameInt of type_int
   | TypeNameIdent of ident list
   | TypeString

   and unop =
     Minus

 end
