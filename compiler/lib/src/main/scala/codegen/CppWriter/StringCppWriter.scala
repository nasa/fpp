package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.ast.*
import fpp.compiler.codegen
import fpp.compiler.util.*

/** Write C++ string classes of given sizes */
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
        mkLines(CppDoc.Lines(CppDocWriter.writeBannerComment(s"$name class"))),
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
        lines(
          s"""|public:
              |
              |  enum {
              |    STRING_SIZE = $size,
              |    SERIALIZED_SIZE = STATIC_SERIALIZED_SIZE(STRING_SIZE)
              |  };
              |
              |  $name() : StringBase() { *this = ""; }
              |
              |  $name(const $name& src) : StringBase() { *this = src; }
              |
              |  $name(const StringBase& src) : StringBase() { *this = src; }
              |
              |  $name(const char* src) : StringBase() { *this = src; }
              |
              |  ~$name() {}
              |
              |  $name& operator=(const $name& src) {
              |    (void)StringBase::operator=(src);
              |    return *this;
              |  }
              |
              |  $name& operator=(const StringBase& src) {
              |    (void)StringBase::operator=(src);
              |    return *this;
              |  }
              |
              |  $name& operator=(const char* src) {
              |    (void)StringBase::operator=(src);
              |    return *this;
              |  }
              |
              |  const char* toChar() const { return this->m_buf; }
              |
              |  StringBase::SizeType getCapacity() const { return sizeof this->m_buf; }
              |
              |private:
              |
              |  char m_buf[BUFFER_SIZE(STRING_SIZE)];"""
        ).map(_.indentOut(2))
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
