package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component data product definitions */
case class DpComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val name = s.getName(symbol)

  private val componentFileName = ComputeCppFiles.FileNames.getComponent(name)

  private val dpFileName = ComputeCppFiles.FileNames.getDpComponent(name)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val baseClassName = s"${name}ComponentBase"

  private val dpBaseClassName = s"${name}DpComponentBase"

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, dpFileName)
    CppWriter.createCppDoc(
      s"Data product base class for $name component",
      dpFileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      Some(
        addSeparatedString(
          s"\\class $dpBaseClassName\n\\brief Auto-generated data product base class for $name component",
          AnnotationCppWriter.asStringOpt(aNode)
        )
      ),
      dpBaseClassName,
      Some(s"public Fw::$baseClassName"),
      getClassMembers
    )
    List(
      List(hppIncludes, cppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val standardHeaders = List(
      "FpConfig.hpp",
      "Fw/Com/ComPacket.hpp",
      "Fw/Dp/DpContainer.hpp",
      s"${s.getRelativePath(componentFileName).toString}.hpp",
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val headers = List(
      "Fw/Types/Assert.hpp",
      s"${s.getRelativePath(dpFileName).toString}.hpp",
    )
    linesMember(
      addBlankPrefix(headers.map(CppWriter.headerString).map(line)),
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = List(
    getConstructionMembers,
  ).flatten

  private def getConstructionMembers: List[CppDoc.Class.Member] = List(
    writeAccessTagAndComment(
      "PROTECTED",
      "Construction and destruction"
    ),
    List(
      constructorClassMember(
        Some(s"Construct $dpBaseClassName object"),
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const char*"),
            "compName",
            Some("The component name"),
            Some("\"\"")
          )
        ),
        List(s"${baseClassName}(compName)"),
        Nil
      ),
      destructorClassMember(
        Some(s"Destroy $dpBaseClassName object"),
        Nil,
        CppDoc.Class.Destructor.Virtual
      )
    )
  ).flatten

//  private def getMutexOperationMembers: List[CppDoc.Class.Member] = {
//    if !hasGuardedInputPorts then Nil
//    else List(
//      writeAccessTagAndComment(
//        "PROTECTED",
//        "Mutex operations for guarded ports",
//        Some(
//          """|You can override these operations to provide more sophisticated
//             |synchronization
//             |"""
//        )
//      ),
//      List(
//        functionClassMember(
//          Some("Lock the guarded mutex"),
//          "lock",
//          Nil,
//          CppDoc.Type("void"),
//          Nil,
//          CppDoc.Function.Virtual
//        ),
//        functionClassMember(
//          Some("Unlock the guarded mutex"),
//          "unLock",
//          Nil,
//          CppDoc.Type("void"),
//          Nil,
//          CppDoc.Function.Virtual
//        )
//      )
//    ).flatten
//  }
//
//  private def getDispatchFunctionMember: List[CppDoc.Class.Member] = {
//    if data.kind == Ast.ComponentKind.Passive then Nil
//    else List(
//      writeAccessTagAndComment(
//        data.kind match {
//          case Ast.ComponentKind.Active => "PRIVATE"
//          case Ast.ComponentKind.Queued => "PROTECTED"
//          case _ => ""
//        },
//        "Message dispatch functions"
//      ),
//      List(
//        functionClassMember(
//          Some("Called in the message loop to dispatch a message from the queue"),
//          "doDispatch",
//          Nil,
//          CppDoc.Type(
//            "MsgDispatchStatus",
//            Some("Fw::QueuedComponentBase::MsgDispatchStatus")
//          ),
//          Nil,
//          CppDoc.Function.Virtual
//        )
//      )
//    ).flatten
//  }
//
//  private def getTimeFunctionMember: List[CppDoc.Class.Member] = {
//    if !hasTimeGetPort then Nil
//    else List(
//      writeAccessTagAndComment(
//        "PROTECTED",
//        "Time"
//      ),
//      List(
//        functionClassMember(
//          Some(
//            s"""| Get the time
//                |
//                |\\return The current time
//                |"""
//          ),
//          "getTime",
//          Nil,
//          CppDoc.Type("Fw::Time"),
//          Nil
//        )
//      )
//    ).flatten
//  }
//
//  private def getMsgSizeVariableMember: List[CppDoc.Class.Member] = {
//    if !hasSerialAsyncInputPorts then Nil
//    else List(
//      linesClassMember(
//        List(
//          CppDocHppWriter.writeAccessTag("PRIVATE"),
//          lines(
//            """|
//             |//! Stores max message size
//               |NATIVE_INT_TYPE m_msgSize;
//               |"""
//          )
//        ).flatten
//      )
//    )
//  }
//
//  private def getMutexVariableMembers: List[CppDoc.Class.Member] = {
//    if !(hasGuardedInputPorts|| hasParameters) then Nil
//    else List(
//      linesClassMember(
//        List(
//          CppDocHppWriter.writeAccessTag("PRIVATE"),
//          CppDocWriter.writeBannerComment(
//            "Mutexes"
//          ),
//          if !hasGuardedInputPorts then Nil
//          else lines(
//            """|
//               |//! Mutex for guarded ports
//               |Os::Mutex m_guardedPortMutex;
//               |"""
//          ),
//          if !hasParameters then Nil
//          else lines(
//            """|
//               |//! Mutex for locking parameters during sets and saves
//               |Os::Mutex m_paramLock;
//               |"""
//          )
//        ).flatten
//      )
//    )
//  }

}
