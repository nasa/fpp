package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology private functions */
case class TopPrivateFunctions(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  /** Compute the set of defined function names and the list
   *  of lines defining the functions */
  def getLines: (Set[String], List[Line]) = {
    // Get pairs of (function name, function lines)
    val pairs = List(
      getFreeThreadsLines,
      getTearDownComponentsLines,
    )
    // Compute the set of names with nonempty lines
    val fns = pairs.foldLeft (Set[String]()) {
      case (set, (_, Nil)) => set
      case (set, (name, _)) => set + name
    }
    // Extract the lines
    val ll = addBannerComment(
      "Private functions",
      pairs.map(_._2).flatten
    )
    (fns, ll)
  }

  private def getFreeThreadsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.freeThreads) (ci).getOrElse {
        if (isActive(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"(void) $name.ActiveComponentBase::join(nullptr);")
        }
        else Nil
      }
    val name = "freeThreads"
    val ll = addComment(
      "Free threads",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getTearDownComponentsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.tearDownComponents) (ci).getOrElse(Nil)
    }
    val name = "tearDownComponents"
    val ll = addComment(
      "Tear down components",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

}
