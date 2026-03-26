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
      getPublicConstructors,
      getPublicMemberFunctions,
      getPublicStaticFunctions,
      getPrivateMemberVariables,
      getPublicMemberVariables
    )
  )

  private def getDeserializeFunction =
    functionClassMember(
      Some("Deserialze port arguments into members"),
      "deserializePortArgs",
      List(serialBufferFunctionParam),
      CppDoc.Type("Fw::SerializeStatus"),
      List.concat(
        lines("Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;"),
        portParams.flatMap(writeDeserializationForParam),
        lines("return _status;")
      )
    )

  private def getParamVariable(portParam: PortParamType): Line = {
    val data = portParam._2.data
    val name = data.name
    val t = s.a.typeMap(data.typeName.id)
    val typeName = typeCppWriter.write(t)
    t.getUnderlyingType match {
      case _: Type.String =>
        line(s"Fw::ExternalString m_$name;")
      case _ =>
        line(s"$typeName m_$name;")
    }
  }

  private def getPrivateMemberVariables: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      s"Private member variables for $portSerializerName",
      guardedList (hasStringParams) (
        List(
          linesClassMember(
            Line.blank ::
            portParams.flatMap(getStringBuffer)
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def getPublicConstructors = 
    addAccessTagAndComment(
      "public",
      s"Public constructors for $portSerializerName",
      List(
        constructorClassMember(
          Some("Constructor"),
          Nil,
          portParams.map(writeInitializer),
          Nil
        )
      )
    )

  private def getPublicMemberFunctions =
    addAccessTagAndComment(
      "public",
      s"Public member functions for $portSerializerName",
      List(getDeserializeFunction)
    )

  private def getPublicMemberVariables: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      s"Public member variables for $portSerializerName",
      List(
        linesClassMember(
          Line.blank ::
          portParams.map(getParamVariable)
        )
      ),
      CppDoc.Lines.Hpp
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

  private def getStringBuffer(portParam: PortParamType): List[Line] = {
    val data = portParam._2.data
    val t = s.a.typeMap(data.typeName.id)
    t.getUnderlyingType match {
      case st: Type.String =>
        val name = data.name
        val bufferName = getBufferName(name)
        val size = writeStringSize(s, st)
        lines(s"char m_$bufferName[Fw::StringBase::BUFFER_SIZE($size)];")
      case _ => Nil
    }
  }

  private def writeDeserializationForParam(param: PortParamType) = {
    val paramName = param._2.data.name
    lines(
      s"""|if (_status == Fw::FW_SERIALIZE_OK) {
          |  _status = _buffer.deserializeTo(m_$paramName);
          |}"""
    )
  }

  private def writeInitializer(portParam: PortParamType) = {
    val data = portParam._2.data
    val paramName = data.name
    val bufferName = getBufferName(paramName)
    val t = s.a.typeMap(data.typeName.id)
    t.getUnderlyingType match {
      case _: Type.String => s"m_$paramName(m_$bufferName, sizeof m_$bufferName)"
      case _ => s"m_$paramName()"
    }
  }

  private def writeSerializationForParam(param: PortParamType) = {
    val paramName = param._2.data.name
    lines(
      s"""|if (_status == Fw::FW_SERIALIZE_OK) {
          |  _status = _buffer.serializeFrom($paramName);
          |}"""
    )
  }

}
