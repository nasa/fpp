package fpp.compiler.syntax

import scala.util.parsing.input.Positional

sealed trait Token extends Positional

object Token {
  final case class IDENTIFIER(str: String) extends Token
  final case class LITERAL_FLOAT(str: String) extends Token
  final case class LITERAL_INT(str: String) extends Token
  final case class LITERAL_STRING(str: String) extends Token
  final case class POST_ANNOTATION(str: String) extends Token
  final case class PRE_ANNOTATION(str: String) extends Token
  final case object ACTIVE extends Token
  final case object ACTIVITY extends Token
  final case object ALWAYS extends Token
  final case object ARRAY extends Token
  final case object ASSERT extends Token
  final case object ASYNC extends Token
  final case object AT extends Token
  final case object BASE extends Token
  final case object BLOCK extends Token
  final case object BOOL extends Token
  final case object CHANGE extends Token
  final case object COLON extends Token
  final case object COMMA extends Token
  final case object COMMAND extends Token
  final case object COMPONENT extends Token
  final case object CONNECTIONS extends Token
  final case object CONSTANT extends Token
  final case object DEFAULT extends Token
  final case object DIAGNOSTIC extends Token
  final case object DOT extends Token
  final case object DROP extends Token
  final case object ENUM extends Token
  final case object EOL extends Token
  final case object EQUALS extends Token
  final case object EVENT extends Token
  final case object F32 extends Token
  final case object F64 extends Token
  final case object FALSE extends Token
  final case object FATAL extends Token
  final case object FORMAT extends Token
  final case object GET extends Token
  final case object GUARDED extends Token
  final case object HIGH extends Token
  final case object I16 extends Token
  final case object I32 extends Token
  final case object I64 extends Token
  final case object I8 extends Token
  final case object ID extends Token
  final case object IMPORT extends Token
  final case object INCLUDE extends Token
  final case object INIT extends Token
  final case object INPUT extends Token
  final case object INSTANCE extends Token
  final case object INTERNAL extends Token
  final case object LBRACE extends Token
  final case object LBRACKET extends Token
  final case object LOCATE extends Token
  final case object LOW extends Token
  final case object LPAREN extends Token
  final case object MINUS extends Token
  final case object MODULE extends Token
  final case object ON extends Token
  final case object OPCODE extends Token
  final case object ORANGE extends Token
  final case object OUTPUT extends Token
  final case object PARAM extends Token
  final case object PASSIVE extends Token
  final case object PATTERN extends Token
  final case object PHASE extends Token
  final case object PLUS extends Token
  final case object PORT extends Token
  final case object PRIORITY extends Token
  final case object PRIVATE extends Token
  final case object QUEUE extends Token
  final case object QUEUED extends Token
  final case object RARROW extends Token
  final case object RBRACE extends Token
  final case object RBRACKET extends Token
  final case object RECV extends Token
  final case object RED extends Token
  final case object REF extends Token
  final case object REG extends Token
  final case object RESP extends Token
  final case object RPAREN extends Token
  final case object SAVE extends Token
  final case object SEMI extends Token
  final case object SERIAL extends Token
  final case object SET extends Token
  final case object SEVERITY extends Token
  final case object SIZE extends Token
  final case object SLASH extends Token
  final case object STACK extends Token
  final case object STAR extends Token
  final case object STRING extends Token
  final case object STRUCT extends Token
  final case object SYNC extends Token
  final case object TELEMETRY extends Token
  final case object TEXT extends Token
  final case object THROTTLE extends Token
  final case object TIME extends Token
  final case object TOPOLOGY extends Token
  final case object TRUE extends Token
  final case object TYPE extends Token
  final case object U16 extends Token
  final case object U32 extends Token
  final case object U64 extends Token
  final case object U8 extends Token
  final case object UNUSED extends Token
  final case object UPDATE extends Token
  final case object WARNING extends Token
  final case object YELLOW extends Token
}
