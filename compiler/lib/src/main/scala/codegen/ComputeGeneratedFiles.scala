package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.transform._
import fpp.compiler.util._

/** Computes the names of generated files */
object ComputeGeneratedFiles {

  /** Computes autocoded files (XML, C++ and JSON Dictionary) */
  def getAutocodeFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      aTul <- ResolveTemplates.tuList(Analysis(), tul)
      cppFiles <- getAutocodeCppFiles(aTul._1, aTul._2)
      dictFiles <- getDictionaryJsonFiles(aTul._1, aTul._2)
    }
    yield cppFiles ++ dictFiles

  /** Computes component implementation files */
  def getImplFiles(tul: List[Ast.TransUnit]): Result.Result[List[String]] =
    for {
      aTul <- ResolveTemplates.tuList(Analysis(), tul)
      cppFiles <- getImplCppFiles(aTul._1, aTul._2)
    }
    yield cppFiles

  /** Computes autocoded C++ files for testing */
  def getTestFiles(
    tul: List[Ast.TransUnit],
    testHelperMode: CppWriter.TestHelperMode
  ): Result.Result[List[String]] =
    for {
      aTul <- ResolveTemplates.tuList(Analysis(), tul)
      testFiles <- getTestCppFiles(aTul._1, aTul._2, testHelperMode)
    }
    yield testFiles

  /** Computes unit test implementation files */
  def getTestImplFiles(
    tul: List[Ast.TransUnit],
    testHelperMode: CppWriter.TestHelperMode
  ): Result.Result[List[String]] =
    for {
      aTul <- ResolveTemplates.tuList(Analysis(), tul)
      cppFiles <- getTestImplCppFiles(aTul._1, aTul._2, testHelperMode)
    }
    yield cppFiles

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

  private def getTestCppFiles(
    a: Analysis,
    tul: List[Ast.TransUnit],
    testHelperMode: CppWriter.TestHelperMode
  ): Result.Result[List[String]] = {
    val computeTestCppFiles = ComputeTestCppFiles(testHelperMode)
    for {
      s <- computeTestCppFiles.visitList(
        CppWriterState(a),
        tul,
        computeTestCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)
  }

  private def getTestImplCppFiles(
    a: Analysis,
    tul: List[Ast.TransUnit],
    testHelperMode: CppWriter.TestHelperMode
  ): Result.Result[List[String]] = {
    val computeTestImplCppFiles = ComputeTestImplCppFiles(testHelperMode)
    for {
      s <- computeTestImplCppFiles.visitList(
        CppWriterState(a),
        tul,
        computeTestImplCppFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)
  }

  private def getDictionaryJsonFiles(a: Analysis, tul: List[Ast.TransUnit]):
  Result.Result[List[String]] =
    for {
      s <- ComputeDictionaryFiles.visitList(
        DictionaryJsonEncoderState(a),
        tul,
        ComputeDictionaryFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

}
