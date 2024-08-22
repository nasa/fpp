package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component configuration objects */
case class TopConfigObjects(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  private val bannerComment = "Component configuration objects"

  def getCppLines: List[Line] = {
    addBannerComment(
      bannerComment,
      addBlankPrefix(getConfigObjectLines)
    )
  }

  def getHppLines: List[Line] = {
    addBannerComment(
      bannerComment,
      addBlankPrefix(getConfigObjectHeaderLines)
    )
  }

  private def getConfigObjectHeaderLines: List[Line] = {
    def specialGen(ci: ComponentInstance, qn: String) = {
      qn match {
        case "Svc.Health" => getHealthCode(ci, getPingDeclarationBlock)
        case _ => None
      }
    }

    val pairs = instances.map(ci => (ci, getSpecialCode(ci, specialGen))).
      filter(_._2.isDefined).map {
        case (ci, codeOpt) => (ci, codeOpt.get)
      }
    wrapInNamespace(
      "ConfigObjects",
      addBlankPostfix(
        flattenWithBlankPrefix(
          pairs.map {
            case (ci, code) => wrapInNamespace(
              CppWriter.identFromQualifiedName(ci.qualifiedName),
              code
            )
          }
        )
      )
    )
  }

  private def getConfigObjectLines: List[Line] = {
    def specialGen(ci: ComponentInstance, qn: String) = {
      qn match {
        case "Svc.Health" => getHealthCode(ci, getPingEntryBlock)
        case _ => None
      }
    }

    def getCode(ci: ComponentInstance) = getSpecialCode(ci, specialGen) match {
      case None => (
        getCodeForPhase (CppWriter.Phases.configObjects) (ci)
      ).map(lines)
      case code => code
    }
    val pairs = instances.map(ci => (ci, getCode(ci))).
      filter(_._2.isDefined).map {
        case (ci, codeOpt) => (ci, codeOpt.get)
      }
    wrapInNamespace(
      "ConfigObjects",
      addBlankPostfix(
        flattenWithBlankPrefix(
          pairs.map {
            case (ci, code) => wrapInNamespace(
              CppWriter.identFromQualifiedName(ci.qualifiedName),
              code
            )
          }
        )
      )
    )
  }

  private def getSpecialCode(ci: ComponentInstance, specialGen: (ComponentInstance, String) => Option[List[Line]]): Option[List[Line]] = {
    val symbol = Symbol.Component(ci.component.aNode)
    val qn = s.a.getQualifiedName(symbol)
    specialGen(ci, qn.toString)
  }

  // Get the lines for a ping port entry
  private def getPingEntryBlock(map: Map[Int, ComponentInstance], maxNum: Int): List[Line] = {
    def getEntryLines(ciOpt: Option[ComponentInstance]) = {
      val ss = ciOpt match {
        // Entry for connected port
        case Some(ci) =>
          val name = ci.qualifiedName
          val ident = CppWriter.identFromQualifiedName(name)
          List(
            s"PingEntries::$ident::WARN,",
            s"PingEntries::$ident::FATAL,",
            s"$q$ident$q"
          )
        // Dummy entry for unconnected port
        case None => List("0", "0", "")
      }
      wrapInScope("{", ss.map(line), "},")
    }
    wrapInScope(
      "Svc::Health::PingEntry pingEntries[NUM_PING_ENTRIES] = {",
      // Loop over all ports in the range 0..maxNum.
      // Entries are positional, so we must generate code
      // for any unconnected entries in this range.
      List.range(0, maxNum+1).flatMap(n => getEntryLines(map.get(n))),
      "};",
    )
  }

  private def getPingDeclarationBlock(map: Map[Int, ComponentInstance], maxNum: Int): List[Line] = {
    val numEntries = maxNum + 1
    List(
         "//!< Number of entries in the pingEntryies array",
         s"constexpr FwSizeType NUM_PING_ENTRIES = $numEntries;",
         "//!< Ping entry configuration for Svc::Health",
         "extern Svc::Health::PingEntry pingEntries[NUM_PING_ENTRIES];",
    ).map(line)
  }

  private def getHealthCode(ci: ComponentInstance, blockGen: (Map[Int, ComponentInstance], Int) => List[Line]): Option[List[Line]] =
    for {
      // Get the ping out port
      pingOutPort <- {
        def isPingOutPort(pi: PortInstance) =
          s.a.isGeneralPort(pi, PortInstance.Direction.Output, "Svc.Ping")
        ci.component.portMap.values.filter(isPingOutPort) match {
          // Found it
          case p :: _ => Some(p)
          // No ping out port: abort
          case _ => None
        }
      }
      // Map the port numbers to ports, and compute the max port number
      mapAndMaxNum <- {
        val pii = PortInstanceIdentifier(ci, pingOutPort)
        val m0: Map[Int, ComponentInstance] = Map()
        val mn = t.getConnectionsFrom(pii).foldLeft ((m0, 0)) {
          case ((m, n), c) =>
            val n1 = t.getPortNumber(pii.portInstance, c).get
            (
              m + (n1 -> c.to.port.componentInstance),
              if (n1 > n) n1 else n
            )
        }
        // Abort if the map is empty.
        // In this case there are no ping connections.
        if (mn._1.size > 0) Some(mn) else None
      }
    }
    yield {
      val (map, maxNum) = mapAndMaxNum
      blockGen(map, maxNum)
    }

}
