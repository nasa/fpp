package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.codegen.CppWriterState.builtInTypes
import fpp.compiler.util.*

/** C++ Writer state */
case class CppWriterState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String = ".",
  /** The include guard prefix */
  guardPrefix: Option[String] = None,
  /** The list of include path prefixes */
  pathPrefixes: List[String] = Nil,
  /** The default string size */
  defaultStringSize: Int = CppWriterState.defaultDefaultStringSize,
  /** The name of the tool using the CppWriter */
  toolName: Option[String] = None,
  /** The map from strings to locations */
  locationMap: Map[String, Option[Location]] = Map()
) {

  /** Removes the longest prefix from a Java path */
  def removeLongestPathPrefix(path: File.JavaPath): File.JavaPath =
    File.removeLongestPrefix(pathPrefixes)(path)

  /** Gets the relative path for a file */
  def getRelativePath(fileName: String): File.JavaPath = {
    val path = java.nio.file.Paths.get(fileName).toAbsolutePath.normalize
    removeLongestPathPrefix(path)
  }

  /** Constructs an include guard from the prefix and a name */
  def includeGuardFromPrefix(name: String): String = {
    val rawPrefix = guardPrefix.getOrElse(getRelativePath(".").toString)
    val prefix = "[^A-Za-z0-9_]".r.replaceAllIn(rawPrefix, "_")
    prefix match {
      case "" =>  s"${name}_HPP"
      case _ => s"${prefix}_${name}_HPP"
    }
  }

  /** Constructs a C++ identifier from a qualified symbol name */
  def identFromQualifiedSymbolName(s: Symbol): String =
    CppWriter.identFromQualifiedName(a.getQualifiedName(s))

  /** Constructs an include guard from a qualified name and a kind */
  def includeGuardFromQualifiedName(s: Symbol, name: String): String = {
    val guard = a.getEnclosingNames(s) match {
      case Nil => name
      case names =>
        val prefix = CppWriter.identFromQualifiedName(
          Name.Qualified.fromIdentList(names)
        )
        s"${prefix}_$name"
    }
    s"${guard}_HPP"
  }

  /** Gets the C++ namespace associated with a symbol */
  def getNamespace(symbol: Symbol): Option[String] =
    getNamespaceIdentList(symbol) match {
      case Nil => None
      case identList => Some(identList.mkString("::"))
    }

  /** Gets the list of identifiers representing the namespace
   *  associated with a symbol */
  def getNamespaceIdentList(symbol: Symbol): List[String] = {
    removeComponentQualifiers(a.parentSymbolMap.get(symbol), Nil)
  }

  /** Gets the unqualified name associated with a symbol.
   *  If a symbol is defined in a component, then we prefix its name
   *  with the component name. This is to work around the fact that
   *  we cannot define classes inside components in the F Prime XML. */
  def getName(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(cs: Symbol.Component) => s"${cs.getUnqualifiedName}_$name"
      case _ => name
    }
  }

  /** Write an FPP symbol as C++ */
  def writeSymbol(sym: Symbol): String = {
    val qualifiedName = sym match {
      // For component symbols, use the qualified name
      case cs: Symbol.Component => a.getQualifiedName(cs)
      // For other symbols, remove component qualifiers
      case _ => {
        val identList = removeComponentQualifiers(Some(sym), Nil)
        Name.Qualified.fromIdentList(identList)
      }
    }
    CppWriter.writeQualifiedName(qualifiedName)
  }

  // Skip component names in qualifiers
  // Those appear in the prefixes of definition names
  private def removeComponentQualifiers(
    symOpt: Option[Symbol],
    out: List[String]
  ): List[String] = symOpt match {
    case None => out
    case Some(sym) =>
      val psOpt = a.parentSymbolMap.get(sym)
      val out1 = sym match {
        case _: Symbol.Component => out
        case _ => getName(sym) :: out
      }
      removeComponentQualifiers(psOpt, out1)
  }

  /** Get an include path for a symbol and a file name base */
  def getIncludePath(
    sym: Symbol,
    fileNameBase: String
  ): String = {
    val loc = sym.getLoc.tuLocation
    val fullPath = loc.getNeighborPath(fileNameBase)
    val path = removeLongestPathPrefix(fullPath)
    s"${path.toString}.hpp"
  }

  /** Write include directives for autocoded files */
  def writeIncludeDirectives(usedSymbols: Iterable[Symbol]): List[String] = {
    def getDirectiveForSymbol(sym: Symbol): Option[String] =
      for {
        fileName <- sym match {
          case Symbol.AbsType(node) => getName(Symbol.AbsType(node)) match {
            case name if isBuiltInType(name) => None
            case name => Some(name)
          }
          case Symbol.Array(node) => Some(
            ComputeCppFiles.FileNames.getArray(getName(Symbol.Array(node)))
          )
          case Symbol.Component(node) => Some(
            ComputeCppFiles.FileNames.getComponent(getName(Symbol.Component(node)))
          )
          case Symbol.Enum(node) => Some(
            ComputeCppFiles.FileNames.getEnum(getName(Symbol.Enum(node)))
          )
          case Symbol.Port(node) => Some(
            ComputeCppFiles.FileNames.getPort(getName(Symbol.Port(node)))
          )
           case Symbol.StateMachine(node) => Some(
            ComputeCppFiles.FileNames.getStateMachine(getName(Symbol.StateMachine(node)))
          )
          case Symbol.Struct(node) => Some(
            ComputeCppFiles.FileNames.getStruct(getName(Symbol.Struct(node)))
          )
          case Symbol.Topology(node) => Some(
            ComputeCppFiles.FileNames.getTopology(getName(Symbol.Topology(node)))
          )
          case _ => None
        }
      }
      yield CppWriter.headerString(getIncludePath(sym, fileName))

    usedSymbols.map(getDirectiveForSymbol).filter(_.isDefined).map(_.get).toList
  }

  /** Is t a built-in type? */
  def isBuiltInType(typeName: String): Boolean = builtInTypes.contains(typeName)

  /** Is t a primitive type (not serializable)? */
  def isPrimitive(t: Type, typeName: String): Boolean  = t.isPrimitive || isBuiltInType(typeName)

  /** Is t a string type? */
  def isStringType(t: Type) = t match {
    case _: Type.String => true
    case _ => false
  }

}

object CppWriterState {

  /** The default default string size */
  val defaultDefaultStringSize = 80

  /** A mapping from special built-in types to their
   *  default values */
  val zero: Value.Integer = Value.Integer(0)
  val builtInTypes: Map[String,Value.Integer] = Map(
    "FwChanIdType" -> zero,
    "FwDpIdType" -> zero,
    "FwDpPriorityType" -> zero,
    "FwEnumStoreType" -> zero,
    "FwEventIdType" -> zero,
    "FwIndexType" -> zero,
    "FwOpcodeType" -> zero,
    "FwPacketDescriptorType" -> zero,
    "FwPrmIdType" -> zero,
    "FwSignedSizeType" -> zero,
    "FwSizeStoreType" -> zero,
    "FwSizeType" -> zero,
    "FwTimeBaseStoreType" -> zero,
    "FwTimeContextStoreType" -> zero,
    "FwTlmPacketizeIdType" -> zero,
  )

}
