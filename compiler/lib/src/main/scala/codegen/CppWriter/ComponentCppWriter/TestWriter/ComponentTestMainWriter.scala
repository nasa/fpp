package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component main test harness classes */
case class ComponentTestMainWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentTestMain(componentName)

  private val name = componentName

  private val symbol = componentSymbol

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component test main function",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val testerType = s.getNamespace(symbol) match {
      case Some(namespace) => s"$namespace::$testImplClassName"
      case None => testImplClassName
    }

    List(
      getIncludes,
      linesMember(
        Line.blank :: lines(
          s"""|TEST(Nominal, toDo) {
              |  $testerType tester;
              |  tester.toDo();
              |}
              |
              |int main(int argc, char** argv) {
              |  ::testing::InitGoogleTest(&argc, argv);
              |  return RUN_ALL_TESTS();
              |}
              |"""
        ),
        CppDoc.Lines.Cpp
      )
    )
  }

  private def getIncludes: CppDoc.Member = {
    val fileName = ComputeCppFiles.FileNames.getComponentTestImpl(name)
    val header = s"$fileName.hpp"
    linesMember(
      addBlankPrefix(lines(CppWriter.headerString(header))),
      CppDoc.Lines.Cpp
    )
  }

}
