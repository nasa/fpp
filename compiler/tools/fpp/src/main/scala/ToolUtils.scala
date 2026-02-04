package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

object ToolUtils {

  def parseFiles(files: List[File]) =
    Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)

  def resolveAsts(a: Analysis, tul: List[Ast.TransUnit]) =
    for {
      aTul <- ResolveSpecInclude.transUnitList(a, tul)
      a <- Right(aTul._1)
      tul <- Right(aTul._2)
      tul <- AddStateEnums.transUnitList(tul)
    } yield (a, tul)

  def parseFilesAndResolveAsts(a: Analysis, files: List[File]) =
    for {
      tul <- parseFiles(files)
      aTul <- resolveAsts(a, tul)
    } yield aTul

}
