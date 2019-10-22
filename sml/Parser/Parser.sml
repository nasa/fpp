(* ----------------------------------------------------------------------
 * Parser.sml
 * ----------------------------------------------------------------------*) 

structure FPPLrVals =
  FPPLrValsFun(structure Token = LrParser.Token)

structure FPPLex =
  FPPLexFun(structure Tokens = FPPLrVals.Tokens);

structure FPPParser =
  Join(structure LrParser = LrParser
    structure ParserData = FPPLrVals.ParserData
    structure Lex = FPPLex)

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
      FPPParser.parse (15, lexstream, printError, ())
    val lexer = FPPParser.makeLexer input
    val (result,lexer) = invoke lexer
  in 
    result
  end

end

