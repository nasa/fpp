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
    lazy val dpGetByName = containersByName.map((_, c) => {
      val name = c.getName
      linesClassMember(
        lines(
          s"""|
              |//! Get a buffer and use it to initialize container $name
              |//! \\return The status of the buffer request
              |Fw::Success::T dpGet_$name(
              |    FwSizeType dataSize, //!< The data size (input)
              |    DpContainer& container //!< The container (output)
              |) {
              |  return this->dpGet(ContainerId::$name, dataSize, container);
              |}"""
        )
      )
    })
    lazy val dpRequestByName = containersByName.map((_, c) => {
      val name = c.getName
      linesClassMember(
        lines(
          s"""|
              |//! Request a $name container
              |void dpRequest_$name(
              |    FwSizeType size //!< The buffer size (input)
              |) {
              |  return this->dpRequest(ContainerId::$name, size);
              |}"""
        )
      )
    })
    lazy val dpSendFunction = functionClassMember(
      Some("Send a data product"),
      "dpSend",
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
              |container.serializeHeader();
              |// Update the data hash
              |container.updateDataHash();
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
      guardedList (hasDataProducts) (
        List.concat(
          guardedList (hasProductGetPort) (dpGetByName),
          guardedList (hasProductRequestPort) (dpRequestByName),
          List(dpSendFunction)
        )
      )
    )
  }

  def getPrivateDpFunctionMembers: List[CppDoc.Class.Member] = {
    lazy val dpGet = functionClassMember(
      Some(raw"""|Get a buffer and use it to initialize a data product container
                 |\return The status of the buffer request"""),
      "dpGet",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("ContainerId::T"),
          "containerId",
          Some("The component-local container id (input)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwSizeType"),
          "dataSize",
          Some("The data size (input)")
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
                  |const FwDpIdType globalId = baseId + containerId;
                  |const FwSizeType size = DpContainer::getPacketSizeForDataSize(dataSize);
                  |Fw::Buffer buffer;
                  |const Fw::Success::T status = this->$invokeProductGet(0, globalId, size, buffer);
                  |if (status == Fw::Success::SUCCESS) {
                  |  container.setId(globalId);
                  |  container.setBuffer(buffer);
                  |  container.setBaseId(baseId);
                  |}
                  |return status;""")
      }
    )
    lazy val dpRequest = functionClassMember(
      Some("Request a data product container"),
      "dpRequest",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("ContainerId::T"),
          "containerId",
          Some("The component-local container id")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwSizeType"),
          "dataSize",
          Some("The data size")
        )
      ),
      CppDoc.Type("void"),
      {
        val invokeProductRequest = outputPortInvokerName(productRequestPort.get)
        lines(
          s"""|const FwDpIdType globalId = this->getIdBase() + containerId;
              |const FwSizeType size = DpContainer::getPacketSizeForDataSize(dataSize);
              |this->$invokeProductRequest(0, globalId, size);"""
        )
      }
    )
    def getDpRecvHandler(portInstance: PortInstance) = {
      val portName = portInstance.getUnqualifiedName
      functionClassMember(
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
        if hasDataProducts then
          List.concat(
            lines(
              """|DpContainer container(id, buffer, this->getIdBase());
                 |// Convert global id to local id
                 |const FwDpIdType idBase = this->getIdBase();
                 |FW_ASSERT(id >= idBase, id, idBase);
                 |const FwDpIdType localId = id - idBase;
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
                        |  this->dpRecv_${name}_handler(container, status.e);
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
        else lines("""|(void) portNum;
                      |(void) id;
                      |(void) buffer;
                      |(void) status;
                      |// No data products defined""")
      )
    }
    addAccessTagAndComment(
      "PRIVATE",
      "Private data product handling functions",
      List.concat(
        guardedList (hasContainers && hasProductGetPort) (List(dpGet)),
        guardedList (hasContainers && hasProductRequestPort) (List(dpRequest)),
        productRecvPort.map(getDpRecvHandler).map(List(_)).getOrElse(Nil)
      )
    )
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

    def getContainer = guardedList (hasDataProducts) (
      List(
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
    )

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

    private def singleRecordSerializeFn(name: String, t: Type) = {
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
          s"""|const FwSizeType sizeDelta =
              |  sizeof(FwDpIdType) +
              |  $typeSize;
              |Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
              |if (this->dataBuffer.getBuffLength() + sizeDelta <= this->dataBuffer.getBuffCapacity()) {
              |  const FwDpIdType id = this->baseId + RecordId::$name;
              |  status = this->dataBuffer.serialize(id);
              |  FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
              |  status = this->dataBuffer.serialize(elt);
              |  FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
              |  this->dataSize += sizeDelta;
              |}
              |else {
              |  status = Fw::FW_SERIALIZE_NO_ROOM_LEFT;
              |}
              |return status;"""
        )
      )
    }

    private def arrayRecordSerializeFn(name: String, t: Type) = {
      val typeName = TypeCppWriter.getName(s, t)
      val paramType = s"const ${typeName}*"
      val eltSize = if (s.isPrimitive(t, typeName))
        s"sizeof($typeName)" else s"${typeName}::SERIALIZED_SIZE"
      functionClassMember(
        Some(s"""|Serialize a $name record into the packet buffer
                 |\\return The serialize status"""),
        s"serializeRecord_${name}",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(paramType),
            "array",
            Some(s"An array of ${typeName} elements")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("FwSizeType"),
            "size",
            Some("The array size")
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        {
          val serializeElts = (t match {
            // Optimize the U8 case
            case Type.U8 =>
              """|  const bool omitSerializedLength = true;
                 |  status = this->dataBuffer.serialize(array, size, omitSerializedLength);
                 |  FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);"""
            case _ =>
              """|  for (FwSizeType i = 0; i < size; i++) {
                 |    status = this->dataBuffer.serialize(array[i]);
                 |    FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
                 |  }"""
          }).stripMargin
          lines(
            s"""|FW_ASSERT(array != nullptr);
                |const FwSizeType sizeDelta =
                |  sizeof(FwDpIdType) +
                |  sizeof(FwSizeType) +
                |  size * $eltSize;
                |Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
                |if (this->dataBuffer.getBuffLength() + sizeDelta <= this->dataBuffer.getBuffCapacity()) {
                |  const FwDpIdType id = this->baseId + RecordId::$name;
                |  status = this->dataBuffer.serialize(id);
                |  FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
                |  status = this->dataBuffer.serialize(size);
                |  FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
                |$serializeElts
                |  this->dataSize += sizeDelta;
                |}
                |else {
                |  status = Fw::FW_SERIALIZE_NO_ROOM_LEFT;
                |}
                |return status;"""
          )
        }
      )
    }

    private def getSerializeFunctionMembers =
      recordsByName.map((id, record) => {
        val name = record.getName
        val t = record.recordType
        if record.isArray then arrayRecordSerializeFn(name, t)
        else singleRecordSerializeFn(name, t)
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

