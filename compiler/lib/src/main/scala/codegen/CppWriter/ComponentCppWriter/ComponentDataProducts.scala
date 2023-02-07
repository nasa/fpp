package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component data products */
case class ComponentDataProducts (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedContainers = component.containerMap.toList.sortBy(_._1)

  private val sortedRecords = component.recordMap.toList.sortBy(_._1)

  def getTypeMembers: List[CppDoc.Class.Member] =
    List(
      getContainerIds,
      getRecordIds,
      getContainer
    ).flatten

  private def getContainerIds = sortedContainers match {
    case Nil => Nil
    case _ => List(
      linesClassMember(
        CppDocWriter.writeDoxygenComment("The container ids") ++
        wrapInNamedStruct(
          "ContainerId",
          wrapInNamedEnum(
            "T : FwDpIdType",
            sortedContainers.map((id, container) => line(
              writeEnumConstant(container.getName, id)
            ))
          )
        )
      )
    )
  }

  private def getRecordIds = sortedRecords match {
    case Nil => Nil
    case _ => List(
      linesClassMember(
        CppDocWriter.writeDoxygenComment("The record ids") ++
        wrapInNamedStruct(
          "RecordId",
          wrapInNamedEnum(
            "T : FwDpIdType",
            sortedRecords.map((id, container) => line(
              writeEnumConstant(container.getName, id)
            ))
          )
        )
      )
    )
  }

  private def getContainer = component.hasDataProducts match {
    case false => Nil
    case true => List(
      classClassMember(
        Some("A data product container"),
        "DpContainer",
        Some("public Fw::DpContainer"),
        getContainerMembers
      )
    )
  }

  private def getContainerMembers: List[CppDoc.Class.Member.Class] = Nil

}
