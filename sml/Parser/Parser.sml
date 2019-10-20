(* ----------------------------------------------------------------------
 * parser.sml
 * ----------------------------------------------------------------------*) 

structure TNetLrVals =
  TNetLrValsFun(structure Token = LrParser.Token)

structure TNetLex =
  TNetLexFun(structure Tokens = TNetLrVals.Tokens);

structure TNetParser =
  Join(structure LrParser = LrParser
    structure ParserData = TNetLrVals.ParserData
    structure Lex = TNetLex)

structure Parser :> PARSER=
struct

open Loc

fun parse (file, instream) = 
  let 
    val _ = ParserState.file := file
    val _ = ParserState.pos := 1
    fun input _ = TextIO.input instream
    fun printError (s, pos1, pos2) =
    let
      val loc = Loc {
        file = !ParserState.file,
        pos1 = pos1,
        pos2 = pos2
      }
    in
      raise Error.SyntaxError (loc, s)
    end
    fun invoke lexstream = 
      TNetParser.parse (15, lexstream, printError, ())
    val lexer = TNetParser.makeLexer input
    val (result,lexer) = invoke lexer
  in 
    result
  end

end

