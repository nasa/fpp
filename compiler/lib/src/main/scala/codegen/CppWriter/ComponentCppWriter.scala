package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

case class ComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Component(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getComponent(name)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val kindStr = data.kind match {
    case Ast.ComponentKind.Active => "Active"
    case Ast.ComponentKind.Passive => "Passive"
    case Ast.ComponentKind.Queued => "Queued"
  }

  private val members = data.members
  
  private val generalPorts = members.map(member =>
    member.node._2 match {
      case Ast.ComponentMember.SpecPortInstance(node) => node.data match {
        case p: Ast.SpecPortInstance.General => Some(p)
        case _ => None
      }
      case _ => None
    }).filter(_.isDefined).map(_.get)

  private val inputPorts = generalPorts.map(p => {
    p.kind match {
      case Ast.SpecPortInstance.Output => None
      case _ => Some(p)
    }
  }).filter(_.isDefined).map(_.get)

  private val outputPorts = generalPorts.map(p => {
    p.kind match {
      case Ast.SpecPortInstance.Output => Some(p)
      case _ => None
    }
  }).filter(_.isDefined).map(_.get)

  private val specialPorts = members.map(member =>
    member.node._2 match {
      case Ast.ComponentMember.SpecPortInstance(node) => node.data match {
        case p: Ast.SpecPortInstance.Special => Some(p)
        case _ => None
      }
      case _ => None
    }).filter(_.isDefined).map(_.get)

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component base class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = CppDoc.Member.Class(
      CppDoc.Class(
        AnnotationCppWriter.asStringOpt(aNode),
        s"${name}ComponentBase",
        Some(s"public Fw::${kindStr}ComponentBase"),
        getClassMembers
      )
    )
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val standardHeaders = List(
      "FpConfig.hpp",
      s"Fw/Comp/${kindStr}ComponnetBase.hpp"
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    CppWriter.linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemHeaders = List(
      "cstdio",
    ).map(CppWriter.systemHeaderString).map(line)
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/String.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp"
    ).sorted.map(CppWriter.headerString).map(line)
    CppWriter.linesMember(
      List(
        Line.blank :: systemHeaders,
        Line.blank :: userHeaders
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List(
      getInputPortGetters,
      getOutputPortConnectors,
    ).flatten
  }

  private def getInputPortGetters: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment(
                "Getters for typed input ports"
              ),
            ).flatten
          )
        )
      ),
      inputPorts.map(p => {
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some("Get input port at index"),
            s"get_${p.name}_InputPort",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "portNum",
                Some("The port number")
              )
            ),
            CppDoc.Type(s"${getQualifiedPortName(p)}*"),
            lines(
              s"""|FW_ASSERT(
                  |  portNum < this->getNum_${p.name}_InputPorts(),
                  |  static_cast<FwAssertArgType>(portNum)
                  | );
                  |
                  |return &this->m_${p.name}_InputPort[portNum];
                  |"""
            )
          )
        )
      })
    ).flatten
  }

  private def getOutputPortConnectors: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment("" +
                "Connect typed input ports to typed output ports"
              ),
            ).flatten
          )
        )
      ),
      outputPorts.map(p => {
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Connect port to ${p.name}[portNum]"),
            s"set_${p.name}_OutputPort",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "portNum",
                Some("The port number")
              ),
              CppDoc.Function.Param(
                CppDoc.Type(s"${getQualifiedPortName(p, false)}*"),
                "port",
                Some("The input port")
              )
            ),
            CppDoc.Type("void"),
            lines(
              s"""|FW_ASSERT(
                  |  portNum < this->getNum_${p.name}_OutputPorts(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |this->m_${p.name}_OutputPort[portNum].addCallPort(port);
                  |"""
            )
          )
        )
      })
    ).flatten
  }

  private def getQualifiedPortName(
    p: Ast.SpecPortInstance.General,
    matchKind: Boolean = true
  ) = {
    val portKind = (matchKind, p.kind) match {
      case (true, Ast.SpecPortInstance.Output) => "Output"
      case (false, Ast.SpecPortInstance.Output) => "Input"
      case (true, _) => "Input"
      case (false, _) => "Output"
    }

    p.port match {
      case Some(node) => node.data match {
        case Ast.QualIdent.Qualified(qualifier, name) =>
          qualifier.data.toIdentList.mkString("::") + s"::${portKind}${name.data}Port"
        case Ast.QualIdent.Unqualified(name) =>
          s"${portKind}${name}Port"
      }
      case None => ""
    }
  }

}
