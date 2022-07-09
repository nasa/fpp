package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Writes out C++ for struct definitions */
case class StructCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Struct(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getStruct(name)

  private val structType @ Type.Struct(_, _, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val strCppWriter = StringCppWriter(s, Some(name))

  private val members = structType.anonStruct.members

  private val sizes = structType.sizes

  private val formats = structType.formats

  // Map from member names to C++ type names
  private val membersNamed = members.map((n, t) => t match {
    case strType: Type.String => n -> strCppWriter.getClassName(strType)
    case otherType => n -> typeCppWriter.write(otherType)
  })

  private val membersNamedList = membersNamed.toList

  private val memberNames = members.keys.toList

  private def writeIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ): List[Line] = {
    val Right(a) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  private def getDefaultValues = {
    val defaultValue = structType.getDefaultValue match {
      case Some(s) => Some(s.anonStruct)
      case None => structType.anonStruct.getDefaultValue
    }
    defaultValue.get.members.map((n, t) =>
      (n, ValueCppWriter.write(s, t))
    ).toList
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name struct",
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
        name,
        Some("public Fw::Serializable"),
        getClassMembers
      )
    )
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/BasicTypes.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    ).map(CppWriter.headerString).map(line)
    val symbolHeaders = writeIncludeDirectives(s, aNode)
    val headers = userHeaders ++ symbolHeaders
    CppWriter.linesMember(
      addBlankPrefix(headers)
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemheaders = List(
      "cstdio",
      "cstring",
    ).map(CppWriter.headerString).map(line)
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/StringUtils.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp",
    ).sorted.map(CppWriter.headerString).map(line)
    CppWriter.linesMember(
      List(
        Line.blank :: systemheaders,
        Line.blank :: userHeaders,
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List(
      getStringClasses,
      getConstantMembers,
      getConstructorMembers,
      getOperatorMembers,
      getMemberFunctionMembers,
      getMemberVariableMembers,
    ).flatten

  private def getStringClasses: List[CppDoc.Class.Member] = {
    val strTypes = members.map((_, t) => t match {
      case t: Type.String => Some(t)
      case _ => None
    }).filter(_.isDefined).map(_.get).toList
    strTypes match {
      case Nil => Nil
      case l => strCppWriter.write(l)
    }
  }

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
            CppDocWriter.writeBannerComment("Constants") ++
            addBlankPrefix(
              wrapInEnum(
                List(
                  lines("//! The size of the serial representation"),
                  lines("SERIALIZED_SIZE ="),
                  lines(members.map((n, t) =>
                    s.getSerializedSizeExpr(t, membersNamed(n)) + (
                      if sizes.contains(n) then s" * ${sizes(n)}"
                      else ""
                    )).mkString(" +\n")).map(indentIn),
                ).flatten
              )
            )
        )
      )
    )

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    val defaultValues = getDefaultValues
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public")
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Constructors"),
          CppDoc.Lines.Both
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor (default value)"),
          Nil,
          "Serializable()" ::
            defaultValues
              .filterNot((n, _) => sizes.contains(n))
              .map((n, v) => members(n) match {
                case Type.String(_) => s"$n($v)"
                case t if s.isPrimitive(t, n) => s"$n($v)"
                case _ => s"$n(${v.split("\\(")(1)}"
              }),
          defaultValues.flatMap((n, v) =>
            if sizes.contains(n) then
              iterateN(sizes(n), lines(s"this->$n[i] = $v;"))
            else Nil
          )
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Copy constructor"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The source object")
            )
          ),
          List("Serializable()"),
          lines(
            s"set(" +
              memberNames.map(n => s"obj->$n").mkString(", ") +
              ");"
          )
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Member constructor"),
          writeMembersAsParams,
          Nil,
          lines(
            s"set(" +
              memberNames.map(n => s"$n").mkString(", ") +
              ");"
          )
        )
      )
    )
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] =
    List(

    )

  private def getMemberFunctionMembers: List[CppDoc.Class.Member] =
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(CppDocHppWriter.writeAccessTag("public"))
        ),
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            CppDocWriter.writeBannerComment("Member functions"),
            CppDoc.Lines.Both
          )
        ),
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Getter functions"),
          CppDoc.Lines.Both
        )
      ) :: getGetterFunctionMembers,
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Setter functions"),
          CppDoc.Lines.Both
        )
      ) :: getSetterFunctionMembers,
    ).flatten

  private def getGetterFunctionMembers: List[CppDoc.Class.Member] =
    membersNamedList.map((n, tn) =>
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some(s"Get member $n"),
          s"get$n",
          Nil,
          writeMemberAsReturnType((n, tn)),
          lines(s"return this->$n"),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      )
    )

  private def getSetterFunctionMembers: List[CppDoc.Class.Member] =
    CppDoc.Class.Member.Function(
      CppDoc.Function(
        Some("Set all values"),
        "set",
        writeMembersAsParams,
        CppDoc.Type("void"),
        memberNames.flatMap(n =>
          if sizes.contains(n) then
            iterateN(sizes(n), lines(s"this->$n[i] = $n[i];"))
          else
            lines(s"this->$n = $n;")
        )
      )
    ) ::
      membersNamedList.map((n, tn) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Set member $n"),
            s"set$n",
            List(
              writeMemberAsParam((n, tn))
            ),
            CppDoc.Type("void"),
            lines(s"this->$n = $n;")
          )
        )
      )

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("private"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Member variables") ++
            addBlankPrefix(membersNamedList.map((n, tn) =>
              if sizes.contains(n) then line(s"$tn $n[${sizes(n)}];")
              else line(s"$tn $n;")
            ))
        )
      )
    )

  private def writeMemberAsParam(member: (String, String)) = member match {
    case (n, tn) =>
      if sizes.contains(n) then
        CppDoc.Function.Param(
          CppDoc.Type(s"const $tn"),
          s"(&$n)[${sizes(n)}]",
          None
        )
      else
        CppDoc.Function.Param(
          CppDoc.Type(s"const $tn&"),
          s"$n",
          None
        )
  }

  private def writeMembersAsParams =
    membersNamedList.map(writeMemberAsParam)

  private def writeMemberAsReturnType(member: (String, String)) = member match {
    case (n, tn) =>
      if sizes.contains(n) then
        CppDoc.Type(s"const $tn*", Some(s"const $name::$tn*"))
      else
        CppDoc.Type(s"const $tn&", Some(s"const $name::$tn&"))
  }

  // Writes a for loop that iterates n times
  private def iterateN(n: Int, ll: List[Line]) =
    wrapInForLoop(
      "NATIVE_INT_TYPE i = 0",
      s"i < $n",
      "i++",
      ll
    )

}