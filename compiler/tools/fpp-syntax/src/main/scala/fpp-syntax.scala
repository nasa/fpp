package fpp.compiler

import fpp.compiler.syntax._
import fpp.compiler.util._

object FPPSyntax {

  def main(args: Array[String]) = {
    Error.setTool(Tool("fpp-syntax"))
    val result = Parser.parseFile(Parser.transUnit, File.StdIn)
    result match {
      case Left(error) => {
        error.print
        System.exit(1)
      }
      case Right(_) => ()
    }
  }

}
