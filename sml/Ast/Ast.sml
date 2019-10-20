(* ----------------------------------------------------------------------
 * Ast.sml
 * Author: Rob Bocchino 
 * ----------------------------------------------------------------------*)

 structure Ast =
 struct

   type 'a node = 'a AstNode.t

   type 'a annotated = string list * 'a * string list

   type ident = string

   fun deannotate (_, x, _) = x

   datatype trans_unit = TransUnit of tu_member list

   and def_constant = DefConstant of ident * type_name node option * expr node

   and def_enum =
     DefEnum of ident * type_name node option * enumerator node annotated list

   and def_module = DefModule of ident * tu_member list

   and def_type = DefType of ident * type_name node

   and enumerator = Enumerator of ident * expr node

   and expr =
     ExprDot of expr node * ident
   | ExprIdent of ident
   | ExprLiteral of literal_kind * string
   | ExprUnop of unop * expr node

   and literal_kind = LiteralFloat | LiteralInt

   and tu_member = TUMember of tu_member_node annotated
     
   and tu_member_node = 
     TUDefConstant of def_constant node
   | TUDefEnum of def_enum node
   | TUDefModule of def_module node

   and type_float = F32 | F64

   and type_int = I8 | I16 | I32 | I64 | U8 | U16 | U32 | U64

   and type_name = 
     TypeNameFloat of type_float
   | TypeNameInt of type_int
   | TypeNameIdent of ident list

   and unop =
     Minus

 end
