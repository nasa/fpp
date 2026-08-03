package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes the buffer class for a port definition */
case class PortBufferClassWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends PortCppWriterUtils(s, aNode) {

  def write: CppDoc.Member.Class = classMember(
    Some(s"Serialization buffer for $portName port\n$portAnnotation"),
    portBufferName,
    Some("public Fw::LinearBufferBase"),
    List.concat(
      getPublicConstants,
      getPublicMemberFunctions,
      getPrivateMemberVariables
    )
  )

  private def getPrivateMemberVariables =
    addAccessTagAndComment(
      "private",
      "Private member variables",
      guardedList (hasParams) (
        List(
          linesClassMember(
            Line.blank ::
            lines(s"U8 m_buff[CAPACITY];")
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def getPublicConstants = addAccessTagAndComment(
    "public",
    s"Public constants for $portBufferName",
    List(
      linesClassMember(
        List.concat(
          lines(
            s"""|
                |//! The buffer capacity. This is the sum of the static serialized
                |//! sizes of the port arguments.
                |static constexpr FwSizeType CAPACITY ="""
          ),
          writeBufferCapacity.map(indentIn)
        )
      )
    ),
    CppDoc.Lines.Hpp
  )

  private def getPublicMemberFunctions = addAccessTagAndComment(
    "public",
    s"Public member functions for $portBufferName",
    guardedList (hasParams) (
      List(
        linesClassMember(
          lines(
            s"""|
                |//! Constructor
                |${portBufferName}() {
                |  this->m_buffAddr = m_buff;
                |  this->m_capacity = CAPACITY;
                |}
                |"""
          )
        )
      )
    ),
    CppDoc.Lines.Hpp
  )

  private def writeBufferCapacity: List[Line] = writeSum(
    portParams.map(
      param => {
        val data = param._2.data
        val t = s.a.typeMap(data.typeName.id)
        val tn = typeCppWriter.write(t)
        writeStaticSerializedSizeExpr(s, t, tn)
      }
    )
  )

}
