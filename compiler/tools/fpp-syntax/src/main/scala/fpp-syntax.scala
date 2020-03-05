package fpp.compiler

import fpp.compiler.syntax._

object FPPSyntax {

  def main(args: Array[String]) = {
    val result = Parser.parseString(Parser.exprNode, "0")
    println(s"$result")
  }

}
