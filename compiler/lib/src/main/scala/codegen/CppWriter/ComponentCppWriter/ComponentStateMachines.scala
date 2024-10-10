package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  val externalStateMachineWriter = ComponentExternalStateMachines(s, aNode)

  val internalStateMachineWriter = ComponentInternalStateMachines(s, aNode)

  def getAnonymousNamespaceLines: List[Line] =
    internalStateMachineWriter.getAnonymousNamespaceLines

  def getConstantMembers: List[CppDoc.Class.Member] = {
    lazy val enumLines = smInstancesByName.map(
      (name, _) => line(s"$name,")
    )
    lazy val memberLines = List.concat(
      CppDocWriter.writeDoxygenComment("State machine identifiers"),
      wrapInEnumClass("SmId", enumLines, Some("FwEnumStoreType"))
    )
    lazy val lcm = linesClassMember(memberLines)
    guardedList (hasStateMachineInstances) (List(lcm))
  }

  def getPrivateFunctionMembers: List[CppDoc.Class.Member] =
    internalStateMachineWriter.getPrivateFunctionMembers

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    externalStateMachineWriter.getFunctionMembers,
    internalStateMachineWriter.getProtectedFunctionMembers
  )

  def getTypeMembers: List[CppDoc.Class.Member] =
    internalStateMachineWriter.getTypeMembers

  def getVariableMembers: List[CppDoc.Class.Member] = getSmInstanceMembers

  /** Writes the dispatch cases, if any, for state machine instances */
  def writeDispatchCases: List[Line] = List.concat(
    externalStateMachineWriter.writeDispatchCase,
    internalStateMachineWriter.writeDispatchCase
  )

  private def getSmInstanceMember(
    name: Name.Unqualified,
    smi: StateMachineInstance
  ): CppDoc.Class.Member = {
    val typeName = writeStateMachineImplType(smi.symbol)
    linesClassMember(
      Line.blank ::
      lines(
        s"""|//! State machine $name
            |$typeName m_stateMachine_$name;"""
      )
    )
  }

  private def getSmInstanceMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      s"State machine instances",
      smInstancesByName.map(getSmInstanceMember),
      CppDoc.Lines.Hpp
    )

}
