structure Tokens = Tokens

open Loc
open TextIO

type pos = int
type svalue = Tokens.svalue
type ('a,'b) token = ('a,'b) Tokens.token
type lexresult= (svalue,pos) token

val pos = ParserState.pos
val file = ParserState.file
fun newline () = (pos := (!pos) + 1)
fun newlines s =
let
  fun f c = (c = #"\n")
  val lst = String.explode s
  val lst = List.filter f lst
  val n = List.length lst
in
  pos := (!pos) + n
end
val eof = fn () => Tokens.EOF(!pos, !pos)

fun token t = t (!pos, !pos)
fun syntaxError s =
let 
  val loc = Loc {file = !file, pos1 = !pos, pos2 = !pos}
in 
  raise Error.SyntaxError (loc, s) 
end

%%
%header (functor TNetLexFun(structure Tokens: TNet_TOKENS));
alpha=[A-Za-z];
digit=[0-9];

D=[0-9];
L=[a-zA-Z_];
H=[a-fA-F0-9];
E=[Ee][+-]?{D}+;
WS=[\ ];
NL=[\ ]*[\r]?[\n][\ ]*;

%%
"(" => (token Tokens.LPAREN);
")" => (token Tokens.RPAREN);
"+" => (token Tokens.PLUS);
","{NL}* => (newlines (yytext); token Tokens.COMMA);
"-" => (token Tokens.MINUS);
"." => (token Tokens.DOT);
":" => (token Tokens.COLON);
";"{NL}* => (newlines (yytext); token Tokens.SEMI);
"=" => (token Tokens.EQUALS);
"F32" => (token Tokens.F32);
"F64" => (token Tokens.F64);
"I16" => (token Tokens.I16);
"I32" => (token Tokens.I32);
"I64" => (token Tokens.I64);
"I8" => (token Tokens.I8);
"U16" => (token Tokens.U16);
"U32" => (token Tokens.U32);
"U64" => (token Tokens.U64);
"U8" => (token Tokens.U8);
{NL}*"["{NL}* => (newlines (yytext); token Tokens.LBRACKET);
{NL}*"]"{NL}* => (newlines (yytext); token Tokens.RBRACKET);
"bool" => (token Tokens.BOOL);
"array" => (token Tokens.ARRAY);
"constant" => (token Tokens.CONSTANT);
"default" => (token Tokens.DEFAULT);
"enum" => (token Tokens.ENUM);
"module" => (token Tokens.MODULE);
"string" => (token Tokens.STRING);
"struct" => (token Tokens.STRUCT);
"type" => (token Tokens.TYPE);
{NL}*"{"{NL}* => (newlines (yytext); token Tokens.LBRACE);
{NL}*"}" => (newlines (yytext); token Tokens.RBRACE);
{L}({L}|{D})* => (
  token (fn (x, y) => Tokens.IDENT (yytext, x, y))
);

0[xX]{H}+ => (
  token (fn (x, y) => Tokens.LITERAL_INT (yytext, x, y))
);
0{D}+ => (
  token (fn (x, y) => Tokens.LITERAL_INT (yytext, x, y))
);
{D}+ => (
  token (fn (x, y) => Tokens.LITERAL_INT (yytext, x, y))
);

{D}+{E} => (
  token (fn (x, y) => Tokens.LITERAL_FLOAT (yytext, x, y))
);
{D}*"."{D}+({E})? => (
  token (fn (x, y) => Tokens.LITERAL_FLOAT (yytext, x, y))
);
{D}+"."{D}*({E})? => (
  token (fn (x, y) => Tokens.LITERAL_FLOAT (yytext, x, y))
);

{NL}+ => (newlines (yytext); token Tokens.EOL);

"@<"[^\r\n]*{NL}+ => (
  token (fn (x, y) => (newlines (yytext); Tokens.POST_ANNOTATION (yytext, x, y)))
);
"@"[^<\r\n]*{NL}+ => (
  token (fn (x, y) => (newlines (yytext); Tokens.PRE_ANNOTATION (yytext, x, y)))
);

"#"[^\r\n]*{NL}+ => (newlines (yytext); lex ());
{WS}+ => (lex ());
\\{NL} => (lex ());

\t => (syntaxError "illegal tab character");
. => (syntaxError "illegal character");

