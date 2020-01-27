package fpp.compiler.syntax

import scala.util.parsing.input.Positional

sealed trait Token extends Positional

object Token {
  case class IDENTIFIER(str: String) extends Token
  case class LITERAL_FLOAT(str: String) extends Token
  case class LITERAL_INT(str: String) extends Token
  case class LITERAL_STRING(str: String) extends Token
  case class POST_ANNOTATION(str: String) extends Token
  case class PRE_ANNOTATION(str: String) extends Token
  case object ACTIVE extends Token
  case object ARRAY extends Token
  case object ASSERT extends Token
  case object ASYNC extends Token
  case object AT extends Token
  case object BLOCK extends Token
  case object BOOL extends Token
  case object COLON extends Token
  case object COMMA extends Token
  case object COMMAND extends Token
  case object COMPONENT extends Token
  case object CONSTANT extends Token
  case object DEFAULT extends Token
  case object DOT extends Token
  case object DROP extends Token
  case object ENUM extends Token
  case object EOL extends Token
  case object EQUALS extends Token
  case object EVENT extends Token
  case object F32 extends Token
  case object F64 extends Token
  case object FALSE extends Token
  case object GET extends Token
  case object GUARDED extends Token
  case object I16 extends Token
  case object I32 extends Token
  case object I64 extends Token
  case object I8 extends Token
  case object INPUT extends Token
  case object INSTANCE extends Token
  case object INTERNAL extends Token
  case object LBRACE extends Token
  case object LBRACKET extends Token
  case object LOCATE extends Token
  case object LPAREN extends Token
  case object MINUS extends Token
  case object MODULE extends Token
  case object OUTPUT extends Token
  case object PARAM extends Token
  case object PASSIVE extends Token
  case object PLUS extends Token
  case object PORT extends Token
  case object PRIORITY extends Token
  case object QUEUED extends Token
  case object RARROW extends Token
  case object RBRACE extends Token
  case object RBRACKET extends Token
  case object REF extends Token
  case object REG extends Token
  case object RESP extends Token
  case object RPAREN extends Token
  case object SEMI extends Token
  case object SET extends Token
  case object SIZE extends Token
  case object SLASH extends Token
  case object STAR extends Token
  case object STRING extends Token
  case object STRUCT extends Token
  case object SYNC extends Token
  case object TELEMETRY extends Token
  case object THROTTLE extends Token
  case object TIME extends Token
  case object TOPOLOGY extends Token
  case object TRUE extends Token
  case object TYPE extends Token
  case object YELLOW extends Token
  case object WARNING extends Token
  case object UPDATE extends Token
  case object UNUSED extends Token
  case object TEXT extends Token
  case object STACK extends Token
  case object SERIAL extends Token
  case object SAVE extends Token
  case object RED extends Token
  case object RECV extends Token
  case object QUEUE extends Token
  case object PRIVATE extends Token
  case object PHASE extends Token
  case object PATTERN extends Token
  case object ORANGE extends Token
  case object OPCODE extends Token
  case object ON extends Token
  case object LOW extends Token
  case object INIT extends Token
  case object INCLUDE extends Token
  case object IMPORT extends Token
  case object ID extends Token
  case object HIGH extends Token
  case object FORMAT extends Token
  case object FATAL extends Token
  case object DIAGNOSTIC extends Token
  case object CONNECTIONS extends Token
  case object CHANGE extends Token
  case object BASE extends Token
  case object ALWAYS extends Token
  case object ACTIVITY extends Token
  case object U16 extends Token
  case object U32 extends Token
  case object U64 extends Token
  case object U8 extends Token
}
