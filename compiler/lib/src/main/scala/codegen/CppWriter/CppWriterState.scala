package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

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
    CppWriterState.identFromQualifiedName(a.getQualifiedName(s))

  /** Constructs an include guard from a qualified name and a kind */
  def includeGuardFromQualifiedName(s: Symbol, name: String, headerExtension: String = "HPP"): String = {
    val guard = getNamespaceIdentList(s) match {
      case Nil => name
      case names =>
        val prefix = CppWriterState.identFromQualifiedName(
          Name.Qualified.fromIdentList(names)
        )
        s"${prefix}_$name"
    }
    s"${guard}_$headerExtension"
  }

  /** Gets the C++ namespace associated with a symbol */
  def getNamespace(symbol: Symbol): Option[String] =
    getNamespaceIdentList(symbol) match {
      case Nil => None
      case identList => Some(identList.mkString("::"))
    }

  /** Gets the list of identifiers representing the namespace
   *  associated with a symbol */
  def getNamespaceIdentList(symbol: Symbol): List[String] =
    getQualifyingNameAsIdentList(a.parentSymbolMap.get(symbol))

  /** Gets the C++ unqualified name associated with a symbol.
   *  If a symbol is defined in a component or a state machine,
   *  then we prefix its name with the name of the enclosing
   *  component or state machine. */
  def getName(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(cs: Symbol.Component) => s"${cs.getUnqualifiedName}_$name"
      case Some(sm: Symbol.StateMachine) => s"${sm.getUnqualifiedName}_$name"
      case _ => name
    }
  }

  /** Write an FPP symbol as C++ */
  def writeSymbol(sym: Symbol): String = {
    val qualifiedName = {
      val identList = getQualifiedNameAsIdentList(Some(sym))
      Name.Qualified.fromIdentList(identList)
    }
    CppWriterState.writeQualifiedName(qualifiedName)
  }

  /** Write an FPP symbol as a C++ identifier */
  def writeSymbolAsIdent(sym: Symbol): String =
    writeSymbol(sym).replaceAll("::", "_")

  // Gets the C++ qualified name of a symbol as a list of identifiers.
  // For names declared inside components and state machines,
  // (a) add the name of the component or state machine as
  // a lexical prefix to the last name of the list and
  // (b) remove the name from the list. For example, if B
  // is a component name, then A.B.C becomes A, B_C.
  private def getQualifiedNameAsIdentList(
    symOpt: Option[Symbol]
  ): List[String] = symOpt match {
    case None => Nil
    case Some(sym) =>
      val psOpt = a.parentSymbolMap.get(sym)
      getQualifyingNameAsIdentList(psOpt, List(getName(sym)))
  }

  // Get the C++ name of a qualifying symbol as a list of identifiers.
  // Remove the names of components and state machines from the list.
  private def getQualifyingNameAsIdentList(
    symOpt: Option[Symbol],
    suffix: List[String] = Nil
  ): List[String] = symOpt match {
    case None => suffix
    case Some(sym) =>
      val psOpt = a.parentSymbolMap.get(sym)
      val suffix1 = sym match {
        case _: Symbol.Component => suffix
        case _: Symbol.StateMachine => suffix
        case _ => sym.getUnqualifiedName :: suffix
      }
      getQualifyingNameAsIdentList(psOpt, suffix1)
  }

  /** Get an include path for a symbol and a file name base */
  def getIncludePath(
    sym: Symbol,
    fileNameBase: String,
    headerExtension: String = "hpp"
  ): String = {
    val loc = sym.getLoc.tuLocation
    val fullPath = loc.getNeighborPath(fileNameBase)
    val path = removeLongestPathPrefix(fullPath)
    s"${path.toString}.$headerExtension"
  }

  /** Write include directives for autocoded files */
  def writeIncludeDirectives(usedSymbols: Iterable[Symbol]): List[String] = {
    def getIncludeFiles(sym: Symbol): List[String] = {
      val name = getName(sym)
      for {
        filenames <- sym match {
          case _: Symbol.AbsType => List(getIncludePath(sym, name))
          case _: Symbol.AliasType => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getAliasType(name))
          )
          case _: Symbol.Array => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getArray(name))
          )
          case _: Symbol.Component => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getComponent(name))
          )
          case _: Symbol.Enum => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getEnum(name))
          )
          case _: Symbol.Port => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getPort(name))
          )
          case stateMachine: Symbol.StateMachine =>
            val kind = StateMachine.getSymbolKind(stateMachine)
            List(getIncludePath(sym, ComputeCppFiles.FileNames.getStateMachine(name, kind)))
          case _: Symbol.Struct => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getStruct(name))
          )
          case _: Symbol.Topology => List(
            getIncludePath(sym, ComputeCppFiles.FileNames.getTopology(name))
          )
          case iface: Symbol.Interface =>
            // Resolve the uses to pick up the header dependencies
            // implied by imported interfaces
            val Right(a) = UsedSymbols.defInterfaceAnnotatedNode(
              this.a,
              this.a.interfaceMap(iface).aNode,
            )
            a.usedSymbolSet.flatMap(getIncludeFiles).toList
          case _: Symbol.Constant => List()
          case _: Symbol.EnumConstant => List()
          case _: Symbol.Module => List()
          case _: Symbol.ComponentInstance => List()
        }
      }
      yield filenames
    }

    usedSymbols.flatMap(getIncludeFiles).map(CppWriterState.headerString).toList
  }

  def isTypeSupportedInC(t: Type): Boolean = {
    t match {
      case Type.AliasType(node, aliasType) =>
        a.parentSymbolMap.get(Symbol.AliasType(node)) match {
          // Types that are definied inside some namespace cannot be included in C headers
          case Some(_) => false
          // Make sure all types in the alias chain meet the C requirements
          case None => isTypeSupportedInC(aliasType)
        }
      case Type.PrimitiveInt(_) => true
      case Type.Float(_) => true
      case _ => false
    }
  }

  /** Is t a primitive type (not serializable)? */
  def isPrimitive(t: Type, typeName: String): Boolean  = t.getUnderlyingType.isPrimitive

  /** Is t a string type? */
  def isStringType(t: Type) = t.getUnderlyingType match {
    case _: Type.String => true
    case _ => false
  }

}

object CppWriterState {


  /** Construct a header string */
  def headerString(s: String): String = {
    val q = "\""
    s"#include $q$s$q"
  }

  /** Constructs a C++ identifier from a qualified state machine symbol name */
  def identFromQualifiedSmSymbolName(
    sma: StateMachineAnalysis,
    s: StateMachineSymbol
  ): String = identFromQualifiedName(sma.getQualifiedName(s))

  /** Constructs a C++ identifier from a qualified name */
  def identFromQualifiedName(name: Name.Qualified): String =
    name.toString.replaceAll("\\.", "_")

  /** Writes a qualified name */
  def writeQualifiedName(name: Name.Qualified): String =
    name.toString.replaceAll("\\.", "::")

}
