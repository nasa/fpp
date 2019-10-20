signature TNet_TOKENS =
sig
type ('a,'b) token
type svalue
val U8:  'a * 'a -> (svalue,'a) token
val U64:  'a * 'a -> (svalue,'a) token
val U32:  'a * 'a -> (svalue,'a) token
val U16:  'a * 'a -> (svalue,'a) token
val TYPE:  'a * 'a -> (svalue,'a) token
val SEMI:  'a * 'a -> (svalue,'a) token
val RPAREN:  'a * 'a -> (svalue,'a) token
val RBRACKET:  'a * 'a -> (svalue,'a) token
val RBRACE:  'a * 'a -> (svalue,'a) token
val PRE_ANNOTATION: (string) *  'a * 'a -> (svalue,'a) token
val POST_ANNOTATION: (string) *  'a * 'a -> (svalue,'a) token
val PLUS:  'a * 'a -> (svalue,'a) token
val MODULE:  'a * 'a -> (svalue,'a) token
val MINUS:  'a * 'a -> (svalue,'a) token
val LPAREN:  'a * 'a -> (svalue,'a) token
val LITERAL_INT: (string) *  'a * 'a -> (svalue,'a) token
val LITERAL_FLOAT: (string) *  'a * 'a -> (svalue,'a) token
val LBRACKET:  'a * 'a -> (svalue,'a) token
val LBRACE:  'a * 'a -> (svalue,'a) token
val IDENT: (string) *  'a * 'a -> (svalue,'a) token
val I8:  'a * 'a -> (svalue,'a) token
val I64:  'a * 'a -> (svalue,'a) token
val I32:  'a * 'a -> (svalue,'a) token
val I16:  'a * 'a -> (svalue,'a) token
val F64:  'a * 'a -> (svalue,'a) token
val F32:  'a * 'a -> (svalue,'a) token
val EOL:  'a * 'a -> (svalue,'a) token
val EOF:  'a * 'a -> (svalue,'a) token
val ENUM:  'a * 'a -> (svalue,'a) token
val DOT:  'a * 'a -> (svalue,'a) token
val CONSTANT:  'a * 'a -> (svalue,'a) token
val COMMA:  'a * 'a -> (svalue,'a) token
val COLON:  'a * 'a -> (svalue,'a) token
val ASSIGN:  'a * 'a -> (svalue,'a) token
end
signature TNet_LRVALS=
sig
structure Tokens : TNet_TOKENS
structure ParserData:PARSER_DATA
sharing type ParserData.Token.token = Tokens.token
sharing type ParserData.svalue = Tokens.svalue
end
