package fpp.compiler.codegen

/** A C++ document representing an .hpp file and one or more .cpp files */
case class CppDoc(
  /** The description */
  description: String,
  /** The hpp file */
  hppFile: CppDoc.HppFile,
  /** The cpp file name */
  cppFileName: String,
  /** The members */
  members: List[CppDoc.Member],
  /** An optional tool name */
  toolNameOpt: Option[String] = None,
  /** An optional file banner */
  fileBannerOpt: Option[CppDoc.FileBanner] = None
) {

  /** Get the file banner */
  def getFileBanner: CppDoc.FileBanner =
    fileBannerOpt.getOrElse(CppDoc.DefaultFileBanner(toolNameOpt))

}

object CppDoc {

  /** An hpp file */
  case class HppFile(
    /** The file name */
    name: String,
    /** The include guard */
    includeGuard: String
  )

  /** A C++ class */
  case class Class(
    /** An optional comment */
    comment: Option[String],
    /** The name of the class */
    name: String,
    /** The superclass declarations, if any, after the colon */
    superclassDecls: Option[String],
    /** The class members */
    members: List[Class.Member],
    /** The class final qualifier, if any, before the colon */
    qualifier: Class.FinalQualifier = Class.NonFinal
  )

  object Class {
    sealed trait FinalQualifier
    case object Final extends FinalQualifier
    case object NonFinal extends FinalQualifier
    sealed trait Member
    object Member {
      case class Class(c: CppDoc.Class) extends Member
      case class Constructor(constructor: CppDoc.Class.Constructor) extends Member
      case class Destructor(destructor: CppDoc.Class.Destructor) extends Member
      case class Lines(lines: CppDoc.Lines) extends Member
      case class Function(function: CppDoc.Function) extends Member
    }
    case class Constructor(
      comment: Option[String],
      params: List[Function.Param],
      initializers: List[String],
      body: List[Line],
      explicitQualifier: Constructor.ExplicitQualifier = Constructor.NotExplicit,
      cppFileNameBaseOpt: Option[String] = None
    )
    object Constructor {
      sealed trait ExplicitQualifier
      case object NotExplicit extends ExplicitQualifier
      case object Explicit extends ExplicitQualifier
    }
    case class Destructor(
      comment: Option[String],
      body: List[Line],
      virtualQualifier: Destructor.VirtualQualifier = Destructor.NonVirtual,
      cppFileNameBaseOpt: Option[String] = None,
    )
    object Destructor {
      sealed trait VirtualQualifier
      case object NonVirtual extends VirtualQualifier
      case object Virtual extends VirtualQualifier
    }
  }

  /** A C++ function, either standalone or inside a class */
  case class Function(
    /** An optional comment */
    comment: Option[String],
    /** The function name */
    name: String,
    /** The formal parameters */
    params: List[Function.Param],
    /** The return type */
    retType: Type,
    /** The function body */
    body: List[Line],
    /** The static or virtual qualifier */
    svQualifier: Function.SVQualifier = Function.NonSV,
    /** The const qualifier */
    constQualifier: Function.ConstQualifier = Function.NonConst,
    /** The cpp file to write to */
    cppFileNameBaseOpt: Option[String] = None,
  )
  case object Function {
    case class Param(
      t: Type,
      name: String,
      comment: Option[String] = None,
      default: Option[String] = None
    )
    sealed trait SVQualifier
    case object Final extends SVQualifier
    case object NonSV extends SVQualifier
    case object Override extends SVQualifier
    case object PureVirtual extends SVQualifier
    case object Static extends SVQualifier
    case object Virtual extends SVQualifier
    sealed trait ConstQualifier
    case object Const extends ConstQualifier
    case object NonConst extends ConstQualifier
  }

  /** A C++ namespace */
  case class Namespace(name: String, members: List[Member])

  /** A C++ type. The .cpp spelling of the type can be different from the .hpp type.
   *  E.g., the .cpp version may need more qualifiers. */
  case class Type(
    hppType: String,
    cppType: Option[String] = None
  ) {

    def getCppType: String = cppType match {
      case Some(t) => t
      case None => hppType
    }

  }

  /** A CppWriter document member */
  sealed trait Member
  object Member {
    case class Class(c: CppDoc.Class) extends Member
    case class Lines(lines: CppDoc.Lines) extends Member
    case class Function(function: CppDoc.Function) extends Member
    case class Namespace(namespace: CppDoc.Namespace) extends Member
  }

  /** A list of uninterpreted lines of C++ code */
  case class Lines(
    content: List[Line],
    output: Lines.Output = Lines.Hpp,
    cppFileNameBaseOpt: Option[String] = None,
  )
  object Lines {
    sealed trait Output
    case object Both extends Output
    case object Cpp extends Output
    case object Hpp extends Output
  }

  /** A generic file banner */
  trait FileBanner {
    /** Get the title */
    def getTitle(fileName: String): String
    /** Get the author */
    def getAuthor(fileName: String): String
    /** Get description */
    def getDescription(fileName:String, genericDescription: String): String
  }

  /** The default file banner */
  case class DefaultFileBanner(toolNameOpt: Option[String])
    extends CppDoc.FileBanner
  {
    override def getTitle(fileName: String): String = fileName
    override def getAuthor(fileName: String): String =
      s"Generated by ${toolNameOpt.getOrElse("fpp tools")}"
    override def getDescription(fileName: String, genericDescription: String): String =
      genericDescription
  }

}
