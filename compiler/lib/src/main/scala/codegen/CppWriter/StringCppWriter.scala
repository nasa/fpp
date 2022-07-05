package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.ast.*
import fpp.compiler.codegen
import fpp.compiler.util.*

/** Write C++ string classes of given sizees */
case class StringCppWriter(
  /** List of string sizes. One class will be generated per distinct size. */
  sizes: List[Int],
  /** Enclosing class name, including any qualifiers */
  enclosingClassQualified: Option[String] = None
) extends CppWriterLineUtils {

  def write: List[CppDoc.Class.Member] = {
    // Write access tag if these classes appear inside another class
    val accessTag = enclosingClassQualified match {
      case Some(_) =>
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            CppDocHppWriter.writeAccessTag("public")
          )
        )
      case None => CppDoc.Class.Member.Lines(CppDoc.Lines(Nil))
    }

    accessTag ::
      sizes.distinct.flatMap(size => {
        val name = s"StringSize${size.toString}"
        List(
          CppDoc.Class.Member.Lines(
            CppDoc.Lines(
              CppDocWriter.writeBannerComment(s"$name class"),
              CppDoc.Lines.Both
            )
          ),
          CppDoc.Class.Member.Class(
            writeClass(size, name)
          )
        )
      })
  }

  def writeClass(size: Int, name: String): CppDoc.Class = {
    CppDoc.Class(
      None,
      name,
      Some("public Fw::StringBase"),
      getClassMembers(size, name)
    )
  }

  private def getClassMembers(size: Int, name: String): List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            addBlankPrefix(wrapInEnum(lines(
              s"SERIALIZED_SIZE = $size + sizeof(FwBuffSizeType) //!< Size of buffer + storage of two size words"
            ))),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Default constructor"),
          Nil,
          List("StringBase()"),
          lines("this->m_buf[0] = 0;")
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
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
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
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
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
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
        )
      ),
      CppDoc.Class.Member.Destructor(
        CppDoc.Class.Destructor(
          Some("Destructor"),
          Nil
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Copy assignment operator"),
          "operator=",
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "other",
              None
            )
          ),
          CppDoc.Type(s"$name&", getCppType(s"$name&")),
          List(
            wrapInIf("this == &other", lines("return *this;")),
            List(
              Line.blank,
              line("Fw::StringUtils::string_copy(this->m_buf, other.toChar(), sizeof(this->m_buf));"),
              line("return *this;"),
            ),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("String base assignment operator"),
          "operator=",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const Fw::StringBase&"),
              "other",
              None
            )
          ),
          CppDoc.Type(s"$name&", getCppType(s"$name&")),
          List(
            wrapInIf("this == &other", lines("return *this;")),
            List(
              Line.blank,
              line("Fw::StringUtils::string_copy(this->m_buf, other.toChar(), sizeof(this->m_buf));"),
              line("return *this;"),
            ),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("char* assignment operator"),
          "operator=",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const char*"),
              "other",
              None
            )
          ),
          CppDoc.Type(s"$name&", getCppType(s"$name&")),
          List(
            line("Fw::StringUtils::string_copy(this->m_buf, other, sizeof(this->m_buf));"),
            line("return *this;"),
          )
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Retrieves char buffer of string"),
          "toChar",
          Nil,
          CppDoc.Type("const char*"),
          lines("return this->m_buf;"),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          None,
          "getCapacity",
          Nil,
          CppDoc.Type("NATIVE_UINT_TYPE"),
          lines("return sizeof(this->m_buf);"),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("private"),
            addBlankPrefix(lines(
              s"char m_buf[$size]; //!< Buffer for string storage"
            )),
          ).flatten
        )
      )
    )

  private def getCppType(name: String) = enclosingClassQualified match {
    case Some(qualifier) => Some(s"$qualifier::$name")
    case None => None
  }

}
