package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology constants */
case class TopConstants(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getLines: List[Line] = 
    addBannerComment(
      "Constants",
      flattenWithBlankPrefix(
        List(
          getComponentConfigLines,
          getBaseIdLines,
          getCPULines,
          getInstanceIdLines,
          getPriorityLines,
          getQueueSizeLines,
          getStackSizeLines,
          getTaskIdLines
        )
      )
    )

  private def getComponentConfigLines: List[Line] = {
    val getCode = getCodeForPhase (CppWriter.Phases.configConstants) _
    val pairs = instances.map(ci => (ci, getCode(ci))).
      filter(_._2.isDefined).map { 
        case (ci, codeOpt) => (ci, codeOpt.get)
      }
    wrapInNamespace(
      "ConfigConstants",
      pairs.flatMap { 
        case (ci, code) => wrapInNamespace(
          getNameAsIdent(ci.qualifiedName),
          lines(code)
        )
      }
    )
  }

  private def generateEnum(
    namespace: String,
    f: ComponentInstance => Option[String],
    il: List[ComponentInstance] = instances
  ): List[Line] = {
    wrapInNamespace(
      namespace,
      wrapInEnum(
        il.map(f).filter(_.isDefined).
        map(_.get).map(s => line(s"$s,"))
      )
    )
  }

  private def getBaseIdLines: List[Line] =
    generateEnum(
      "BaseIds",
      ci => {
        val name = getNameAsIdent(ci.qualifiedName)
        val value = CppWriter.writeId(ci.baseId)
        Some(s"$name = $value")
      },
      instancesByBaseId
    )

  private def getCPULines: List[Line] =
    generateEnum(
      "CPUs",
      ci => ci.cpu.map(
        cpu => {
          val name = getNameAsIdent(ci.qualifiedName)
          s"$name = $cpu"
        }
      )
    )

  private def getInstanceIdLines: List[Line] =
    generateEnum(
      "InstanceIds",
      ci => {
        val name = getNameAsIdent(ci.qualifiedName)
        Some(s"$name")
      }
    )

  private def getPriorityLines: List[Line] =
    generateEnum(
      "Priorities",
      ci => ci.priority.map(
        priority => {
          val name = getNameAsIdent(ci.qualifiedName)
          s"$name = $priority"
        }
      )
    )

  private def getQueueSizeLines: List[Line] =
    generateEnum(
      "QueueSizes",
      ci => ci.queueSize.map(
        queueSize => {
          val name = getNameAsIdent(ci.qualifiedName)
          s"$name = $queueSize"
        }
      )
    )

  private def getStackSizeLines: List[Line] =
    generateEnum(
      "StackSizes",
      ci => ci.stackSize.map(
        stackSize => {
          val name = getNameAsIdent(ci.qualifiedName)
          s"$name = $stackSize"
        }
      )
    )

  private def getTaskIdLines: List[Line] =
    generateEnum(
      "TaskIds",
      ci => {
        val c = ci.component
        val kind = c.aNode._2.data.kind
        kind match {
          case Ast.ComponentKind.Active => 
            val name = getNameAsIdent(ci.qualifiedName)
            Some(name.toString)
          case _ => None
        }
      }
    )

}
