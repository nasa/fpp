functor TNetLrValsFun(structure Token : TOKEN)
 : sig structure ParserData : PARSER_DATA
       structure Tokens : TNet_TOKENS
   end
 = 
struct
structure ParserData=
struct
structure Header = 
struct
(* ----------------------------------------------------------------------
 * fpp.grm
 * ----------------------------------------------------------------------*) 

open Ast
open AstNode
open Loc

fun pos1 node = 
  let val Loc {pos1,...} = Locations.lookup node
  in pos1 end

fun pos2 node = 
  let val Loc {pos2,...} = Locations.lookup node
  in pos2 end

fun node (e, pos1, pos2) =
let
  val node = AstNode.node e
in
  Locations.insert (node, Loc {
    file=(!ParserState.file),
    pos1=pos1,
    pos2=pos2
  });
  node
end


end
structure LrTable = Token.LrTable
structure Token = Token
local open LrTable in 
val table=let val actionRows =
"\
\\001\000\001\000\162\000\016\000\162\000\000\000\
\\001\000\001\000\163\000\016\000\163\000\000\000\
\\001\000\001\000\164\000\016\000\164\000\000\000\
\\001\000\001\000\165\000\016\000\165\000\000\000\
\\001\000\001\000\166\000\016\000\166\000\000\000\
\\001\000\001\000\167\000\016\000\167\000\000\000\
\\001\000\001\000\168\000\016\000\168\000\000\000\
\\001\000\001\000\169\000\016\000\169\000\000\000\
\\001\000\001\000\170\000\016\000\170\000\000\000\
\\001\000\001\000\171\000\016\000\171\000\000\000\
\\001\000\001\000\172\000\016\000\172\000\000\000\
\\001\000\001\000\173\000\016\000\173\000\000\000\
\\001\000\001\000\174\000\016\000\174\000\000\000\
\\001\000\001\000\042\000\002\000\041\000\000\000\
\\001\000\001\000\088\000\000\000\
\\001\000\001\000\090\000\000\000\
\\001\000\002\000\040\000\016\000\039\000\000\000\
\\001\000\003\000\125\000\007\000\125\000\008\000\125\000\024\000\125\000\
\\026\000\125\000\029\000\125\000\000\000\
\\001\000\003\000\126\000\007\000\126\000\008\000\126\000\024\000\126\000\
\\026\000\126\000\029\000\126\000\000\000\
\\001\000\003\000\127\000\007\000\127\000\008\000\127\000\024\000\127\000\
\\026\000\127\000\029\000\127\000\000\000\
\\001\000\003\000\128\000\007\000\128\000\008\000\128\000\024\000\128\000\
\\026\000\128\000\029\000\128\000\000\000\
\\001\000\003\000\129\000\007\000\129\000\008\000\129\000\024\000\129\000\
\\026\000\129\000\029\000\129\000\000\000\
\\001\000\003\000\130\000\007\000\130\000\008\000\130\000\024\000\130\000\
\\026\000\130\000\029\000\130\000\000\000\
\\001\000\003\000\131\000\007\000\131\000\008\000\131\000\024\000\131\000\
\\026\000\131\000\029\000\131\000\000\000\
\\001\000\003\000\132\000\007\000\132\000\008\000\132\000\024\000\132\000\
\\026\000\132\000\029\000\132\000\000\000\
\\001\000\003\000\133\000\008\000\133\000\024\000\133\000\026\000\133\000\000\000\
\\001\000\003\000\084\000\008\000\083\000\024\000\134\000\026\000\134\000\000\000\
\\001\000\004\000\143\000\006\000\143\000\007\000\143\000\015\000\143\000\
\\022\000\143\000\024\000\022\000\025\000\143\000\026\000\143\000\000\000\
\\001\000\004\000\144\000\006\000\144\000\007\000\144\000\015\000\144\000\
\\022\000\144\000\025\000\144\000\026\000\144\000\000\000\
\\001\000\004\000\145\000\006\000\145\000\015\000\145\000\022\000\145\000\
\\025\000\016\000\000\000\
\\001\000\004\000\146\000\006\000\146\000\015\000\146\000\022\000\146\000\000\000\
\\001\000\004\000\147\000\006\000\147\000\007\000\147\000\022\000\147\000\
\\024\000\147\000\025\000\147\000\026\000\147\000\000\000\
\\001\000\004\000\148\000\006\000\148\000\007\000\148\000\022\000\148\000\
\\024\000\148\000\025\000\148\000\026\000\148\000\000\000\
\\001\000\004\000\152\000\006\000\152\000\007\000\152\000\022\000\152\000\
\\024\000\022\000\025\000\152\000\026\000\152\000\000\000\
\\001\000\004\000\153\000\006\000\153\000\007\000\153\000\022\000\153\000\
\\024\000\022\000\025\000\153\000\026\000\153\000\000\000\
\\001\000\004\000\154\000\006\000\154\000\007\000\154\000\022\000\154\000\
\\025\000\154\000\026\000\154\000\000\000\
\\001\000\004\000\155\000\006\000\155\000\007\000\155\000\022\000\155\000\
\\025\000\155\000\026\000\155\000\000\000\
\\001\000\004\000\156\000\006\000\156\000\007\000\156\000\022\000\156\000\
\\025\000\156\000\026\000\156\000\000\000\
\\001\000\004\000\157\000\006\000\157\000\007\000\157\000\022\000\157\000\
\\025\000\157\000\026\000\157\000\000\000\
\\001\000\004\000\161\000\006\000\161\000\007\000\161\000\022\000\161\000\
\\024\000\161\000\025\000\161\000\026\000\161\000\000\000\
\\001\000\004\000\020\000\006\000\019\000\007\000\149\000\008\000\018\000\
\\022\000\017\000\025\000\016\000\000\000\
\\001\000\004\000\020\000\006\000\019\000\007\000\159\000\022\000\017\000\
\\025\000\016\000\026\000\159\000\000\000\
\\001\000\004\000\020\000\006\000\019\000\022\000\017\000\000\000\
\\001\000\004\000\020\000\006\000\019\000\022\000\017\000\025\000\016\000\
\\026\000\044\000\000\000\
\\001\000\007\000\000\000\000\000\
\\001\000\007\000\101\000\000\000\
\\001\000\007\000\104\000\008\000\104\000\024\000\104\000\026\000\104\000\
\\029\000\104\000\000\000\
\\001\000\007\000\105\000\008\000\105\000\024\000\105\000\026\000\105\000\
\\029\000\105\000\000\000\
\\001\000\007\000\106\000\008\000\106\000\024\000\106\000\026\000\106\000\
\\029\000\106\000\000\000\
\\001\000\007\000\107\000\008\000\107\000\024\000\107\000\026\000\107\000\
\\029\000\107\000\000\000\
\\001\000\007\000\108\000\008\000\108\000\024\000\108\000\026\000\108\000\
\\029\000\108\000\000\000\
\\001\000\007\000\109\000\008\000\109\000\024\000\109\000\026\000\109\000\
\\029\000\109\000\000\000\
\\001\000\007\000\110\000\008\000\110\000\024\000\110\000\026\000\110\000\
\\029\000\110\000\000\000\
\\001\000\007\000\111\000\008\000\111\000\024\000\111\000\026\000\111\000\
\\029\000\111\000\000\000\
\\001\000\007\000\112\000\008\000\112\000\024\000\112\000\026\000\112\000\
\\029\000\112\000\000\000\
\\001\000\007\000\113\000\008\000\113\000\024\000\113\000\026\000\113\000\
\\029\000\113\000\000\000\
\\001\000\007\000\114\000\008\000\114\000\024\000\114\000\026\000\114\000\
\\029\000\114\000\000\000\
\\001\000\007\000\138\000\008\000\029\000\024\000\022\000\026\000\138\000\
\\029\000\028\000\000\000\
\\001\000\007\000\139\000\008\000\029\000\024\000\022\000\026\000\139\000\
\\029\000\028\000\000\000\
\\001\000\007\000\140\000\008\000\140\000\024\000\140\000\026\000\140\000\
\\029\000\140\000\000\000\
\\001\000\007\000\141\000\008\000\141\000\024\000\141\000\026\000\141\000\
\\029\000\141\000\000\000\
\\001\000\007\000\142\000\008\000\142\000\024\000\142\000\026\000\142\000\
\\029\000\142\000\000\000\
\\001\000\007\000\150\000\000\000\
\\001\000\007\000\151\000\000\000\
\\001\000\007\000\158\000\026\000\158\000\000\000\
\\001\000\007\000\160\000\026\000\160\000\000\000\
\\001\000\009\000\068\000\010\000\067\000\011\000\066\000\012\000\065\000\
\\013\000\064\000\014\000\063\000\031\000\062\000\032\000\061\000\
\\033\000\060\000\034\000\059\000\000\000\
\\001\000\015\000\102\000\024\000\102\000\025\000\102\000\026\000\102\000\000\000\
\\001\000\015\000\103\000\024\000\103\000\025\000\103\000\026\000\103\000\000\000\
\\001\000\015\000\115\000\024\000\115\000\025\000\115\000\026\000\115\000\000\000\
\\001\000\015\000\116\000\024\000\116\000\025\000\116\000\026\000\116\000\000\000\
\\001\000\015\000\117\000\024\000\022\000\025\000\117\000\026\000\117\000\000\000\
\\001\000\015\000\118\000\024\000\022\000\025\000\118\000\026\000\118\000\000\000\
\\001\000\015\000\119\000\025\000\119\000\026\000\119\000\000\000\
\\001\000\015\000\120\000\025\000\120\000\026\000\120\000\000\000\
\\001\000\015\000\121\000\025\000\121\000\026\000\121\000\000\000\
\\001\000\015\000\031\000\000\000\
\\001\000\015\000\033\000\000\000\
\\001\000\015\000\034\000\000\000\
\\001\000\015\000\054\000\000\000\
\\001\000\015\000\054\000\025\000\016\000\026\000\123\000\000\000\
\\001\000\015\000\054\000\025\000\016\000\026\000\053\000\000\000\
\\001\000\015\000\054\000\025\000\016\000\026\000\097\000\000\000\
\\001\000\015\000\077\000\018\000\076\000\019\000\075\000\021\000\074\000\000\000\
\\001\000\016\000\038\000\000\000\
\\001\000\016\000\089\000\000\000\
\\001\000\024\000\022\000\026\000\135\000\000\000\
\\001\000\024\000\022\000\026\000\136\000\000\000\
\\001\000\026\000\122\000\000\000\
\\001\000\026\000\124\000\000\000\
\\001\000\026\000\137\000\000\000\
\\001\000\026\000\078\000\000\000\
\\001\000\026\000\085\000\000\000\
\\001\000\026\000\099\000\000\000\
\"
val actionRowNumbers =
"\040\000\033\000\062\000\041\000\
\\045\000\042\000\057\000\064\000\
\\061\000\056\000\060\000\053\000\
\\059\000\048\000\029\000\076\000\
\\040\000\077\000\078\000\035\000\
\\027\000\065\000\034\000\058\000\
\\039\000\036\000\031\000\032\000\
\\030\000\084\000\063\000\016\000\
\\013\000\028\000\037\000\038\000\
\\043\000\081\000\066\000\066\000\
\\083\000\091\000\054\000\079\000\
\\088\000\086\000\026\000\092\000\
\\080\000\071\000\070\000\049\000\
\\014\000\085\000\001\000\000\000\
\\012\000\008\000\011\000\010\000\
\\009\000\004\000\007\000\006\000\
\\005\000\003\000\002\000\015\000\
\\017\000\046\000\022\000\021\000\
\\083\000\020\000\019\000\018\000\
\\055\000\087\000\072\000\074\000\
\\069\000\068\000\067\000\050\000\
\\089\000\073\000\083\000\082\000\
\\083\000\023\000\024\000\090\000\
\\075\000\025\000\093\000\052\000\
\\047\000\051\000\044\000"
val gotoT =
"\
\\001\000\098\000\003\000\013\000\004\000\012\000\005\000\011\000\
\\006\000\010\000\007\000\009\000\008\000\008\000\023\000\007\000\
\\024\000\006\000\026\000\005\000\028\000\004\000\029\000\003\000\
\\030\000\002\000\031\000\001\000\000\000\
\\025\000\019\000\000\000\
\\000\000\
\\003\000\013\000\004\000\012\000\005\000\011\000\006\000\010\000\
\\007\000\009\000\008\000\008\000\023\000\007\000\024\000\006\000\
\\026\000\005\000\029\000\003\000\030\000\021\000\031\000\001\000\000\000\
\\000\000\
\\003\000\013\000\004\000\012\000\005\000\011\000\006\000\010\000\
\\007\000\009\000\008\000\008\000\024\000\023\000\031\000\022\000\000\000\
\\025\000\025\000\027\000\024\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\026\000\028\000\000\000\
\\000\000\
\\003\000\013\000\004\000\012\000\005\000\011\000\006\000\010\000\
\\007\000\009\000\008\000\008\000\023\000\007\000\024\000\006\000\
\\026\000\005\000\028\000\030\000\029\000\003\000\030\000\002\000\
\\031\000\001\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\025\000\033\000\000\000\
\\000\000\
\\025\000\034\000\000\000\
\\025\000\035\000\027\000\024\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\003\000\013\000\004\000\012\000\005\000\011\000\006\000\010\000\
\\007\000\009\000\008\000\008\000\023\000\007\000\024\000\006\000\
\\026\000\005\000\029\000\003\000\030\000\041\000\031\000\001\000\000\000\
\\009\000\050\000\010\000\049\000\011\000\048\000\012\000\047\000\
\\020\000\046\000\021\000\045\000\022\000\044\000\026\000\043\000\000\000\
\\032\000\056\000\033\000\055\000\034\000\054\000\035\000\053\000\000\000\
\\032\000\056\000\033\000\055\000\034\000\054\000\035\000\067\000\000\000\
\\014\000\071\000\015\000\070\000\017\000\069\000\018\000\068\000\000\000\
\\000\000\
\\000\000\
\\009\000\050\000\010\000\078\000\020\000\046\000\021\000\077\000\000\000\
\\000\000\
\\025\000\079\000\000\000\
\\002\000\080\000\000\000\
\\000\000\
\\009\000\050\000\010\000\049\000\011\000\048\000\012\000\084\000\
\\020\000\046\000\021\000\045\000\022\000\044\000\026\000\043\000\000\000\
\\025\000\085\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\015\000\070\000\018\000\090\000\019\000\089\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\025\000\091\000\000\000\
\\025\000\092\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\014\000\071\000\015\000\070\000\017\000\093\000\018\000\068\000\000\000\
\\009\000\050\000\010\000\049\000\011\000\048\000\012\000\094\000\
\\020\000\046\000\021\000\045\000\022\000\044\000\026\000\043\000\000\000\
\\014\000\071\000\015\000\070\000\017\000\096\000\018\000\068\000\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\\000\000\
\"
val numstates = 99
val numrules = 74
val s = ref "" and index = ref 0
val string_to_int = fn () => 
let val i = !index
in index := i+2; Char.ord(String.sub(!s,i)) + Char.ord(String.sub(!s,i+1)) * 256
end
val string_to_list = fn s' =>
    let val len = String.size s'
        fun f () =
           if !index < len then string_to_int() :: f()
           else nil
   in index := 0; s := s'; f ()
   end
