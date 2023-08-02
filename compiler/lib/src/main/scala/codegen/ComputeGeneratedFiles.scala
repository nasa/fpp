package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Computes the names of generated files */
object ComputeGeneratedFiles {

  /** Computes autocoded files (XML and C++) */
  def getAutocodeFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      a <- enterSymbols(tul)
      xmlFiles <- getXmlFiles(a, tul)
      cppFiles <- getAutocodeCppFiles(a, tul)
    } 
    yield xmlFiles ++ cppFiles

  /** Computes component implementation files */
  def getImplFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      a <- enterSymbols(tul)
      cppFiles <- getImplCppFiles(a, tul)
    }
    yield cppFiles

  /** Computes files needed for unit test compilation
   *  (autocode XML and C++, test autocode C++) */
  def getTestFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      a <- enterSymbols(tul)
      autocodeFiles <- getAutocodeFiles(tul)
      testFiles <- getTestCppFiles(a, tul)
    }
    yield autocodeFiles ++ testFiles

  /** Computes unit test implementation files */
  def getTestImplFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      a <- enterSymbols(tul)
      cppFiles <- getTestImplCppFiles(a, tul)
    }
    yield cppFiles

  private def enterSymbols(tul: List[Ast.TransUnit]): Result.Result[Analysis] =
    EnterSymbols.visitList(Analysis(), tul, EnterSymbols.transUnit)

  private def getAutocodeCppFiles(a: Analysis, tul: List[Ast.TransUnit]):
  Result.Result[List[String]] =
    for {
      s <- ComputeAutocodeCppFiles.visitList(
        CppWriterState(a),
        tul,
        ComputeAutocodeCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  private def getImplCppFiles(a: Analysis, tul: List[Ast.TransUnit]):
  Result.Result[List[String]] =
    for {
      s <- ComputeImplCppFiles.visitList(
        CppWriterState(a),
        tul,
        ComputeImplCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  private def getTestCppFiles(a: Analysis, tul: List[Ast.TransUnit]):
  Result.Result[List[String]] =
    for {
      s <- ComputeTestCppFiles.visitList(
        CppWriterState(a),
        tul,
        ComputeTestCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  private def getTestImplCppFiles(a: Analysis, tul: List[Ast.TransUnit]):
  Result.Result[List[String]] =
    for {
      s <- ComputeTestImplCppFiles.visitList(
        CppWriterState(a),
        tul,
        ComputeTestImplCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  private def getXmlFiles(a: Analysis, tul: List[Ast.TransUnit]):
  Result.Result[List[String]] =
    for {
      s <- ComputeXmlFiles.visitList(
        XmlWriterState(a),
        tul,
        ComputeXmlFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

}
