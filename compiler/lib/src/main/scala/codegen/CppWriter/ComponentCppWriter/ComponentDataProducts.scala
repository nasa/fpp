package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component data products */
case class ComponentDataProducts (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getTypeMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Types for data products",
      List.concat(
        getContainerIds,
        getContainerPriorities,
        getRecordIds,
        Container.getContainer
      )
    )

  def getVirtualFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Handlers to implement for data products",
      containersByName.map((id, container) => getDpRecvHandler(container.getName)),
      CppDoc.Lines.Hpp
    )

  def getProtectedDpFunctionMembers: List[CppDoc.Class.Member] = {
    lazy val dpGetFunction = functionClassMember(
      Some(raw"""|Get a buffer and use it to initialize a data product container
                 |\return The status of the buffer request"""),
      "Dp_Get",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("ContainerId::T"),
          "containerId",
          Some("The container id (input)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwSizeType"),
          "size",
          Some("The buffer size (input)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("DpContainer&"),
          "container",
          Some("The container (output)")
        )
      ),
      CppDoc.Type("Fw::Success::T"),
      {
        val invokeProductGet = outputPortInvokerName(productGetPort.get)
        lines(s"""|const FwDpIdType baseId = this->getIdBase();
                  |const FwDpIdType id = baseId + container.getId();
                  |Fw::Buffer buffer;
                  |const Fw::Success::T status = this->$invokeProductGet(0, id, size, buffer);
                  |if (status == Fw::Success::SUCCESS) {
                  |  container.setId(id);
                  |  container.setBuffer(buffer);
                  |  container.setBaseId(baseId);
                  |}
                  |return status;""")
      }
    )
    lazy val dpRequestFunction = functionClassMember(
      Some("Request a data product container"),
      "Dp_Request",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("ContainerId::T"),
          "containerId",
          Some("The container id")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwSizeType"),
          "size",
          Some("The buffer size")
        )
      ),
      CppDoc.Type("void"),
      {
        val invokeProductRequest = outputPortInvokerName(productRequestPort.get)
        lines(
          s"""|const FwDpIdType globalId = this->getIdBase() + containerId;
              |this->$invokeProductRequest(0, globalId, size);"""
        )
      }
    )
    lazy val dpSendFunction = functionClassMember(
      Some("Send a data product"),
      "Dp_Send",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("DpContainer&"),
          "container",
          Some("The data product container")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("Fw::Time"),
          "timeTag",
          Some("The time tag"),
          Some("Fw::ZERO_TIME")
        )
      ),
      CppDoc.Type("void"),
      {
        val invokeProductSend = outputPortInvokerName(productSendPort.get)
        lines(
          s"""|// Update the time tag
              |if (timeTag == Fw::ZERO_TIME) {
              |  // Get the time from the time port
              |  timeTag = this->getTime();
              |}
              |container.setTimeTag(timeTag);
              |// Serialize the header into the packet
              |Fw::SerializeStatus status = container.serializeHeader();
              |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
              |// Update the size of the buffer according to the data size
              |const FwSizeType packetSize = container.getPacketSize();
              |Fw::Buffer buffer = container.getBuffer();
              |FW_ASSERT(packetSize <= buffer.getSize(), packetSize, buffer.getSize());
              |buffer.setSize(packetSize);
              |// Send the buffer
              |this->$invokeProductSend(0, container.getId(), buffer);"""
        )
      }
    )
    addAccessTagAndComment(
      "PROTECTED",
      "Functions for managing data products",
      List.concat(
        guardedList (hasProductGetPort) (List(dpGetFunction)),
        guardedList (hasProductRequestPort) (List(dpRequestFunction)),
        guardedList (hasDataProducts) (List(dpSendFunction))
      )
    )
  }

  def getPrivateDpFunctionMembers: List[CppDoc.Class.Member] = {
    def code(portInstance: PortInstance) = {
      val portName = portInstance.getUnqualifiedName
      val handlerFunction = functionClassMember(
        Some(s"Handler implementation for ${portName}"),
        s"${portName}_handler",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const NATIVE_INT_TYPE"),
            "portNum",
            Some("The port number")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("FwDpIdType"),
            "id",
            Some("The container id")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("const Fw::Buffer&"),
            "buffer",
            Some("The buffer")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("const Fw::Success&"),
            "status",
            Some("The buffer status")
          )
        ),
        CppDoc.Type("void"),
        List.concat(
          lines(
            """|DpContainer container(id, buffer, this->getIdBase());
               |// Convert global id to local id
               |const auto idBase = this->getIdBase();
               |FW_ASSERT(id >= idBase, id, idBase);
               |const auto localId = id - idBase;
               |// Switch on the local id"""
          ),
          wrapInSwitch(
            "localId",
            List.concat(
              containersById.flatMap((id, container) => {
                val name = container.getName
                lines(
                  s"""|case ContainerId::$name:
                      |  // Set the priority
                      |  container.setPriority(ContainerPriority::$name);
                      |  // Call the handler
                      |  this->Dp_Recv_${name}_handler(container, status.e);
                      |  break;"""
                )
              }),
              lines (
                """|default:
                   |  FW_ASSERT(0);
                   |  break;"""
              )
            )
          )
        )
      )
      addAccessTagAndComment(
        "PRIVATE",
        "Private data product handling functions",
        List(handlerFunction)
      )
    }
    component.specialPortMap.get(Ast.SpecPortInstance.ProductRecv).
      map(code).getOrElse(Nil)
  }

  private def getContainerIds = containersById match {
    case Nil => Nil
    case _ => List(
      linesClassMember(
        CppDocWriter.writeDoxygenComment("The container ids") ++
        wrapInNamedStruct(
          "ContainerId",
          wrapInNamedEnum(
            "T : FwDpIdType",
            containersById.flatMap((id, container) =>
              writeEnumConstant(container.getName, id)
            )
          )
        )
      )
    )
  }

  private def getContainerPriorities = containersById match {
    case Nil => Nil
    case _ => List(
      linesClassMember(
        CppDocWriter.writeDoxygenComment("The container default priorities") ++
        wrapInNamedStruct(
          "ContainerPriority",
          wrapInNamedEnum(
            "T : FwDpPriorityType",
            containersById.flatMap((id, container) => {
              val priority = container.defaultPriority.getOrElse(BigInt(0))
              writeEnumConstant(container.getName, priority)
            })
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
            recordsById.flatMap((id, container) =>
              writeEnumConstant(container.getName, id)
            )
          )
        )
      )
    )
  }

  /** Generates the Container inner class */
  private object Container extends ComponentCppWriterUtils(s, aNode) {

    def getContainer = component.hasDataProducts match {
      case false => Nil
      case true => List(
        classClassMember(
          Some("A data product container"),
          "DpContainer",
          Some("public Fw::DpContainer"),
          List.concat(
            getConstructionMembers,
            getFunctionMembers,
            getVariableMembers
          )
        )
      )
    }

    private def getConstructionMembers = List(
      linesClassMember(CppDocHppWriter.writeAccessTag("public")),
      constructorClassMember(
        Some("Constructor with custom initialization"),
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
      ),
      constructorClassMember(
        Some("Constructor with default initialization"),
        Nil,
        List("Fw::DpContainer()", "baseId(0)"),
        Nil
      ),
    )

    private def typedRecordSerializeFn(name: String, t: Type) = {
      val typeName = TypeCppWriter.getName(s, t)
      val paramType = if (s.isPrimitive(t, typeName))
        typeName else s"const ${typeName}&"
      val typeSize = s.getSerializedSizeExpr(t, typeName)
      functionClassMember(
        Some(s"""|Serialize a $name record into the packet buffer
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
    }

    private def rawRecordSerializeFn(name: String) =
      functionClassMember(
        Some(s"""|Serialize a $name record into the packet buffer
                 |\\return The serialize status"""),
        s"serializeRecord_${name}",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::ByteArray"),
            "byteArray",
            Some("The raw byte array")
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        lines(
          s"""|Fw::SerializeBufferBase& serializeRepr = buffer.getSerializeRepr();
              |const FwDpIdType id = this->baseId + RecordId::${name};
              |const FwSizeType size = byteArray.size;
              |Fw::SerializeStatus status = serializeRepr.serialize(id);
              |if (status == Fw::FW_SERIALIZE_OK) {
              |  status = serializeRepr.serialize(size);
              |}
              |if (status == Fw::FW_SERIALIZE_OK) {
              |  const bool omitSerializedLength = true;
              |  status = serializeRepr.serialize(
              |      byteArray.bytes,
              |      size,
              |      omitSerializedLength
              |  );
              |}
              |if (status == Fw::FW_SERIALIZE_OK) {
              |  this->dataSize += sizeof(FwDpIdType);
              |  this->dataSize += sizeof(FwSizeType);
              |  this->dataSize += size;
              |}
              |return status;"""
        )
      )


    private def getSerializeFunctionMembers =
      recordsByName.map((id, record) => {
        val name = record.getName
        record.recordType match {
          case Some(t) => typedRecordSerializeFn(name, t)
          case None => rawRecordSerializeFn(name)
        }
      })

    private val getAccessFunctionsMember = linesClassMember(
      lines(
        raw"""|
              |FwDpIdType getBaseId() const { return this->baseId; }
              |
              |void setBaseId(FwDpIdType baseId) { this->baseId = baseId; }"""
      )
    )

    private def getFunctionMembers =
      linesClassMember(CppDocHppWriter.writeAccessTag("public")) ::
      (getSerializeFunctionMembers :+ getAccessFunctionsMember)

    private def getVariableMembers = List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("PRIVATE") ++
        CppDocWriter.writeDoxygenComment("The component base id") ++
        lines("FwDpIdType baseId;")
      )
    )

  }

}

