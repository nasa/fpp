package fpp.compiler.syntax

import scala.util.parsing.input.Positional

sealed trait Token extends Positional

object Token {
  final case class ACTION() extends Token
  final case class ACTIVE() extends Token
  final case class ACTIVITY() extends Token
  final case class ALWAYS() extends Token
  final case class ARRAY() extends Token
  final case class ASSERT() extends Token
  final case class ASYNC() extends Token
  final case class AT() extends Token
  final case class BASE() extends Token
  final case class BLOCK() extends Token
  final case class BOOL() extends Token
  final case class CHANGE() extends Token
  final case class COLON() extends Token
  final case class COMMA() extends Token
  final case class COMMAND() extends Token
  final case class COMPONENT() extends Token
  final case class CONNECTIONS() extends Token
  final case class CONSTANT() extends Token
  final case class CONTAINER() extends Token
  final case class CPU() extends Token
  final case class DEFAULT() extends Token
  final case class DIAGNOSTIC() extends Token
  final case class DO() extends Token
  final case class DOT() extends Token
  final case class DROP() extends Token
  final case class ELSE() extends Token
  final case class ENTER() extends Token
  final case class ENTRY() extends Token
  final case class ENUM() extends Token
  final case class EOL() extends Token
  final case class EQUALS() extends Token
  final case class EVENT() extends Token
  final case class EXIT() extends Token
  final case class F32() extends Token
  final case class F64() extends Token
  final case class FALSE() extends Token
  final case class FATAL() extends Token
  final case class FORMAT() extends Token
  final case class GET() extends Token
  final case class GUARD() extends Token
  final case class GUARDED() extends Token
  final case class HEALTH() extends Token
  final case class HIGH() extends Token
  final case class HOOK() extends Token
  final case class I16() extends Token
  final case class I32() extends Token
  final case class I64() extends Token
  final case class I8() extends Token
  final case class ID() extends Token
  final case class IDENTIFIER(str: String) extends Token
  final case class IF() extends Token
  final case class IMPORT() extends Token
  final case class INCLUDE() extends Token
  final case class INITIAL() extends Token
  final case class INPUT() extends Token
  final case class INSTANCE() extends Token
  final case class INTERNAL() extends Token
  final case class JUNCTION() extends Token
  final case class LBRACE() extends Token
  final case class LBRACKET() extends Token
  final case class LITERAL_FLOAT(str: String) extends Token
  final case class LITERAL_INT(str: String) extends Token
  final case class LITERAL_STRING(str: String) extends Token
  final case class LOCATE() extends Token
  final case class LOW() extends Token
  final case class LPAREN() extends Token
  final case class MACHINE() extends Token
  final case class MATCH() extends Token
  final case class MINUS() extends Token
  final case class MODULE() extends Token
  final case class ON() extends Token
  final case class OPCODE() extends Token
  final case class ORANGE() extends Token
  final case class OUTPUT() extends Token
  final case class PARAM() extends Token
  final case class PASSIVE() extends Token
  final case class PHASE() extends Token
  final case class PLUS() extends Token
  final case class PORT() extends Token
  final case class POST_ANNOTATION(str: String) extends Token
  final case class PRE_ANNOTATION(str: String) extends Token
  final case class PRIORITY() extends Token
  final case class PRIVATE() extends Token
  final case class PRODUCT() extends Token
  final case class QUEUE() extends Token
  final case class QUEUED() extends Token
  final case class RARROW() extends Token
  final case class RBRACE() extends Token
  final case class RBRACKET() extends Token
  final case class RECORD() extends Token
  final case class RECV() extends Token
  final case class RED() extends Token
  final case class REF() extends Token
  final case class REG() extends Token
  final case class REQUEST() extends Token
  final case class RESP() extends Token
  final case class RPAREN() extends Token
  final case class SAVE() extends Token
  final case class SEMI() extends Token
  final case class SEND() extends Token
  final case class SERIAL() extends Token
  final case class SET() extends Token
  final case class SEVERITY() extends Token
  final case class SIGNAL() extends Token
  final case class SIZE() extends Token
  final case class SLASH() extends Token
  final case class STACK() extends Token
  final case class STAR() extends Token
  final case class STATE() extends Token
  final case class STRING() extends Token
  final case class STRUCT() extends Token
  final case class SYNC() extends Token
  final case class TELEMETRY() extends Token
  final case class TEXT() extends Token
  final case class THROTTLE() extends Token
  final case class TIME() extends Token
  final case class TOPOLOGY() extends Token
  final case class TRUE() extends Token
  final case class TYPE() extends Token
  final case class U16() extends Token
  final case class U32() extends Token
  final case class U64() extends Token
  final case class U8() extends Token
  final case class UNUSED() extends Token
  final case class UPDATE() extends Token
  final case class WARNING() extends Token
  final case class WITH() extends Token
  final case class YELLOW() extends Token
}
