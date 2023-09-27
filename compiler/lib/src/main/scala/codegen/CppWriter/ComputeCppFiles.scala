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
    locOpt: Option[Location],
    hppFileExtension: String = "hpp",
    cppFileExtension: String = "cpp"
  ) = {
    for {
      s <- addHppMappingNew(s, fileName, locOpt, hppFileExtension)
      s <- addCppMappingNew(s, fileName, locOpt, cppFileExtension)
    }
    yield s
  }

  /** Adds a mapping for an hpp file  */
  protected def addHppMapping(
    map: Map[String, Option[Location]],
    fileName: String,
    locOpt: Option[Location],
    hppFileExtension: String = "hpp"
  ) = addMapping(map, (s"$fileName.$hppFileExtension" -> locOpt))

  /** Adds a mapping for an hpp file  */
  protected def addHppMappingNew(
    s: State,
    fileName: String,
    locOpt: Option[Location],
    hppFileExtension: String = "hpp"
  ) = addMappingNew(s, (s"$fileName.$hppFileExtension" -> locOpt))

  /** Adds a mapping for a cpp file  */
  protected def addCppMapping(
    map: Map[String, Option[Location]],
    fileName: String,
    locOpt: Option[Location],
    cppFileExtension: String = "cpp"
  ) = addMapping(map, (s"$fileName.$cppFileExtension" -> locOpt))

  /** Adds a mapping for a cpp file  */
  protected def addCppMappingNew(
    s: State,
    fileName: String,
    locOpt: Option[Location],
    cppFileExtension: String = "cpp"
  ) = addMappingNew(s, (s"$fileName.$cppFileExtension" -> locOpt))

  /** Adds a mapping for one file */
  private def addMapping(
    map: Map[String, Option[Location]],
    mapping: (String, Option[Location])
  ) = {
    val (fileName, locOpt) = mapping
    (map.get(fileName), locOpt) match {
      case (Some(Some(prevLoc)), Some(loc)) =>
        Left(CodeGenError.DuplicateCppFile(fileName, loc, prevLoc))
      case _ => Right(map + mapping)
    }
  }

  /** Adds a mapping for one file */
  private def addMappingNew(
    s: State,
    mapping: (String, Option[Location])
  ) = {
    val m = s.locationMap
    val (fileName, locOpt) = mapping
    for {
      m1 <- (m.get(fileName), locOpt) match {
        case (Some(Some(prevLoc)), Some(loc)) =>
          Left(CodeGenError.DuplicateCppFile(fileName, loc, prevLoc))
        case _ => Right(m + mapping)
      }
    } yield s.copy(locationMap = m1)
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

    /** Gets the C++ file name for generated component Google Test harness base classes */
    def getComponentGTestBase(baseName: String) = s"${baseName}GTestBase"

    /** Gets the C++ file name for generated component test harness base classes */
    def getComponentTesterBase(baseName: String) = s"${baseName}TesterBase"

    /** Gets the C++ file name for generated component test harness implementation classes */
    def getComponentTestImpl(baseName: String) = s"${baseName}Tester"

    /** Gets the C++ file name for generated component test harness helpers */
    def getComponentTestHelper(baseName: String) = s"${baseName}TesterHelpers"

    /** Gets the C++ file name for generated component main test harness classes */
    def getComponentTestMain(baseName: String) = s"${baseName}TestMain"

  }

}
