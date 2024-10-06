package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentInternalStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  /** Gets the function members */
  def getFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    getSignalSendFunctions,
    getOverflowHooks,
    getVirtualActions,
    getVirtualGuards
  )

  /** Gets the type members */
  def getTypeMembers: List[CppDoc.Class.Member] = 
    addAccessTagAndComment(
      "PROTECTED",
      "Types for internal state machines",
      List.concat(
        SignalBufferWriter.getSignalBuffer,
        getStateMachines
      )
    )

  /** Writes the dispatch case, if any, for internal state machine instances */
  def writeDispatchCase: List[Line] =
    // TODO
    Nil

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private def getSignalSendFunctions: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private def getStateMachines: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private def getVirtualActions: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private def getVirtualGuards: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private object SignalBufferWriter extends ComponentCppWriterUtils(s, aNode) {

    def getSignalBuffer: List[CppDoc.Class.Member] =
      guardedList (hasInternalStateMachineInstances) (
        List(
          classClassMember(
            Some("Buffer for serializing internal state machine signals"),
            "SmSignalBuffer",
            Some("public Fw::SerializeBufferBase"),
            List.concat(
              getTypeMembers,
              getConstantMembers,
              getFunctionMembers,
              getVariableMembers
            )
          )
        )
      )

    /** The signal types and the signal string size */
    private def signalTypesAndStringSize: (Set[Type], BigInt) =
      smSymbols.foldLeft ((Set(), BigInt(0))) {
        case ((ts, maxStringSize), sym) => {
          val signals = StateMachine.getSignals(sym.node._2.data)
          signals.foldLeft ((ts, maxStringSize)) {
            case ((ts, maxStringSize), signal) =>
              signal._2.data.typeName match {
                case Some(tn) =>
                  s.a.typeMap(tn.id) match {
                    case t: Type.String => (
                      ts + Type.String(None),
                      maxStringSize.max(getStringSize(s, t))
                    )
                    case t => (ts + t, maxStringSize)
                  }
                case None => (ts, maxStringSize)
              }
          }
        }
      }

    private val signalTypes: List[Type] =
      signalTypesAndStringSize._1.toList.sortBy(writeSignalTypeName)

    private val signalStringSize: BigInt = signalTypesAndStringSize._2

    private val hasSignalTypes: Boolean = !signalTypes.isEmpty

    private def getConstantMembers: List[CppDoc.Class.Member] =
      linesClassMember(CppDocHppWriter.writeAccessTag("public")) ::
      List(getSerializedSizeConstant)

    private def getFunctionMembers: List[CppDoc.Class.Member] =
      List(
        linesClassMember(CppDocHppWriter.writeAccessTag("public")),
        linesClassMember(
          Line.blank ::
          lines(
            """|//! Get the buffer capacity
               |Fw::Serializable::SizeType getBuffCapacity() const {
               |  return sizeof(this->m_buff);
               |}
               |
               |//! Get the buffer address (non-const)
               |U8* getBuffAddr() {
               |  return this->m_buff;
               |}
               |
               |//! Get the buffer address (const)
               |const U8* getBuffAddr() const {
               |  return this->m_buff;
               |}"""
          )
        )
      )

    private def getSerializedSizeConstant: CppDoc.Class.Member = {
      val comment = CppDocWriter.writeDoxygenComment(
        "The serialized size"
      )
      val terms = "2 * sizeof(FwEnumStoreType)" ::
        (guardedList (hasSignalTypes) (List("sizeof(SignalTypeUnion)")))
      val sum = s"${terms.mkString(" +\n")};"
      val constantLines = line("static constexpr FwSizeType SERIALIZED_SIZE =") ::
        lines(sum).map(indentIn)
      linesClassMember(List.concat(comment, constantLines))
    }

    private def getSignalTypeUnion: CppDoc.Class.Member = {
      val members = signalTypes.map (
        t => {
          val cppType = writeSignalTypeName(t)
          val typeIdent = cppType.replaceAll("::", "_")
          val sizeIdent = s"size_of_$typeIdent"
          val sizeExpr = writeSignalTypeSize(t)
          line(s"BYTE $sizeIdent[$sizeExpr];")
        }
      )
      val comment = CppDocWriter.writeDoxygenComment(
        "The union of the signal types, for sizing"
      )
      val union = wrapInScope("union SignalTypeUnion {", members, "};")
      linesClassMember(List.concat(comment, union))
    }

    private def getTypeMembers: List[CppDoc.Class.Member] =
      guardedList (hasSignalTypes) (
        linesClassMember(CppDocHppWriter.writeAccessTag("private")) ::
        List(getSignalTypeUnion)
      )

    private def getVariableMembers: List[CppDoc.Class.Member] =
      List(
        linesClassMember(CppDocHppWriter.writeAccessTag("private")),
        linesClassMember(
          Line.blank ::
          lines(
            """|//! The buffer
               |U8 m_buff[SERIALIZED_SIZE];"""
          )
        )
      )

    private def writeSignalTypeName(t: Type) = t match {
      case t: Type.String => "string"
      case _ => TypeCppWriter.getName(s, t)
    }

    private def writeSignalTypeSize(t: Type): String =
      t match {
        case _: Type.String =>
          s"Fw::StringBase::STATIC_SERIALIZED_SIZE(${signalStringSize.toString})"
        case _ => writeSerializedSizeExpr(s, t, TypeCppWriter.getName(s, t))
      }

  }

  private case class StateMachineWriter(sm: StateMachine)
    extends ComponentCppWriterUtils(s, aNode)
  {

    def getStateMachine: CppDoc.Class.Member =
      // TODO
      ???
  }

}
