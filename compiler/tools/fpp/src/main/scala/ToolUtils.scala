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

  def parseFilesAndResolveIncludes(a: Analysis, files: List[File]):
    Result.Result[(Analysis, List[Ast.TransUnit])] =
    for {
      tul <- parseFiles(files)
      aTul <- ResolveSpecInclude.transUnitList(a, tul)
    } yield aTul

  def parseFilesAndResolveIncludes(a: Analysis, files1: List[File], files2: List[File]):
    Result.Result[(Analysis, List[Ast.TransUnit], List[Ast.TransUnit])] =
    for {
      aTul <- ToolUtils.parseFilesAndResolveIncludes(a, files1)
      a <- Right(aTul._1)
      files1 <- Right(aTul._2)
      aTul <- ToolUtils.parseFilesAndResolveIncludes(a, files2)
      a <- Right(aTul._1)
      files2 <- Right(aTul._2)
    }
    yield (a, files1, files2)

}