val string_to_pairlist = fn (conv_key,conv_entry) =>
     let fun f () =
         case string_to_int()
         of 0 => EMPTY
          | n => PAIR(conv_key (n-1),conv_entry (string_to_int()),f())
     in f
     end
val string_to_pairlist_default = fn (conv_key,conv_entry) =>
    let val conv_row = string_to_pairlist(conv_key,conv_entry)
    in fn () =>
       let val default = conv_entry(string_to_int())
           val row = conv_row()
       in (row,default)
       end
   end
val string_to_table = fn (convert_row,s') =>
    let val len = String.size s'
        fun f ()=
           if !index < len then convert_row() :: f()
           else nil
     in (s := s'; index := 0; f ())
     end
local
  val memo = Array.array(numstates+numrules,ERROR)
  val _ =let fun g i=(Array.update(memo,i,REDUCE(i-numstates)); g(i+1))
       fun f i =
            if i=numstates then g i
            else (Array.update(memo,i,SHIFT (STATE i)); f (i+1))
          in f 0 handle General.Subscript => ()
          end
in
val entry_to_action = fn 0 => ACCEPT | 1 => ERROR | j => Array.sub(memo,(j-2))
end
val gotoT=Array.fromList(string_to_table(string_to_pairlist(NT,STATE),gotoT))
val actionRows=string_to_table(string_to_pairlist_default(T,entry_to_action),actionRows)
val actionRowNumbers = string_to_list actionRowNumbers
val actionT = let val actionRowLookUp=
let val a=Array.fromList(actionRows) in fn i=>Array.sub(a,i) end
in Array.fromList(List.map actionRowLookUp actionRowNumbers)
end
in LrTable.mkLrTable {actions=actionT,gotos=gotoT,numRules=numrules,
numStates=numstates,initialState=STATE 0}
end
end
local open Header in
type pos = int
type arg = unit
structure MlyValue = 
struct
datatype svalue = VOID | ntVOID of unit ->  unit
 | PRE_ANNOTATION of unit ->  (string)
 | POST_ANNOTATION of unit ->  (string)
 | LITERAL_INT of unit ->  (string)
 | LITERAL_FLOAT of unit ->  (string) | IDENT of unit ->  (string)
 | TYPE_NAME_NODE of unit ->  (type_name node)
 | TYPE_NAME_INT of unit ->  (type_name)
 | TYPE_NAME_FLOAT of unit ->  (type_name)
 | TYPE_NAME of unit ->  (type_name)
 | TU_MEMBER_NODE of unit ->  (tu_member_node)
 | TU_MEMBER_LIST of unit ->  (tu_member list)
 | TU_MEMBER of unit ->  (tu_member)
 | TRANS_UNIT of unit ->  (trans_unit)
 | SEMI_OR_EOL of unit ->  (unit)
 | PRE_ANNOTATION_LIST of unit ->  (string list)
 | POST_ANNOTATION_LIST of unit ->  (string list)
 | LAST_TU_MEMBER_NODE of unit ->  (tu_member_node)
 | LAST_TU_MEMBER of unit ->  (tu_member)
 | LAST_ENUMERATOR_NODE_ANNOTATED of unit ->  (enumerator node annotated)
 | LAST_ENUMERATOR_NODE of unit ->  (enumerator node)
 | LAST_ENUMERATOR of unit ->  (enumerator)
 | EXPR_UNOP_NODE of unit ->  (expr node)
 | EXPR_UNOP of unit ->  (expr) | EXPR_NODE of unit ->  (expr node)
 | EXPR_ATOMIC_NODE of unit ->  (expr node)
 | EXPR_ATOMIC of unit ->  (expr) | EXPR of unit ->  (expr)
 | EOL_LBRACE_EOL of unit ->  (unit)
 | ENUMERATOR_NODE_ANNOTATED_LIST of unit ->  (enumerator node annotated list)
 | ENUMERATOR_NODE_ANNOTATED of unit ->  (enumerator node annotated)
 | ENUMERATOR_NODE of unit ->  (enumerator node)
 | ENUMERATOR of unit ->  (enumerator)
 | DEF_MODULE_NODE of unit ->  (def_module node)
 | DEF_MODULE of unit ->  (def_module)
 | DEF_ENUM_NODE of unit ->  (def_enum node)
 | DEF_ENUM of unit ->  (def_enum)
 | DEF_CONSTANT_NODE of unit ->  (def_constant node)
 | DEF_CONSTANT of unit ->  (def_constant)
 | COMMA_OR_EOL of unit ->  (unit) | START of unit ->  (trans_unit)
end
type svalue = MlyValue.svalue
type result = trans_unit
end
structure EC=
struct
open LrTable
infix 5 $$
fun x $$ y = y::x
val is_keyword =
fn _ => false
val preferred_change : (term list * term list) list = 
nil
val noShift = 
fn (T 6) => true | _ => false
val showTerminal =
fn (T 0) => "ASSIGN"
  | (T 1) => "COLON"
  | (T 2) => "COMMA"
  | (T 3) => "CONSTANT"
  | (T 4) => "DOT"
  | (T 5) => "ENUM"
  | (T 6) => "EOF"
  | (T 7) => "EOL"
  | (T 8) => "F32"
  | (T 9) => "F64"
  | (T 10) => "I16"
  | (T 11) => "I32"
  | (T 12) => "I64"
  | (T 13) => "I8"
  | (T 14) => "IDENT"
  | (T 15) => "LBRACE"
  | (T 16) => "LBRACKET"
  | (T 17) => "LITERAL_FLOAT"
  | (T 18) => "LITERAL_INT"
  | (T 19) => "LPAREN"
  | (T 20) => "MINUS"
  | (T 21) => "MODULE"
  | (T 22) => "PLUS"
  | (T 23) => "POST_ANNOTATION"
  | (T 24) => "PRE_ANNOTATION"
  | (T 25) => "RBRACE"
  | (T 26) => "RBRACKET"
  | (T 27) => "RPAREN"
  | (T 28) => "SEMI"
  | (T 29) => "TYPE"
  | (T 30) => "U16"
  | (T 31) => "U32"
  | (T 32) => "U64"
  | (T 33) => "U8"
  | _ => "bogus-term"
local open Header in
val errtermvalue=
fn _ => MlyValue.VOID
end
val terms : term list = nil
 $$ (T 33) $$ (T 32) $$ (T 31) $$ (T 30) $$ (T 29) $$ (T 28) $$ (T 27)
 $$ (T 26) $$ (T 25) $$ (T 22) $$ (T 21) $$ (T 20) $$ (T 19) $$ (T 16)
 $$ (T 15) $$ (T 13) $$ (T 12) $$ (T 11) $$ (T 10) $$ (T 9) $$ (T 8)
 $$ (T 7) $$ (T 6) $$ (T 5) $$ (T 4) $$ (T 3) $$ (T 2) $$ (T 1) $$ (T 
0)end
structure Actions =
struct 
exception mlyAction of int
local open Header in
val actions = 
fn (i392,defaultPos,stack,
    (()):arg) =>
case (i392,stack)
of  ( 0, ( ( _, ( MlyValue.TRANS_UNIT TRANS_UNIT1, TRANS_UNIT1left, 
TRANS_UNIT1right)) :: rest671)) => let val  result = MlyValue.START
 (fn _ => let val  (TRANS_UNIT as TRANS_UNIT1) = TRANS_UNIT1 ()
 in (TRANS_UNIT)
end)
 in ( LrTable.NT 0, ( result, TRANS_UNIT1left, TRANS_UNIT1right), 
rest671)
end
|  ( 1, ( ( _, ( _, COMMA1left, COMMA1right)) :: rest671)) => let val 
 result = MlyValue.COMMA_OR_EOL (fn _ => (()))
 in ( LrTable.NT 1, ( result, COMMA1left, COMMA1right), rest671)
end
|  ( 2, ( ( _, ( _, EOL1left, EOL1right)) :: rest671)) => let val  
result = MlyValue.COMMA_OR_EOL (fn _ => (()))
 in ( LrTable.NT 1, ( result, EOL1left, EOL1right), rest671)
end
|  ( 3, ( ( _, ( MlyValue.EXPR_NODE EXPR_NODE1, _, EXPR_NODE1right))
 :: _ :: ( _, ( MlyValue.IDENT IDENT1, _, _)) :: ( _, ( _, 
CONSTANT1left, _)) :: rest671)) => let val  result = 
MlyValue.DEF_CONSTANT (fn _ => let val  (IDENT as IDENT1) = IDENT1 ()
 val  (EXPR_NODE as EXPR_NODE1) = EXPR_NODE1 ()
 in (DefConstant (IDENT, NONE, EXPR_NODE))
end)
 in ( LrTable.NT 2, ( result, CONSTANT1left, EXPR_NODE1right), rest671
)
end
|  ( 4, ( ( _, ( MlyValue.EXPR_NODE EXPR_NODE1, _, EXPR_NODE1right))
 :: _ :: ( _, ( MlyValue.TYPE_NAME_NODE TYPE_NAME_NODE1, _, _)) :: _
 :: ( _, ( MlyValue.IDENT IDENT1, _, _)) :: ( _, ( _, CONSTANT1left, _
)) :: rest671)) => let val  result = MlyValue.DEF_CONSTANT (fn _ =>
 let val  (IDENT as IDENT1) = IDENT1 ()
 val  (TYPE_NAME_NODE as TYPE_NAME_NODE1) = TYPE_NAME_NODE1 ()
 val  (EXPR_NODE as EXPR_NODE1) = EXPR_NODE1 ()
 in (
      DefConstant (IDENT, SOME TYPE_NAME_NODE, EXPR_NODE)
    )

end)
 in ( LrTable.NT 2, ( result, CONSTANT1left, EXPR_NODE1right), rest671
)
end
|  ( 5, ( ( _, ( MlyValue.DEF_CONSTANT DEF_CONSTANT1, (
DEF_CONSTANTleft as DEF_CONSTANT1left), (DEF_CONSTANTright as 
DEF_CONSTANT1right))) :: rest671)) => let val  result = 
MlyValue.DEF_CONSTANT_NODE (fn _ => let val  (DEF_CONSTANT as 
DEF_CONSTANT1) = DEF_CONSTANT1 ()
 in (node (DEF_CONSTANT, DEF_CONSTANTleft, DEF_CONSTANTright))
end)
 in ( LrTable.NT 3, ( result, DEF_CONSTANT1left, DEF_CONSTANT1right), 
rest671)
end
|  ( 6, ( ( _, ( _, _, RBRACE1right)) :: _ :: ( _, ( MlyValue.IDENT 
IDENT1, _, _)) :: ( _, ( _, ENUM1left, _)) :: rest671)) => let val  
result = MlyValue.DEF_ENUM (fn _ => let val  (IDENT as IDENT1) = 
IDENT1 ()
 in (
      DefEnum (IDENT, NONE, [])
    )
end)
 in ( LrTable.NT 4, ( result, ENUM1left, RBRACE1right), rest671)
end
|  ( 7, ( ( _, ( _, _, RBRACE1right)) :: ( _, ( 
MlyValue.ENUMERATOR_NODE_ANNOTATED_LIST 
ENUMERATOR_NODE_ANNOTATED_LIST1, _, _)) :: _ :: ( _, ( MlyValue.IDENT 
IDENT1, _, _)) :: ( _, ( _, ENUM1left, _)) :: rest671)) => let val  
result = MlyValue.DEF_ENUM (fn _ => let val  (IDENT as IDENT1) = 
IDENT1 ()
 val  (ENUMERATOR_NODE_ANNOTATED_LIST as 
ENUMERATOR_NODE_ANNOTATED_LIST1) = ENUMERATOR_NODE_ANNOTATED_LIST1 ()
 in (
      DefEnum (IDENT, NONE, ENUMERATOR_NODE_ANNOTATED_LIST)
    
)
end)
 in ( LrTable.NT 4, ( result, ENUM1left, RBRACE1right), rest671)
end
|  ( 8, ( ( _, ( _, _, RBRACE1right)) :: ( _, ( 
MlyValue.ENUMERATOR_NODE_ANNOTATED_LIST 
ENUMERATOR_NODE_ANNOTATED_LIST1, _, _)) :: _ :: ( _, ( 
MlyValue.TYPE_NAME_NODE TYPE_NAME_NODE1, _, _)) :: _ :: ( _, ( 
MlyValue.IDENT IDENT1, _, _)) :: ( _, ( _, ENUM1left, _)) :: rest671))
 => let val  result = MlyValue.DEF_ENUM (fn _ => let val  (IDENT as 
IDENT1) = IDENT1 ()
 val  (TYPE_NAME_NODE as TYPE_NAME_NODE1) = TYPE_NAME_NODE1 ()
 val  (ENUMERATOR_NODE_ANNOTATED_LIST as 
ENUMERATOR_NODE_ANNOTATED_LIST1) = ENUMERATOR_NODE_ANNOTATED_LIST1 ()
 in (

      DefEnum (IDENT, SOME TYPE_NAME_NODE, ENUMERATOR_NODE_ANNOTATED_LIST)
    
)
end)
 in ( LrTable.NT 4, ( result, ENUM1left, RBRACE1right), rest671)
end
|  ( 9, ( ( _, ( _, _, RBRACE1right)) :: _ :: ( _, ( 
MlyValue.TYPE_NAME_NODE TYPE_NAME_NODE1, _, _)) :: _ :: ( _, ( 
MlyValue.IDENT IDENT1, _, _)) :: ( _, ( _, ENUM1left, _)) :: rest671))
 => let val  result = MlyValue.DEF_ENUM (fn _ => let val  (IDENT as 
IDENT1) = IDENT1 ()
 val  (TYPE_NAME_NODE as TYPE_NAME_NODE1) = TYPE_NAME_NODE1 ()
 in (
      DefEnum (IDENT, SOME TYPE_NAME_NODE, [])
    )
end)
 in ( LrTable.NT 4, ( result, ENUM1left, RBRACE1right), rest671)
end
|  ( 10, ( ( _, ( MlyValue.DEF_ENUM DEF_ENUM1, (DEF_ENUMleft as 
DEF_ENUM1left), (DEF_ENUMright as DEF_ENUM1right))) :: rest671)) =>
 let val  result = MlyValue.DEF_ENUM_NODE (fn _ => let val  (DEF_ENUM
 as DEF_ENUM1) = DEF_ENUM1 ()
 in (node (DEF_ENUM, DEF_ENUMleft, DEF_ENUMright))
end)
 in ( LrTable.NT 5, ( result, DEF_ENUM1left, DEF_ENUM1right), rest671)

end
|  ( 11, ( ( _, ( _, _, RBRACE1right)) :: _ :: ( _, ( MlyValue.IDENT 
IDENT1, _, _)) :: ( _, ( _, MODULE1left, _)) :: rest671)) => let val  
result = MlyValue.DEF_MODULE (fn _ => let val  (IDENT as IDENT1) = 
IDENT1 ()
 in (DefModule (IDENT, []))
end)
 in ( LrTable.NT 6, ( result, MODULE1left, RBRACE1right), rest671)
end
|  ( 12, ( ( _, ( _, _, RBRACE1right)) :: ( _, ( 
MlyValue.TU_MEMBER_LIST TU_MEMBER_LIST1, _, _)) :: _ :: ( _, ( 
MlyValue.IDENT IDENT1, _, _)) :: ( _, ( _, MODULE1left, _)) :: rest671
)) => let val  result = MlyValue.DEF_MODULE (fn _ => let val  (IDENT
 as IDENT1) = IDENT1 ()
 val  (TU_MEMBER_LIST as TU_MEMBER_LIST1) = TU_MEMBER_LIST1 ()
 in (
      DefModule (IDENT, TU_MEMBER_LIST)
    )
end)
 in ( LrTable.NT 6, ( result, MODULE1left, RBRACE1right), rest671)
end
|  ( 13, ( ( _, ( MlyValue.DEF_MODULE DEF_MODULE1, (DEF_MODULEleft as 
DEF_MODULE1left), (DEF_MODULEright as DEF_MODULE1right))) :: rest671))
 => let val  result = MlyValue.DEF_MODULE_NODE (fn _ => let val  (
DEF_MODULE as DEF_MODULE1) = DEF_MODULE1 ()
 in (node (DEF_MODULE, DEF_MODULEleft, DEF_MODULEright))
end)
 in ( LrTable.NT 7, ( result, DEF_MODULE1left, DEF_MODULE1right), 
rest671)
end
|  ( 14, ( ( _, ( MlyValue.COMMA_OR_EOL COMMA_OR_EOL1, _, 
COMMA_OR_EOL1right)) :: ( _, ( MlyValue.LAST_ENUMERATOR 
LAST_ENUMERATOR1, LAST_ENUMERATOR1left, _)) :: rest671)) => let val  
result = MlyValue.ENUMERATOR (fn _ => let val  (LAST_ENUMERATOR as 
LAST_ENUMERATOR1) = LAST_ENUMERATOR1 ()
 val  COMMA_OR_EOL1 = COMMA_OR_EOL1 ()
 in (LAST_ENUMERATOR)
end)
 in ( LrTable.NT 8, ( result, LAST_ENUMERATOR1left, COMMA_OR_EOL1right
), rest671)
end
|  ( 15, ( ( _, ( MlyValue.ENUMERATOR ENUMERATOR1, (ENUMERATORleft as 
ENUMERATOR1left), (ENUMERATORright as ENUMERATOR1right))) :: rest671))
 => let val  result = MlyValue.ENUMERATOR_NODE (fn _ => let val  (
ENUMERATOR as ENUMERATOR1) = ENUMERATOR1 ()
 in (node (ENUMERATOR, ENUMERATORleft, ENUMERATORright))
end)
 in ( LrTable.NT 9, ( result, ENUMERATOR1left, ENUMERATOR1right), 
rest671)
end
|  ( 16, ( ( _, ( MlyValue.ENUMERATOR_NODE ENUMERATOR_NODE1, 
ENUMERATOR_NODE1left, ENUMERATOR_NODE1right)) :: rest671)) => let val 
 result = MlyValue.ENUMERATOR_NODE_ANNOTATED (fn _ => let val  (
ENUMERATOR_NODE as ENUMERATOR_NODE1) = ENUMERATOR_NODE1 ()
 in (([], ENUMERATOR_NODE, []))
end)
 in ( LrTable.NT 10, ( result, ENUMERATOR_NODE1left, 
ENUMERATOR_NODE1right), rest671)
end
|  ( 17, ( ( _, ( MlyValue.ENUMERATOR_NODE ENUMERATOR_NODE1, _, 
ENUMERATOR_NODE1right)) :: ( _, ( MlyValue.PRE_ANNOTATION_LIST 
PRE_ANNOTATION_LIST1, PRE_ANNOTATION_LIST1left, _)) :: rest671)) =>
 let val  result = MlyValue.ENUMERATOR_NODE_ANNOTATED (fn _ => let
 val  (PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = 
PRE_ANNOTATION_LIST1 ()
 val  (ENUMERATOR_NODE as ENUMERATOR_NODE1) = ENUMERATOR_NODE1 ()
 in (
      (PRE_ANNOTATION_LIST, ENUMERATOR_NODE, [])
    )
end)
 in ( LrTable.NT 10, ( result, PRE_ANNOTATION_LIST1left, 
ENUMERATOR_NODE1right), rest671)
end
|  ( 18, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( MlyValue.ENUMERATOR_NODE 
ENUMERATOR_NODE1, ENUMERATOR_NODE1left, _)) :: rest671)) => let val  
result = MlyValue.ENUMERATOR_NODE_ANNOTATED (fn _ => let val  (
ENUMERATOR_NODE as ENUMERATOR_NODE1) = ENUMERATOR_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (
      ([], ENUMERATOR_NODE, POST_ANNOTATION_LIST)
    )
end)
 in ( LrTable.NT 10, ( result, ENUMERATOR_NODE1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 19, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( 
MlyValue.LAST_ENUMERATOR_NODE LAST_ENUMERATOR_NODE1, 
LAST_ENUMERATOR_NODE1left, _)) :: rest671)) => let val  result = 
MlyValue.ENUMERATOR_NODE_ANNOTATED (fn _ => let val  (
LAST_ENUMERATOR_NODE as LAST_ENUMERATOR_NODE1) = LAST_ENUMERATOR_NODE1
 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (
      ([], LAST_ENUMERATOR_NODE, POST_ANNOTATION_LIST)
    )
end
)
 in ( LrTable.NT 10, ( result, LAST_ENUMERATOR_NODE1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 20, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( MlyValue.ENUMERATOR_NODE 
ENUMERATOR_NODE1, _, _)) :: ( _, ( MlyValue.PRE_ANNOTATION_LIST 
PRE_ANNOTATION_LIST1, PRE_ANNOTATION_LIST1left, _)) :: rest671)) =>
 let val  result = MlyValue.ENUMERATOR_NODE_ANNOTATED (fn _ => let
 val  (PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = 
PRE_ANNOTATION_LIST1 ()
 val  (ENUMERATOR_NODE as ENUMERATOR_NODE1) = ENUMERATOR_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (

      (PRE_ANNOTATION_LIST, ENUMERATOR_NODE, POST_ANNOTATION_LIST)
    
)
end)
 in ( LrTable.NT 10, ( result, PRE_ANNOTATION_LIST1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 21, ( ( _, ( MlyValue.LAST_ENUMERATOR_NODE_ANNOTATED 
LAST_ENUMERATOR_NODE_ANNOTATED1, LAST_ENUMERATOR_NODE_ANNOTATED1left, 
LAST_ENUMERATOR_NODE_ANNOTATED1right)) :: rest671)) => let val  result
 = MlyValue.ENUMERATOR_NODE_ANNOTATED_LIST (fn _ => let val  (
LAST_ENUMERATOR_NODE_ANNOTATED as LAST_ENUMERATOR_NODE_ANNOTATED1) = 
LAST_ENUMERATOR_NODE_ANNOTATED1 ()
 in ([ LAST_ENUMERATOR_NODE_ANNOTATED ])
end)
 in ( LrTable.NT 11, ( result, LAST_ENUMERATOR_NODE_ANNOTATED1left, 
LAST_ENUMERATOR_NODE_ANNOTATED1right), rest671)
end
|  ( 22, ( ( _, ( MlyValue.ENUMERATOR_NODE_ANNOTATED 
ENUMERATOR_NODE_ANNOTATED1, ENUMERATOR_NODE_ANNOTATED1left, 
ENUMERATOR_NODE_ANNOTATED1right)) :: rest671)) => let val  result = 
MlyValue.ENUMERATOR_NODE_ANNOTATED_LIST (fn _ => let val  (
ENUMERATOR_NODE_ANNOTATED as ENUMERATOR_NODE_ANNOTATED1) = 
ENUMERATOR_NODE_ANNOTATED1 ()
 in ([ ENUMERATOR_NODE_ANNOTATED ])
end)
 in ( LrTable.NT 11, ( result, ENUMERATOR_NODE_ANNOTATED1left, 
ENUMERATOR_NODE_ANNOTATED1right), rest671)
end
|  ( 23, ( ( _, ( MlyValue.ENUMERATOR_NODE_ANNOTATED_LIST 
ENUMERATOR_NODE_ANNOTATED_LIST1, _, 
ENUMERATOR_NODE_ANNOTATED_LIST1right)) :: ( _, ( 
MlyValue.ENUMERATOR_NODE_ANNOTATED ENUMERATOR_NODE_ANNOTATED1, 
ENUMERATOR_NODE_ANNOTATED1left, _)) :: rest671)) => let val  result = 
MlyValue.ENUMERATOR_NODE_ANNOTATED_LIST (fn _ => let val  (
ENUMERATOR_NODE_ANNOTATED as ENUMERATOR_NODE_ANNOTATED1) = 
ENUMERATOR_NODE_ANNOTATED1 ()
 val  (ENUMERATOR_NODE_ANNOTATED_LIST as 
ENUMERATOR_NODE_ANNOTATED_LIST1) = ENUMERATOR_NODE_ANNOTATED_LIST1 ()
 in (

      ENUMERATOR_NODE_ANNOTATED :: ENUMERATOR_NODE_ANNOTATED_LIST
    
)
end)
 in ( LrTable.NT 11, ( result, ENUMERATOR_NODE_ANNOTATED1left, 
ENUMERATOR_NODE_ANNOTATED_LIST1right), rest671)
end
|  ( 24, ( ( _, ( MlyValue.EXPR_UNOP EXPR_UNOP1, EXPR_UNOP1left, 
EXPR_UNOP1right)) :: rest671)) => let val  result = MlyValue.EXPR (fn
 _ => let val  (EXPR_UNOP as EXPR_UNOP1) = EXPR_UNOP1 ()
 in (EXPR_UNOP)
end)
 in ( LrTable.NT 13, ( result, EXPR_UNOP1left, EXPR_UNOP1right), 
rest671)
end
|  ( 25, ( ( _, ( MlyValue.IDENT IDENT1, IDENT1left, IDENT1right)) :: 
rest671)) => let val  result = MlyValue.EXPR_ATOMIC (fn _ => let val 
 (IDENT as IDENT1) = IDENT1 ()
 in (ExprIdent IDENT)
end)
 in ( LrTable.NT 14, ( result, IDENT1left, IDENT1right), rest671)
end
|  ( 26, ( ( _, ( MlyValue.LITERAL_FLOAT LITERAL_FLOAT1, 
LITERAL_FLOAT1left, LITERAL_FLOAT1right)) :: rest671)) => let val  
result = MlyValue.EXPR_ATOMIC (fn _ => let val  (LITERAL_FLOAT as 
LITERAL_FLOAT1) = LITERAL_FLOAT1 ()
 in (ExprLiteral (LiteralFloat, LITERAL_FLOAT))
end)
 in ( LrTable.NT 14, ( result, LITERAL_FLOAT1left, LITERAL_FLOAT1right
), rest671)
end
|  ( 27, ( ( _, ( MlyValue.LITERAL_INT LITERAL_INT1, LITERAL_INT1left,
 LITERAL_INT1right)) :: rest671)) => let val  result = 
MlyValue.EXPR_ATOMIC (fn _ => let val  (LITERAL_INT as LITERAL_INT1) =
 LITERAL_INT1 ()
 in (ExprLiteral (LiteralInt, LITERAL_INT))
end)
 in ( LrTable.NT 14, ( result, LITERAL_INT1left, LITERAL_INT1right), 
rest671)
end
|  ( 28, ( ( _, ( MlyValue.EXPR EXPR1, (EXPRleft as EXPR1left), (
EXPRright as EXPR1right))) :: rest671)) => let val  result = 
MlyValue.EXPR_NODE (fn _ => let val  (EXPR as EXPR1) = EXPR1 ()
 in (node (EXPR, EXPRleft, EXPRright))
end)
 in ( LrTable.NT 16, ( result, EXPR1left, EXPR1right), rest671)
end
|  ( 29, ( ( _, ( MlyValue.EXPR_ATOMIC EXPR_ATOMIC1, EXPR_ATOMIC1left,
 EXPR_ATOMIC1right)) :: rest671)) => let val  result = 
MlyValue.EXPR_UNOP (fn _ => let val  (EXPR_ATOMIC as EXPR_ATOMIC1) = 
EXPR_ATOMIC1 ()
 in (EXPR_ATOMIC)
end)
 in ( LrTable.NT 17, ( result, EXPR_ATOMIC1left, EXPR_ATOMIC1right), 
rest671)
end
|  ( 30, ( ( _, ( MlyValue.EXPR_UNOP_NODE EXPR_UNOP_NODE1, _, 
EXPR_UNOP_NODE1right)) :: ( _, ( _, MINUS1left, _)) :: rest671)) =>
 let val  result = MlyValue.EXPR_UNOP (fn _ => let val  (
EXPR_UNOP_NODE as EXPR_UNOP_NODE1) = EXPR_UNOP_NODE1 ()
 in (ExprUnop (Minus, EXPR_UNOP_NODE))
end)
 in ( LrTable.NT 17, ( result, MINUS1left, EXPR_UNOP_NODE1right), 
rest671)
end
|  ( 31, ( ( _, ( MlyValue.EXPR_UNOP EXPR_UNOP1, (EXPR_UNOPleft as 
EXPR_UNOP1left), (EXPR_UNOPright as EXPR_UNOP1right))) :: rest671)) =>
 let val  result = MlyValue.EXPR_UNOP_NODE (fn _ => let val  (
EXPR_UNOP as EXPR_UNOP1) = EXPR_UNOP1 ()
 in (node (EXPR_UNOP, EXPR_UNOPleft, EXPR_UNOPright))
end)
 in ( LrTable.NT 18, ( result, EXPR_UNOP1left, EXPR_UNOP1right), 
rest671)
end
|  ( 32, ( ( _, ( MlyValue.EXPR_NODE EXPR_NODE1, _, EXPR_NODE1right))
 :: _ :: ( _, ( MlyValue.IDENT IDENT1, IDENT1left, _)) :: rest671)) =>
 let val  result = MlyValue.LAST_ENUMERATOR (fn _ => let val  (IDENT
 as IDENT1) = IDENT1 ()
 val  (EXPR_NODE as EXPR_NODE1) = EXPR_NODE1 ()
 in (Enumerator (IDENT, EXPR_NODE))
end)
 in ( LrTable.NT 19, ( result, IDENT1left, EXPR_NODE1right), rest671)

end
|  ( 33, ( ( _, ( MlyValue.LAST_ENUMERATOR LAST_ENUMERATOR1, (
LAST_ENUMERATORleft as LAST_ENUMERATOR1left), (LAST_ENUMERATORright
 as LAST_ENUMERATOR1right))) :: rest671)) => let val  result = 
MlyValue.LAST_ENUMERATOR_NODE (fn _ => let val  (LAST_ENUMERATOR as 
LAST_ENUMERATOR1) = LAST_ENUMERATOR1 ()
 in (node (LAST_ENUMERATOR, LAST_ENUMERATORleft, LAST_ENUMERATORright)
)
end)
 in ( LrTable.NT 20, ( result, LAST_ENUMERATOR1left, 
LAST_ENUMERATOR1right), rest671)
end
|  ( 34, ( ( _, ( MlyValue.LAST_ENUMERATOR_NODE LAST_ENUMERATOR_NODE1,
 LAST_ENUMERATOR_NODE1left, LAST_ENUMERATOR_NODE1right)) :: rest671))
 => let val  result = MlyValue.LAST_ENUMERATOR_NODE_ANNOTATED (fn _ =>
 let val  (LAST_ENUMERATOR_NODE as LAST_ENUMERATOR_NODE1) = 
LAST_ENUMERATOR_NODE1 ()
 in (([], LAST_ENUMERATOR_NODE, []))
end)
 in ( LrTable.NT 21, ( result, LAST_ENUMERATOR_NODE1left, 
LAST_ENUMERATOR_NODE1right), rest671)
end
|  ( 35, ( ( _, ( MlyValue.LAST_ENUMERATOR_NODE LAST_ENUMERATOR_NODE1,
 _, LAST_ENUMERATOR_NODE1right)) :: ( _, ( 
MlyValue.PRE_ANNOTATION_LIST PRE_ANNOTATION_LIST1, 
PRE_ANNOTATION_LIST1left, _)) :: rest671)) => let val  result = 
MlyValue.LAST_ENUMERATOR_NODE_ANNOTATED (fn _ => let val  (
PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = PRE_ANNOTATION_LIST1 ()
 val  (LAST_ENUMERATOR_NODE as LAST_ENUMERATOR_NODE1) = 
LAST_ENUMERATOR_NODE1 ()
 in (
      (PRE_ANNOTATION_LIST, LAST_ENUMERATOR_NODE, [])
    )
end)
 in ( LrTable.NT 21, ( result, PRE_ANNOTATION_LIST1left, 
LAST_ENUMERATOR_NODE1right), rest671)
end
|  ( 36, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( 
MlyValue.LAST_ENUMERATOR_NODE LAST_ENUMERATOR_NODE1, _, _)) :: ( _, ( 
MlyValue.PRE_ANNOTATION_LIST PRE_ANNOTATION_LIST1, 
PRE_ANNOTATION_LIST1left, _)) :: rest671)) => let val  result = 
MlyValue.LAST_ENUMERATOR_NODE_ANNOTATED (fn _ => let val  (
PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = PRE_ANNOTATION_LIST1 ()
 val  (LAST_ENUMERATOR_NODE as LAST_ENUMERATOR_NODE1) = 
LAST_ENUMERATOR_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (

      (PRE_ANNOTATION_LIST, LAST_ENUMERATOR_NODE, POST_ANNOTATION_LIST)
    
)
end)
 in ( LrTable.NT 21, ( result, PRE_ANNOTATION_LIST1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 37, ( ( _, ( MlyValue.LAST_TU_MEMBER_NODE LAST_TU_MEMBER_NODE1, 
LAST_TU_MEMBER_NODE1left, LAST_TU_MEMBER_NODE1right)) :: rest671)) =>
 let val  result = MlyValue.LAST_TU_MEMBER (fn _ => let val  (
LAST_TU_MEMBER_NODE as LAST_TU_MEMBER_NODE1) = LAST_TU_MEMBER_NODE1 ()
 in (TUMember ([], LAST_TU_MEMBER_NODE, []))
end)
 in ( LrTable.NT 22, ( result, LAST_TU_MEMBER_NODE1left, 
LAST_TU_MEMBER_NODE1right), rest671)
end
|  ( 38, ( ( _, ( MlyValue.LAST_TU_MEMBER_NODE LAST_TU_MEMBER_NODE1, _
, LAST_TU_MEMBER_NODE1right)) :: ( _, ( MlyValue.PRE_ANNOTATION_LIST 
PRE_ANNOTATION_LIST1, PRE_ANNOTATION_LIST1left, _)) :: rest671)) =>
 let val  result = MlyValue.LAST_TU_MEMBER (fn _ => let val  (
PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = PRE_ANNOTATION_LIST1 ()
 val  (LAST_TU_MEMBER_NODE as LAST_TU_MEMBER_NODE1) = 
LAST_TU_MEMBER_NODE1 ()
 in (

      TUMember (PRE_ANNOTATION_LIST, LAST_TU_MEMBER_NODE, [])
    )

end)
 in ( LrTable.NT 22, ( result, PRE_ANNOTATION_LIST1left, 
LAST_TU_MEMBER_NODE1right), rest671)
end
|  ( 39, ( ( _, ( MlyValue.DEF_CONSTANT_NODE DEF_CONSTANT_NODE1, 
DEF_CONSTANT_NODE1left, DEF_CONSTANT_NODE1right)) :: rest671)) => let
 val  result = MlyValue.LAST_TU_MEMBER_NODE (fn _ => let val  (
DEF_CONSTANT_NODE as DEF_CONSTANT_NODE1) = DEF_CONSTANT_NODE1 ()
 in (TUDefConstant DEF_CONSTANT_NODE)
end)
 in ( LrTable.NT 23, ( result, DEF_CONSTANT_NODE1left, 
DEF_CONSTANT_NODE1right), rest671)
end
|  ( 40, ( ( _, ( MlyValue.DEF_ENUM_NODE DEF_ENUM_NODE1, 
DEF_ENUM_NODE1left, DEF_ENUM_NODE1right)) :: rest671)) => let val  
result = MlyValue.LAST_TU_MEMBER_NODE (fn _ => let val  (DEF_ENUM_NODE
 as DEF_ENUM_NODE1) = DEF_ENUM_NODE1 ()
 in (TUDefEnum DEF_ENUM_NODE)
end)
 in ( LrTable.NT 23, ( result, DEF_ENUM_NODE1left, DEF_ENUM_NODE1right
), rest671)
end
|  ( 41, ( ( _, ( MlyValue.DEF_MODULE_NODE DEF_MODULE_NODE1, 
DEF_MODULE_NODE1left, DEF_MODULE_NODE1right)) :: rest671)) => let val 
 result = MlyValue.LAST_TU_MEMBER_NODE (fn _ => let val  (
DEF_MODULE_NODE as DEF_MODULE_NODE1) = DEF_MODULE_NODE1 ()
 in (TUDefModule DEF_MODULE_NODE)
end)
 in ( LrTable.NT 23, ( result, DEF_MODULE_NODE1left, 
DEF_MODULE_NODE1right), rest671)
end
|  ( 42, ( ( _, ( MlyValue.POST_ANNOTATION POST_ANNOTATION1, 
POST_ANNOTATION1left, POST_ANNOTATION1right)) :: rest671)) => let val 
 result = MlyValue.POST_ANNOTATION_LIST (fn _ => let val  (
POST_ANNOTATION as POST_ANNOTATION1) = POST_ANNOTATION1 ()
 in ([ POST_ANNOTATION ])
end)
 in ( LrTable.NT 24, ( result, POST_ANNOTATION1left, 
POST_ANNOTATION1right), rest671)
end
|  ( 43, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( MlyValue.POST_ANNOTATION 
POST_ANNOTATION1, POST_ANNOTATION1left, _)) :: rest671)) => let val  
result = MlyValue.POST_ANNOTATION_LIST (fn _ => let val  (
POST_ANNOTATION as POST_ANNOTATION1) = POST_ANNOTATION1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (POST_ANNOTATION :: POST_ANNOTATION_LIST)
end)
 in ( LrTable.NT 24, ( result, POST_ANNOTATION1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 44, ( ( _, ( MlyValue.PRE_ANNOTATION PRE_ANNOTATION1, 
PRE_ANNOTATION1left, PRE_ANNOTATION1right)) :: rest671)) => let val  
result = MlyValue.PRE_ANNOTATION_LIST (fn _ => let val  (
PRE_ANNOTATION as PRE_ANNOTATION1) = PRE_ANNOTATION1 ()
 in ([ PRE_ANNOTATION ])
end)
 in ( LrTable.NT 25, ( result, PRE_ANNOTATION1left, 
PRE_ANNOTATION1right), rest671)
end
|  ( 45, ( ( _, ( MlyValue.PRE_ANNOTATION_LIST PRE_ANNOTATION_LIST1, _
, PRE_ANNOTATION_LIST1right)) :: ( _, ( MlyValue.PRE_ANNOTATION 
PRE_ANNOTATION1, PRE_ANNOTATION1left, _)) :: rest671)) => let val  
result = MlyValue.PRE_ANNOTATION_LIST (fn _ => let val  (
PRE_ANNOTATION as PRE_ANNOTATION1) = PRE_ANNOTATION1 ()
 val  (PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = 
PRE_ANNOTATION_LIST1 ()
 in (PRE_ANNOTATION :: PRE_ANNOTATION_LIST)
end)
 in ( LrTable.NT 25, ( result, PRE_ANNOTATION1left, 
PRE_ANNOTATION_LIST1right), rest671)
end
|  ( 46, ( ( _, ( _, SEMI1left, SEMI1right)) :: rest671)) => let val  
result = MlyValue.SEMI_OR_EOL (fn _ => (()))
 in ( LrTable.NT 26, ( result, SEMI1left, SEMI1right), rest671)
end
|  ( 47, ( ( _, ( _, EOL1left, EOL1right)) :: rest671)) => let val  
result = MlyValue.SEMI_OR_EOL (fn _ => (()))
 in ( LrTable.NT 26, ( result, EOL1left, EOL1right), rest671)
end
|  ( 48, ( rest671)) => let val  result = MlyValue.TRANS_UNIT (fn _ =>
 (TransUnit []))
 in ( LrTable.NT 27, ( result, defaultPos, defaultPos), rest671)
end
|  ( 49, ( ( _, ( MlyValue.TU_MEMBER_LIST TU_MEMBER_LIST1, 
TU_MEMBER_LIST1left, TU_MEMBER_LIST1right)) :: rest671)) => let val  
result = MlyValue.TRANS_UNIT (fn _ => let val  (TU_MEMBER_LIST as 
TU_MEMBER_LIST1) = TU_MEMBER_LIST1 ()
 in (TransUnit TU_MEMBER_LIST)
end)
 in ( LrTable.NT 27, ( result, TU_MEMBER_LIST1left, 
TU_MEMBER_LIST1right), rest671)
end
|  ( 50, ( ( _, ( MlyValue.TRANS_UNIT TRANS_UNIT1, _, TRANS_UNIT1right
)) :: ( _, ( _, EOL1left, _)) :: rest671)) => let val  result = 
MlyValue.TRANS_UNIT (fn _ => let val  (TRANS_UNIT as TRANS_UNIT1) = 
TRANS_UNIT1 ()
 in (TRANS_UNIT)
end)
 in ( LrTable.NT 27, ( result, EOL1left, TRANS_UNIT1right), rest671)

end
|  ( 51, ( ( _, ( MlyValue.TU_MEMBER_NODE TU_MEMBER_NODE1, 
TU_MEMBER_NODE1left, TU_MEMBER_NODE1right)) :: rest671)) => let val  
result = MlyValue.TU_MEMBER (fn _ => let val  (TU_MEMBER_NODE as 
TU_MEMBER_NODE1) = TU_MEMBER_NODE1 ()
 in (TUMember ([], TU_MEMBER_NODE, []))
end)
 in ( LrTable.NT 28, ( result, TU_MEMBER_NODE1left, 
TU_MEMBER_NODE1right), rest671)
end
|  ( 52, ( ( _, ( MlyValue.TU_MEMBER_NODE TU_MEMBER_NODE1, _, 
TU_MEMBER_NODE1right)) :: ( _, ( MlyValue.PRE_ANNOTATION_LIST 
PRE_ANNOTATION_LIST1, PRE_ANNOTATION_LIST1left, _)) :: rest671)) =>
 let val  result = MlyValue.TU_MEMBER (fn _ => let val  (
PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = PRE_ANNOTATION_LIST1 ()
 val  (TU_MEMBER_NODE as TU_MEMBER_NODE1) = TU_MEMBER_NODE1 ()
 in (
      TUMember (PRE_ANNOTATION_LIST, TU_MEMBER_NODE, [])
    )

end)
 in ( LrTable.NT 28, ( result, PRE_ANNOTATION_LIST1left, 
TU_MEMBER_NODE1right), rest671)
end
|  ( 53, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( MlyValue.TU_MEMBER_NODE 
TU_MEMBER_NODE1, TU_MEMBER_NODE1left, _)) :: rest671)) => let val  
result = MlyValue.TU_MEMBER (fn _ => let val  (TU_MEMBER_NODE as 
TU_MEMBER_NODE1) = TU_MEMBER_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (
      TUMember ([], TU_MEMBER_NODE, POST_ANNOTATION_LIST)
    )

end)
 in ( LrTable.NT 28, ( result, TU_MEMBER_NODE1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 54, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( 
MlyValue.LAST_TU_MEMBER_NODE LAST_TU_MEMBER_NODE1, 
LAST_TU_MEMBER_NODE1left, _)) :: rest671)) => let val  result = 
MlyValue.TU_MEMBER (fn _ => let val  (LAST_TU_MEMBER_NODE as 
LAST_TU_MEMBER_NODE1) = LAST_TU_MEMBER_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (

      TUMember ([], LAST_TU_MEMBER_NODE, POST_ANNOTATION_LIST)
    )

end)
 in ( LrTable.NT 28, ( result, LAST_TU_MEMBER_NODE1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 55, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( MlyValue.TU_MEMBER_NODE 
TU_MEMBER_NODE1, _, _)) :: ( _, ( MlyValue.PRE_ANNOTATION_LIST 
PRE_ANNOTATION_LIST1, PRE_ANNOTATION_LIST1left, _)) :: rest671)) =>
 let val  result = MlyValue.TU_MEMBER (fn _ => let val  (
PRE_ANNOTATION_LIST as PRE_ANNOTATION_LIST1) = PRE_ANNOTATION_LIST1 ()
 val  (TU_MEMBER_NODE as TU_MEMBER_NODE1) = TU_MEMBER_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (

      TUMember (PRE_ANNOTATION_LIST, TU_MEMBER_NODE, POST_ANNOTATION_LIST)
    
)
end)
 in ( LrTable.NT 28, ( result, PRE_ANNOTATION_LIST1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 56, ( ( _, ( MlyValue.POST_ANNOTATION_LIST POST_ANNOTATION_LIST1,
 _, POST_ANNOTATION_LIST1right)) :: ( _, ( 
MlyValue.LAST_TU_MEMBER_NODE LAST_TU_MEMBER_NODE1, _, _)) :: ( _, ( 
MlyValue.PRE_ANNOTATION_LIST PRE_ANNOTATION_LIST1, 
PRE_ANNOTATION_LIST1left, _)) :: rest671)) => let val  result = 
MlyValue.TU_MEMBER (fn _ => let val  (PRE_ANNOTATION_LIST as 
PRE_ANNOTATION_LIST1) = PRE_ANNOTATION_LIST1 ()
 val  (LAST_TU_MEMBER_NODE as LAST_TU_MEMBER_NODE1) = 
LAST_TU_MEMBER_NODE1 ()
 val  (POST_ANNOTATION_LIST as POST_ANNOTATION_LIST1) = 
POST_ANNOTATION_LIST1 ()
 in (

      TUMember (PRE_ANNOTATION_LIST, LAST_TU_MEMBER_NODE, POST_ANNOTATION_LIST)
    
)
end)
 in ( LrTable.NT 28, ( result, PRE_ANNOTATION_LIST1left, 
POST_ANNOTATION_LIST1right), rest671)
end
|  ( 57, ( ( _, ( MlyValue.LAST_TU_MEMBER LAST_TU_MEMBER1, 
LAST_TU_MEMBER1left, LAST_TU_MEMBER1right)) :: rest671)) => let val  
result = MlyValue.TU_MEMBER_LIST (fn _ => let val  (LAST_TU_MEMBER as 
LAST_TU_MEMBER1) = LAST_TU_MEMBER1 ()
 in ([ LAST_TU_MEMBER ])
end)
 in ( LrTable.NT 29, ( result, LAST_TU_MEMBER1left, 
LAST_TU_MEMBER1right), rest671)
end
|  ( 58, ( ( _, ( MlyValue.TU_MEMBER TU_MEMBER1, TU_MEMBER1left, 
TU_MEMBER1right)) :: rest671)) => let val  result = 
MlyValue.TU_MEMBER_LIST (fn _ => let val  (TU_MEMBER as TU_MEMBER1) = 
TU_MEMBER1 ()
 in ([ TU_MEMBER ])
end)
 in ( LrTable.NT 29, ( result, TU_MEMBER1left, TU_MEMBER1right), 
rest671)
end
|  ( 59, ( ( _, ( MlyValue.TU_MEMBER_LIST TU_MEMBER_LIST1, _, 
TU_MEMBER_LIST1right)) :: ( _, ( MlyValue.TU_MEMBER TU_MEMBER1, 
TU_MEMBER1left, _)) :: rest671)) => let val  result = 
MlyValue.TU_MEMBER_LIST (fn _ => let val  (TU_MEMBER as TU_MEMBER1) = 
TU_MEMBER1 ()
 val  (TU_MEMBER_LIST as TU_MEMBER_LIST1) = TU_MEMBER_LIST1 ()
 in (TU_MEMBER :: TU_MEMBER_LIST)
end)
 in ( LrTable.NT 29, ( result, TU_MEMBER1left, TU_MEMBER_LIST1right), 
rest671)
end
|  ( 60, ( ( _, ( MlyValue.SEMI_OR_EOL SEMI_OR_EOL1, _, 
SEMI_OR_EOL1right)) :: ( _, ( MlyValue.LAST_TU_MEMBER_NODE 
LAST_TU_MEMBER_NODE1, LAST_TU_MEMBER_NODE1left, _)) :: rest671)) =>
 let val  result = MlyValue.TU_MEMBER_NODE (fn _ => let val  (
LAST_TU_MEMBER_NODE as LAST_TU_MEMBER_NODE1) = LAST_TU_MEMBER_NODE1 ()
 val  SEMI_OR_EOL1 = SEMI_OR_EOL1 ()
 in (LAST_TU_MEMBER_NODE)
end)
 in ( LrTable.NT 30, ( result, LAST_TU_MEMBER_NODE1left, 
SEMI_OR_EOL1right), rest671)
end
|  ( 61, ( ( _, ( MlyValue.TYPE_NAME_FLOAT TYPE_NAME_FLOAT1, 
TYPE_NAME_FLOAT1left, TYPE_NAME_FLOAT1right)) :: rest671)) => let val 
 result = MlyValue.TYPE_NAME (fn _ => let val  (TYPE_NAME_FLOAT as 
TYPE_NAME_FLOAT1) = TYPE_NAME_FLOAT1 ()
 in (TYPE_NAME_FLOAT)
end)
 in ( LrTable.NT 31, ( result, TYPE_NAME_FLOAT1left, 
TYPE_NAME_FLOAT1right), rest671)
end
|  ( 62, ( ( _, ( MlyValue.TYPE_NAME_INT TYPE_NAME_INT1, 
TYPE_NAME_INT1left, TYPE_NAME_INT1right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME (fn _ => let val  (TYPE_NAME_INT as 
TYPE_NAME_INT1) = TYPE_NAME_INT1 ()
 in (TYPE_NAME_INT)
end)
 in ( LrTable.NT 31, ( result, TYPE_NAME_INT1left, TYPE_NAME_INT1right
), rest671)
end
|  ( 63, ( ( _, ( _, F321left, F321right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_FLOAT (fn _ => (TypeNameFloat F32))
 in ( LrTable.NT 32, ( result, F321left, F321right), rest671)
end
|  ( 64, ( ( _, ( _, F641left, F641right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_FLOAT (fn _ => (TypeNameFloat F64))
 in ( LrTable.NT 32, ( result, F641left, F641right), rest671)
end
|  ( 65, ( ( _, ( _, I81left, I81right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt I8))
 in ( LrTable.NT 33, ( result, I81left, I81right), rest671)
end
|  ( 66, ( ( _, ( _, I161left, I161right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt I16))
 in ( LrTable.NT 33, ( result, I161left, I161right), rest671)
end
|  ( 67, ( ( _, ( _, I321left, I321right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt I32))
 in ( LrTable.NT 33, ( result, I321left, I321right), rest671)
end
|  ( 68, ( ( _, ( _, I641left, I641right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt I64))
 in ( LrTable.NT 33, ( result, I641left, I641right), rest671)
end
|  ( 69, ( ( _, ( _, U81left, U81right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt U8))
 in ( LrTable.NT 33, ( result, U81left, U81right), rest671)
end
|  ( 70, ( ( _, ( _, U161left, U161right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt U16))
 in ( LrTable.NT 33, ( result, U161left, U161right), rest671)
end
|  ( 71, ( ( _, ( _, U321left, U321right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt U32))
 in ( LrTable.NT 33, ( result, U321left, U321right), rest671)
end
|  ( 72, ( ( _, ( _, U641left, U641right)) :: rest671)) => let val  
result = MlyValue.TYPE_NAME_INT (fn _ => (TypeNameInt U64))
 in ( LrTable.NT 33, ( result, U641left, U641right), rest671)
end
|  ( 73, ( ( _, ( MlyValue.TYPE_NAME TYPE_NAME1, (TYPE_NAMEleft as 
TYPE_NAME1left), (TYPE_NAMEright as TYPE_NAME1right))) :: rest671)) =>
 let val  result = MlyValue.TYPE_NAME_NODE (fn _ => let val  (
TYPE_NAME as TYPE_NAME1) = TYPE_NAME1 ()
 in (node (TYPE_NAME, TYPE_NAMEleft, TYPE_NAMEright))
end)
 in ( LrTable.NT 34, ( result, TYPE_NAME1left, TYPE_NAME1right), 
rest671)
end
| _ => raise (mlyAction i392)
end
val void = MlyValue.VOID
val extract = fn a => (fn MlyValue.START x => x
| _ => let exception ParseInternal
	in raise ParseInternal end) a ()
end
end
structure Tokens : TNet_TOKENS =
struct
type svalue = ParserData.svalue
type ('a,'b) token = ('a,'b) Token.token
fun ASSIGN (p1,p2) = Token.TOKEN (ParserData.LrTable.T 0,(
ParserData.MlyValue.VOID,p1,p2))
fun COLON (p1,p2) = Token.TOKEN (ParserData.LrTable.T 1,(
ParserData.MlyValue.VOID,p1,p2))
fun COMMA (p1,p2) = Token.TOKEN (ParserData.LrTable.T 2,(
ParserData.MlyValue.VOID,p1,p2))
fun CONSTANT (p1,p2) = Token.TOKEN (ParserData.LrTable.T 3,(
ParserData.MlyValue.VOID,p1,p2))
fun DOT (p1,p2) = Token.TOKEN (ParserData.LrTable.T 4,(
ParserData.MlyValue.VOID,p1,p2))
fun ENUM (p1,p2) = Token.TOKEN (ParserData.LrTable.T 5,(
ParserData.MlyValue.VOID,p1,p2))
fun EOF (p1,p2) = Token.TOKEN (ParserData.LrTable.T 6,(
ParserData.MlyValue.VOID,p1,p2))
fun EOL (p1,p2) = Token.TOKEN (ParserData.LrTable.T 7,(
ParserData.MlyValue.VOID,p1,p2))
fun F32 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 8,(
ParserData.MlyValue.VOID,p1,p2))
fun F64 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 9,(
ParserData.MlyValue.VOID,p1,p2))
fun I16 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 10,(
ParserData.MlyValue.VOID,p1,p2))
fun I32 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 11,(
ParserData.MlyValue.VOID,p1,p2))
fun I64 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 12,(
ParserData.MlyValue.VOID,p1,p2))
fun I8 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 13,(
ParserData.MlyValue.VOID,p1,p2))
fun IDENT (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 14,(
ParserData.MlyValue.IDENT (fn () => i),p1,p2))
fun LBRACE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 15,(
ParserData.MlyValue.VOID,p1,p2))
fun LBRACKET (p1,p2) = Token.TOKEN (ParserData.LrTable.T 16,(
ParserData.MlyValue.VOID,p1,p2))
fun LITERAL_FLOAT (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 17,(
ParserData.MlyValue.LITERAL_FLOAT (fn () => i),p1,p2))
fun LITERAL_INT (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 18,(
ParserData.MlyValue.LITERAL_INT (fn () => i),p1,p2))
fun LPAREN (p1,p2) = Token.TOKEN (ParserData.LrTable.T 19,(
ParserData.MlyValue.VOID,p1,p2))
fun MINUS (p1,p2) = Token.TOKEN (ParserData.LrTable.T 20,(
ParserData.MlyValue.VOID,p1,p2))
fun MODULE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 21,(
ParserData.MlyValue.VOID,p1,p2))
fun PLUS (p1,p2) = Token.TOKEN (ParserData.LrTable.T 22,(
ParserData.MlyValue.VOID,p1,p2))
fun POST_ANNOTATION (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 23,(
ParserData.MlyValue.POST_ANNOTATION (fn () => i),p1,p2))
fun PRE_ANNOTATION (i,p1,p2) = Token.TOKEN (ParserData.LrTable.T 24,(
ParserData.MlyValue.PRE_ANNOTATION (fn () => i),p1,p2))
fun RBRACE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 25,(
ParserData.MlyValue.VOID,p1,p2))
fun RBRACKET (p1,p2) = Token.TOKEN (ParserData.LrTable.T 26,(
ParserData.MlyValue.VOID,p1,p2))
fun RPAREN (p1,p2) = Token.TOKEN (ParserData.LrTable.T 27,(
ParserData.MlyValue.VOID,p1,p2))
fun SEMI (p1,p2) = Token.TOKEN (ParserData.LrTable.T 28,(
ParserData.MlyValue.VOID,p1,p2))
fun TYPE (p1,p2) = Token.TOKEN (ParserData.LrTable.T 29,(
ParserData.MlyValue.VOID,p1,p2))
fun U16 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 30,(
ParserData.MlyValue.VOID,p1,p2))
fun U32 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 31,(
ParserData.MlyValue.VOID,p1,p2))
fun U64 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 32,(
ParserData.MlyValue.VOID,p1,p2))
fun U8 (p1,p2) = Token.TOKEN (ParserData.LrTable.T 33,(
ParserData.MlyValue.VOID,p1,p2))
end
end
