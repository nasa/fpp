package fpp.compiler

import fpp.compiler.syntax._
import java.io._

object FPPSyntax {

  def main(args: Array[String]) = {
    val br = new BufferedReader(new InputStreamReader(System.in))
    val result = Parser.parseString(Parser.exprNode, "0")
    println(s"$result")
  }

}
