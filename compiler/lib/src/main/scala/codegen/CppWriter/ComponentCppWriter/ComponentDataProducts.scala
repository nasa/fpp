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

  private val recordsById = component.recordMap.toList.sortBy(_._1)

  private val recordsByName = component.recordMap.toList.sortBy(_._2.getName)

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

  private def getRecordIds = recordsById match {
    case Nil => Nil
    case _ => List(
      linesClassMember(
        CppDocWriter.writeDoxygenComment("The record ids") ++
        wrapInNamedStruct(
          "RecordId",
          wrapInNamedEnum(
            "T : FwDpIdType",
            recordsById.map((id, container) => line(
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
        List(
          getConstructionMembers,
          getFunctionMembers,
          getVariableMembers
        ).flatten
      )
    )
  }

  private def getConstructionMembers = List(
    linesClassMember(CppDocHppWriter.writeAccessTag("public")),
    constructorClassMember(
      Some("Constructor"),
      List(
        CppDoc.Function.Param(
          CppDoc.Type("FwDpIdType"),
          "id",
          Some("The container id")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("const Fw::Buffer&"),
          "buffer",
          Some("The packet buffer")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwDpIdType"),
          "baseId",
          Some("The component base id")
        )
      ),
      List("Fw::DpContainer(id, buffer)", "baseId(baseId)"),
      Nil
    )
  )

  private def getFunctionMembers =
    linesClassMember(CppDocHppWriter.writeAccessTag("public")) ::
    recordsByName.map((id, record) => {
      val name = record.getName
      val t = record.recordType
      val typeName = writeCppTypeName(t, s)
      val paramType = if (s.isPrimitive(t, typeName))
        typeName else s"const ${typeName}&"
      val typeSize = s.getSerializedSizeExpr(t, typeName)
      functionClassMember(
        Some(s"""|Serialize a $name into the packet buffer
                 |\\return The serialize status"""),
        s"serializeRecord_${name}",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(paramType),
            "elt",
            Some("The element")
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        lines(
          s"""|Fw::SerializeBufferBase& serializeRepr = buffer.getSerializeRepr();
              |const FwDpIdType id = this->baseId + RecordId::${name};
              |Fw::SerializeStatus status = serializeRepr.serialize(id);
              |if (status == Fw::FW_SERIALIZE_OK) {
              |  status = serializeRepr.serialize(elt);
              |}
              |if (status == Fw::FW_SERIALIZE_OK) {
              |  this->dataSize += sizeof(FwDpIdType);
              |  this->dataSize += $typeSize;
              |}
              |return status;"""
        )
      )
    })

  private def getVariableMembers =
    addAccessTagAndComment(
      "PRIVATE",
      "The component base id",
      List(linesClassMember(lines("FwDpIdType baseId;")))
    )

}
