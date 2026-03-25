package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes the serializer class for a port definition */
case class PortSerializerClassWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends PortCppWriterUtils(s, aNode) {

  def write: CppDoc.Member.Class = classMember(
    Some(s"Serializer for $portName port\n$portAnnotation"),
    portSerializerName,
    None,
    List.concat(
      getPublicStaticFunctions
    )
  )

  private def getPublicStaticFunctions =
    addAccessTagAndComment(
      "public",
      s"Public static functions for $portSerializerName",
      List(getSerializeFunction)
    )

  private def getSerializeFunction =
    functionClassMember(
      Some("Serialize port arguments into a buffer"),
      "serializePortArgs",
      portFunctionParams :+ serialBufferFunctionParam,
      CppDoc.Type("Fw::SerializeStatus"),
      List.concat(
        lines("Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;"),
        portParams.flatMap(writeSerializationForParam),
        lines("return _status;")
      ),
      CppDoc.Function.Static
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
