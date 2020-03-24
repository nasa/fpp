package fpp.compiler.syntax

import fpp.compiler.util._
import util.parsing.input.Position

object ParserState {

  /** The file being parsed */
  var file: File = File.StdIn

  /** The including location */
  var includingLoc: Option[Location] = None

}
