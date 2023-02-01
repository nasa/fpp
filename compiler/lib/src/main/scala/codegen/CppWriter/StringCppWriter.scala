package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.ast.*
import fpp.compiler.codegen
import fpp.compiler.util.*

/** Write C++ string classes of given sizees */
case class StringCppWriter(
  /** CppWriterState */
  s: CppWriterState,
  /** Enclosing class name, including any qualifiers */
  enclosingClassQualified: Option[String] = None
) extends CppWriterUtils {

  /** Get max string size */
  def getSize(str: Type.String): Int = str.size match {
    case Some(typeNode) => s.a.valueMap(typeNode.id) match {
      case Value.EnumConstant(value, _) => value._2.toInt
      case Value.PrimitiveInt(value, _) => value.toInt
      case Value.Integer(value) => value.toInt
      case _ => s.defaultStringSize
    }
    case None => s.defaultStringSize
  }

  /** Compute the string class name from a String type */
  def getClassName(str: Type.String): String = s"StringSize${getSize(str)}"

  /** Compute the string class name from a given size */
  def getClassName(size: Int): String = s"StringSize${size}"

  /** Compute the qualified string class name from a String type */
  def getQualifiedClassName(str: Type.String, namespaceNames: List[String]): String =
    namespaceNames.map(n => s"$n::").mkString("") + getClassName(str)

  /** Compute the qualified string class name from a given size */
  def getQualifiedClassName(size: Int, namespaceNames: List[String]): String =
    namespaceNames.map(n => s"$n::").mkString("") + getClassName(size)

  /** Helper for writing C++ string classes */
  private def writeHelper[T](
    strTypes: List[Type.String],
    mkLines: CppDoc.Lines => T,
    mkClass: CppDoc.Class => T
  ): List[T] = strTypes.map(getSize).distinct.flatMap(
    size => {
      val name = getClassName(size)
      List(
        mkLines(
          CppDoc.Lines(
            CppDocWriter.writeBannerComment(s"$name class"),
            CppDoc.Lines.Both
          )
        ),
        mkClass(writeClass(size))
      )
    }
  )

  /** Writes the C++ string classes */
  def write(strTypes: List[Type.String]): List[CppDoc.Member] =
    writeHelper(strTypes, CppDoc.Member.Lines.apply, CppDoc.Member.Class.apply)

  /** Writes the C++ string classes as nested classes */
  def writeNested(strTypes: List[Type.String]): List[CppDoc.Class.Member] =
    linesClassMember(
      CppDocHppWriter.writeAccessTag("public")
    ) ::
      writeHelper(
        strTypes,
        CppDoc.Class.Member.Lines.apply,
        CppDoc.Class.Member.Class.apply
      )

  def writeClass(size: Int): CppDoc.Class = {
    CppDoc.Class(
      None,
      getClassName(size),
      Some("public Fw::StringBase"),
      getClassMembers(size)
    )
  }

  private def getClassMembers(size: Int): List[CppDoc.Class.Member] = {
    val name = getClassName(size)
    List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("public"),
          addBlankPrefix(
            wrapInEnum(
              List(
                line("//! The size of the string length plus the size of the string buffer"),
                line(s"SERIALIZED_SIZE = sizeof(FwBuffSizeType) + $size")
              )
            )
          ),
        ).flatten
      ),
      constructorClassMember(
        Some("Default constructor"),
        Nil,
        List("StringBase()"),
        lines("this->m_buf[0] = 0;")
      ),
      constructorClassMember(
        Some("Char array constructor"),
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const char*"),
            "src",
            None
          )
        ),
        List("StringBase()"),
        lines(
          "Fw::StringUtils::string_copy(this->m_buf, src, sizeof(this->m_buf));"
        )
      ),
      constructorClassMember(
        Some("String base constructor"),
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const Fw::StringBase&"),
            "src",
            None
          )
        ),
        List("StringBase()"),
        lines(
          "Fw::StringUtils::string_copy(this->m_buf, src.toChar(), sizeof(this->m_buf));"
        )
      ),
      constructorClassMember(
        Some("Copy constructor"),
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $name&"),
            "src",
            None
          )
        ),
        List("StringBase()"),
        lines(
          "Fw::StringUtils::string_copy(this->m_buf, src.toChar(), sizeof(this->m_buf));"
        )
      ),
      destructorClassMember(
        Some("Destructor"),
        Nil
      ),
      functionClassMember(
        Some("Copy assignment operator"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $name&"),
            "other",
            None
          )
        ),
        getCppType(s"$name&"),
        List(
          wrapInIf("this == &other", lines("return *this;")),
          List(
            Line.blank,
            line("Fw::StringUtils::string_copy(this->m_buf, other.toChar(), sizeof(this->m_buf));"),
            line("return *this;"),
          ),
        ).flatten
      ),
      functionClassMember(
        Some("String base assignment operator"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const Fw::StringBase&"),
            "other",
            None
          )
        ),
        getCppType(s"$name&"),
        List(
          wrapInIf("this == &other", lines("return *this;")),
          List(
            Line.blank,
            line("Fw::StringUtils::string_copy(this->m_buf, other.toChar(), sizeof(this->m_buf));"),
            line("return *this;"),
          ),
        ).flatten
      ),
      functionClassMember(
        Some("char* assignment operator"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const char*"),
            "other",
            None
          )
        ),
        getCppType(s"$name&"),
        List(
          line("Fw::StringUtils::string_copy(this->m_buf, other, sizeof(this->m_buf));"),
          line("return *this;"),
        )
      ),
      functionClassMember(
        Some("Retrieves char buffer of string"),
        "toChar",
        Nil,
        CppDoc.Type("const char*"),
        lines("return this->m_buf;"),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ),
      functionClassMember(
        None,
        "getCapacity",
        Nil,
        CppDoc.Type("NATIVE_UINT_TYPE"),
        lines("return sizeof(this->m_buf);"),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ),
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("private"),
          addBlankPrefix(lines(
            s"char m_buf[$size]; //!< Buffer for string storage"
          )),
        ).flatten
      )
    )
  }

  private def getCppType(typeName: String) = {
    CppDoc.Type(s"$typeName",
      enclosingClassQualified match {
        case Some(qualifier) => Some(s"$qualifier::$typeName")
        case None => None
      }
    )
  }
}
