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
      getPublicStaticFunctions,
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
    "Public constants",
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
    "Public member functions",
    List(
      linesClassMember({
        val buffAddr =
          if !hasParams then "nullptr" else "m_buff"
        lines(
          s"""|
              |//! Get the capacity of the buffer
              |//! \\return The capacity
              |Fw::Serializable::SizeType getCapacity() const override {
              |  return CAPACITY;
              |}
              |
              |//! Get the buffer address (non-const)
              |//! \\return The buffer address
              |U8* getBuffAddr() override {
              |  return $buffAddr;
              |}
              |
              |//! Get the buffer address (const)
              |//! \\return The buffer address
              |const U8* getBuffAddr() const override {
              |  return $buffAddr;
              |}
              |"""
        )
      })
    ),
    CppDoc.Lines.Hpp
  )

  private def getPublicStaticFunctions =
    addAccessTagAndComment(
      "public",
      "Public static functions",
      guardedList (hasParams) (
        List(getSerializeFunction)
      )
    )

  private def getSerializeFunction =
    functionClassMember(
      Some("Serialize port arguments into the buffer"),
      "serializePortArgs",
      portFunctionParams :+ bufferFunctionParam,
      CppDoc.Type("Fw::SerializeStatus"),
      List.concat(
        lines("Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;"),
        portParams.flatMap(writeSerializationForParam),
        lines("return _status;")
      ),
      CppDoc.Function.Static
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

  private def writeSerializationForParam(param: PortParamType) = {
    val paramName = param._2.data.name
    lines(
      s"""|if (_status == Fw::FW_SERIALIZE_OK) {
          |  _status = _buffer.serializeFrom($paramName);
          |}"""
    )
  }

}
