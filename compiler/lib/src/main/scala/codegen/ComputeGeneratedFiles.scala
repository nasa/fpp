package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Computes the names of generated files */
object ComputeGeneratedFiles {

  def getAutocodeFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      a <- EnterSymbols.visitList(Analysis(), tul, EnterSymbols.transUnit)
      xmlFiles <- getXmlFiles(a, tul)
      cppFiles <- getAutocodeCppFiles(a, tul)
    } 
    yield xmlFiles ++ cppFiles

  def getImplFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      a <- EnterSymbols.visitList(Analysis(), tul, EnterSymbols.transUnit)
      cppFiles <- getImplCppFiles(a, tul)
    }
    yield cppFiles

  def getAutocodeCppFiles(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      s <- ComputeAutocodeCppFiles.visitList(
        CppWriterState(a),
        tul,
        ComputeAutocodeCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  def getImplCppFiles(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      s <- ComputeImplCppFiles.visitList(
        CppWriterState(a),
        tul,
        ComputeImplCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  def getXmlFiles(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      s <- ComputeXmlFiles.visitList(
        XmlWriterState(a),
        tul,
        ComputeXmlFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

}
