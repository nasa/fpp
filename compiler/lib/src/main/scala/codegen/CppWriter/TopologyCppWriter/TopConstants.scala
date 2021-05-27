package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology constants */
case class TopConstants(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getLines: List[Line] = 
    addBannerComment(
      "Constants",
      flattenWithBlankPrefix(
        List(
          getBaseIdLines,
          getInstanceIdLines,
          getPriorityLines,
          getQueueSizeLines,
          getStackSizeLines,
          getTaskIdLines
        )
      )
    )

  private def getBaseIdLines: List[Line] = {
    wrapInNamespace(
      "BaseIds",
      wrapInEnum(
        t.instanceMap.keys.toList.map(ci => {
          val name = getShortName(ci.qualifiedName)
          val value = CppWriterState.writeId(ci.baseId)
          line(s"$name = $value,")
        })
      )
    )
  }

  private def getInstanceIdLines: List[Line] = {
    // TODO
    Nil
  }

  private def getPriorityLines: List[Line] = {
    // TODO
    Nil
  }

  private def getQueueSizeLines: List[Line] = {
    // TODO
    Nil
  }

  private def getStackSizeLines: List[Line] = {
    // TODO
    Nil
  }

  private def getTaskIdLines: List[Line] = {
    // TODO
    Nil
  }

}
