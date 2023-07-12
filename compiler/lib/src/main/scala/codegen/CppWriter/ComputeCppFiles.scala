package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** ====================================================================== 
 *  Computes the names of the C++ files to generate
 *  Checks for duplicates that would cause a name collision. 
 *  ======================================================================*/
trait ComputeCppFiles extends AstStateVisitor {

  type State = CppWriterState

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  /** Adds mappings for hpp and cppfiles */
  protected def addMappings(
    s: State,
    fileName: String,
    locOpt: Option[Location]
  ) = {
    val m = s.locationMap
    for {
      m <- addHppMapping(m, fileName, locOpt)
      m <- addCppMapping(m, fileName, locOpt)
    }
    yield s.copy(locationMap = m)
  }

  /** Adds a mapping for an hpp file  */
  private def addHppMapping(
    s: Map[String, Option[Location]],
    fileName: String,
    locOpt: Option[Location]
  ) =
    addMapping(s, (s"$fileName.hpp" -> locOpt))

  /** Adds a mapping for a cpp file  */
  private def addCppMapping(
    s: Map[String, Option[Location]],
    fileName: String,
    locOpt: Option[Location]
  ) = addMapping(s, (s"$fileName.cpp" -> locOpt))

  /** Adds a mapping for one file */
  private def addMapping(
    s: Map[String, Option[Location]],
    mapping: (String, Option[Location])
  ) = {
    val (fileName, locOpt) = mapping
    (s.get(fileName), locOpt) match {
      case (Some(Some(prevLoc)), Some(loc)) =>
        Left(CodeGenError.DuplicateCppFile(fileName, loc, prevLoc))
      case _ => Right(s + mapping)
    }
  }

}

object ComputeCppFiles {

  /** Names of generated C++ files */
  object FileNames {

    /** Gets the C++ file name for generated constants */
    def getConstants = "FppConstantsAc"

    /** Gets the C++ file name for generated arrays */
    def getArray(baseName: String) = s"${baseName}ArrayAc"

    /** Gets the C++ file name for generated enums */
    def getEnum(baseName: String) = s"${baseName}EnumAc"

    /** Gets the C++ file name for generated components */
    def getComponent(baseName: String) = s"${baseName}ComponentAc"

    /** Gets the C++ file name for generated component implementation templates */
    def getComponentImpl(baseName: String) = baseName

    /** Gets the C++ file name for generated ports */
    def getPort(baseName: String) = s"${baseName}PortAc"

    /** Gets the C++ file name for generated structs */
    def getStruct(baseName: String) = s"${baseName}SerializableAc"

    /** Gets the C++ file name for generated topologies */
    def getTopology(baseName: String): String = s"${baseName}TopologyAc"

  }

}
