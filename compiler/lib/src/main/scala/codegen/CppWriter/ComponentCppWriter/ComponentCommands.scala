package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component commands */
case class ComponentCommands (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedCommands = component.commandMap.toList.sortBy(_._1)

  def getCmdFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasCommands then Nil
    else List(
      getCmdFunctionMembers
    ).flatten
  }

  private def getCmdRegFunction: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            CppDocWriter.writeBannerComment(
              "Command registration"
            ),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some(
            s"""|\\brief Register commands with the Command Dispatcher
                |
                |Connect the dispatcher first
                |"""
          ),
          "regCommands",
          Nil,
          CppDoc.Type("void"),
          Nil
        )
      )
    )
  }

  def getCmdConstants: List[CppDoc.Class.Member] = {
    def annotationToStr[T](aNode: Ast.Annotated[T]) =
      AnnotationCppWriter.asStringOpt(aNode) match {
        case Some(str) => s" //!< $str"
        case _ => ""
      }
    def addComment(input: String, cmd: Command) =
      cmd match {
        case Command.NonParam(aNode, _) => s"$input${annotationToStr(aNode)}"
        case Command.Param(aNode, _) => s"$input${annotationToStr(aNode)}"
      }
    def writeEnum(input: (Command.Opcode, Command)) = {
      s"OPCODE_${input._2.getName.toUpperCase} = 0x${input._1.toString(16).toUpperCase}"
    }
    def writeEnumComma(input: (Command.Opcode, Command)) =
      s"${writeEnum(input)},"
    def writeEnums(cmds: List[(Command.Opcode, Command)]): List[String] =
      cmds match {
        case Nil => Nil
        case h :: Nil => List(addComment(writeEnum(h), h._2))
        case h :: t => addComment(writeEnumComma(h), h._2) :: writeEnums(t)
      }

    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            Line.blank :: lines(s"//! Command opcodes"),
            wrapInEnum(
              lines(
                writeEnums(sortedCommands).mkString("\n")
              )
            )
          ).flatten
        )
      )
    )
  }

}
