package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component data products */
case class ComponentDataProducts (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val containersById = component.containerMap.toList.sortBy(_._1)

  private val containersByName = component.containerMap.toList.sortBy(_._2.getName)

  private val recordsById = component.recordMap.toList.sortBy(_._1)

  private val recordsByName = component.recordMap.toList.sortBy(_._2.getName)

  def getTypeMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Types",
      List(
        getContainerIds,
        getContainerPriorities,
        getRecordIds,
        Container.getContainer
      ).flatten
    )

  def getVirtualFunctionMembers: List[CppDoc.Class.Member] = 
    addAccessTagAndComment(
      "PROTECTED",
      "Pure virtual functions to implement",
      containersByName.map((id, container) => {
        val name = container.getName
        functionClassMember(
          Some(s"Receive a container of type $name"),
          s"Dp_Recv_${name}_handler",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("DpContainer&"),
              "container",
              Some("The container")
            )
          ),
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.PureVirtual
        )
      }),
      CppDoc.Lines.Hpp
    )

  def getProtectedDpFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Functions for managing data products",
      List(
        functionClassMember(
          Some("Request a data product container"),
          "Dp_Request",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("ContainerId::T"),
              "containerId",
              Some("The container id")
            ),
            CppDoc.Function.Param(
              CppDoc.Type("FwDpBuffSizeType"),
              "size",
              Some("The buffer size")
            )
          ),
          CppDoc.Type("void"),
          lines(
            """|const FwDpIdType globalId = this->getIdBase() + containerId;
               |this->productRequestOut_out(0, globalId, size);"""
          )
        ),
        functionClassMember(
          Some("Send a data product"),
          "Dp_Send",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("DpContainer&"),
              "container",
              Some("The data product container")
            )
          ),
          CppDoc.Type("void"),
          lines(
            """|// Update the time tag
               |const Fw::Time timeTag = this->getTime();
               |container.setTimeTag(timeTag);
               |// Serialize the header into the packet
               |Fw::SerializeStatus status = container.serializeHeader();
               |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, status);
               |// Update the size of the buffer according to the data size
               |const FwDpBuffSizeType packetSize = container.getPacketSize();
               |Fw::Buffer buffer = container.getBuffer();
               |FW_ASSERT(packetSize <= buffer.getSize(), packetSize, buffer.getSize());
               |buffer.setSize(packetSize);
               |// Send the buffer
               |this->productSendOut_out(0, container.getId(), buffer);"""
          )
        )
      )
    )

  def getPrivateDpFunctionMembers: List[CppDoc.Class.Member] = {
    val portInstance = component.specialPortMap(Ast.SpecPortInstance.ProductRecv)
    val portName = portInstance.getUnqualifiedName
    addAccessTagAndComment(
      "PRIVATE",
      "Private data product handling functions",
      List(
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
            )
          ),
          CppDoc.Type("void"),
          // TODO: Range over the containers
          // TODO: Compute the priority values 10 and 20
          lines(
            """|DpContainer container(id, buffer, this->getIdBase());
                |// Convert global id to local id
                |const auto idBase = this->getIdBase();
                |FW_ASSERT(id >= idBase, id, idBase);
                |const auto localId = id - idBase;
                |// Switch on the local id"""
          ) ++ wrapInScope(
            "switch (localId) {",
            containersById.flatMap((id, container) => {
              val name = container.getName
              lines(
                s"""|case ContainerId::$name:
                    |  // Set the priority
                    |  container.setPriority(ContainerPriority::$name);
                    |  // Call the handler
                    |  this->Dp_Recv_${name}_handler(container);
                    |  break;"""
              )
            }) ++ lines (
              """|default:
                 |  FW_ASSERT(0);
                 |  break;"""
            ),
            "}"
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.NonConst,
          CppDoc.Function.Override
        ),
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
            containersById.map((id, container) => line(
              writeEnumConstant(container.getName, id)
            ))
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
            containersById.map((id, container) => {
              val priority = container.defaultPriority.getOrElse(BigInt(0))
              line(writeEnumConstant(container.getName, priority))
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
            recordsById.map((id, container) => line(
              writeEnumConstant(container.getName, id)
            ))
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

    private def getVariableMembers = List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("PRIVATE") ++
        CppDocWriter.writeDoxygenComment("The component base id") ++
        lines("FwDpIdType baseId;")
      )
    )

  }

}

