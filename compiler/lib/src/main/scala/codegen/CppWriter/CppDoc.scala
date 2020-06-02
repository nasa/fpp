package fpp.compiler.codegen

/** A C++ document representing an .hpp file backed by zero or more
 *  .cpp files. */
case class CppDoc(
  hppFile: String,
  includeGuard: String,
  members: List[CppDoc.Member]
)

object CppDoc {

  /** A C++ class */
  case class Class(
    /** The name of the class */
    name: String,
    /** The superclass declarations, if any, after the colon */
    superclassDecls: Option[String],
    /** The class members */
    members: List[Class.Member]
  )
  object Class {
    sealed trait Member
    object Member {
      case class Class(c: CppDoc.Class) extends Member
      case class Constructor(constructor: CppDoc.Class.Constructor) extends Member
      case class Lines(lines: CppDoc.Lines) extends Member
      case class Function(function: CppDoc.Function) extends Member
    }
    case class Constructor(
      params: List[Function.Param],
      initializers: List[String],
      body: List[Line]
    )
  }

  /** A C++ function, either standalone or inside a class */
  case class Function(
    name: String,
    params: List[Function.Param],
    retType: Type,
    body: List[Line],
    constQualifier: Function.ConstQualifier = Function.NonConst,
    virtualQualifier: Function.VirtualQualifier = Function.NonVirtual
  )
  case object Function {
    case class Param(
      constQualifier: Function.ConstQualifier,
      t: Type,
      name: String,
      comment: Option[String]
    )
    sealed trait ConstQualifier
    case object Const extends ConstQualifier
    case object NonConst extends ConstQualifier
    sealed trait VirtualQualifier
    case object NonVirtual extends VirtualQualifier
    case object PureVirtual extends VirtualQualifier
    case object Virtual extends VirtualQualifier
  }


  /** A list of uninterpreted lines of C++ code */
  case class Lines(content: List[Line], output: Lines.Output = Lines.Hpp)
  object Lines {
    sealed trait Output
    case object Hpp extends Output
    case class Cpp(cppFile: String) extends Output
    case class Both(cppFile: String) extends Output
  }

  /** A C++ namespace */
  case class Namespace(name: String, members: List[Namespace.Member])
  object Namespace {
    type Member = CppDoc.Member
  }

  /** A C++ type. The .cpp spelling of the type can be different from the .hpp type. 
   *  E.g., the .cpp version may need more qualifiers. */
  case class Type(
    hppType: String,
    cppType: Option[String] = None
  )

  /** A CppWriter document member */
  sealed trait Member
  object Member {
    case class Class(c: CppDoc.Class) extends Member
    case class Lines(lines: CppDoc.Lines) extends Member
    case class Function(function: CppDoc.Function) extends Member
    case class Namespace(namespace: CppDoc.Namespace) extends Member
  }

}
