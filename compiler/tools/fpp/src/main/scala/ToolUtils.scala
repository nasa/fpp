package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

object ToolUtils {

  def parseFiles(files: List[File]) =
    Result.map(files, Parser.parseFile (Parser.transUnit) (None))

}
