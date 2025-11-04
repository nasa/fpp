package fpp.compiler.syntax

import scala.util.parsing.input.Positional

sealed trait Token extends Positional

object Token {
  case object EOF extends Token

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
  final case class CHOICE() extends Token
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
  final case class DICTIONARY() extends Token
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
  final case class EVERY() extends Token
  final case class EXIT() extends Token
  final case class EXTERNAL() extends Token
  final case class F32() extends Token
  final case class F64() extends Token
  final case class FALSE() extends Token
  final case class FATAL() extends Token
  final case class FORMAT() extends Token
  final case class GET() extends Token
  final case class GROUP() extends Token
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
  final case class INTERFACE() extends Token
  final case class INTERNAL() extends Token
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
  final case class OMIT() extends Token
  final case class ON() extends Token
  final case class OPCODE() extends Token
  final case class ORANGE() extends Token
  final case class OUTPUT() extends Token
  final case class PACKET() extends Token
  final case class PACKETS() extends Token
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
  final case class UNMATCHED() extends Token
  final case class UPDATE() extends Token
  final case class WARNING() extends Token
  final case class WITH() extends Token
  final case class YELLOW() extends Token
}

enum TokenId {
  case EOF

  // Identifier (non keyword words)
  case IDENTIFIER

  // Annotations
  case POST_ANNOTATION
  case PRE_ANNOTATION

  // Literals
  case LITERAL_FLOAT
  case LITERAL_INT
  case LITERAL_STRING

  // Keywords
  case ACTION
  case ACTIVE
  case ACTIVITY
  case ALWAYS
  case ARRAY
  case ASSERT
  case ASYNC
  case AT
  case BASE
  case BLOCK
  case BOOL
  case CHANGE
  case COMMAND
  case COMPONENT
  case CONNECTIONS
  case CONSTANT
  case CONTAINER
  case CPU
  case DEFAULT
  case DIAGNOSTIC
  case DICTIONARY
  case DO
  case DROP
  case ELSE
  case ENTER
  case ENTRY
  case ENUM
  case EVENT
  case EVERY
  case EXIT
  case EXTERNAL
  case F32
  case F64
  case FALSE
  case FATAL
  case FORMAT
  case GET
  case GROUP
  case GUARD
  case GUARDED
  case HEALTH
  case HIGH
  case HOOK
  case I16
  case I32
  case I64
  case I8
  case ID
  case IF
  case IMPORT
  case INCLUDE
  case INITIAL
  case INPUT
  case INSTANCE
  case INTERFACE
  case INTERNAL
  case CHOICE
  case LOCATE
  case LOW
  case MACHINE
  case MATCH
  case MODULE
  case OMIT
  case ON
  case OPCODE
  case ORANGE
  case OUTPUT
  case PACKET
  case PACKETS
  case PARAM
  case PASSIVE
  case PHASE
  case PORT
  case PRIORITY
  case PRIVATE
  case PRODUCT
  case QUEUE
  case QUEUED
  case RECORD
  case RECV
  case RED
  case REF
  case REG
  case REQUEST
  case RESP
  case SAVE
  case SEND
  case SERIAL
  case SET
  case SEVERITY
  case SIGNAL
  case SIZE
  case STACK
  case STATE
  case STRING
  case STRUCT
  case SYNC
  case TELEMETRY
  case TEXT
  case THROTTLE
  case TIME
  case TOPOLOGY
  case TRUE
  case TYPE
  case U16
  case U32
  case U64
  case U8
  case UNMATCHED
  case UPDATE
  case WARNING
  case WITH
  case YELLOW

  // Symbols
  case COLON
  case COMMA
  case DOT
  case EOL
  case EQUALS
  case LBRACE
  case LBRACKET
  case LPAREN
  case MINUS
  case PLUS
  case RARROW
  case RBRACE
  case RBRACKET
  case RPAREN
  case SEMI
  case SLASH
  case STAR
}
