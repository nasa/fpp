package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component configuration objects */
case class TopConfigObjects(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getLines: List[Line] = {
    addBannerComment(
      "Component configuration objects",
      addBlankPrefix(getConfigObjectLines)
    )
  }

  private def getConfigObjectLines: List[Line] = {
    def getCode(ci: ComponentInstance) = getSpecialCode(ci) match {
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
              getNameAsIdent(ci.qualifiedName),
              code
            )
          }
        )
      )
    )
  }

  private def getSpecialCode(ci: ComponentInstance): Option[List[Line]] = {
    val symbol = Symbol.Component(ci.component.aNode)
    val qn = s.a.getQualifiedName(symbol)
    qn.toString match {
      case "Svc.Health" => getHealthCode(ci)
      case _ => None
    }
  }

  private def getHealthCode(ci: ComponentInstance): Option[List[Line]] =
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
      // Get the lines for a ping port entry
      def getEntryLines(ciOpt: Option[ComponentInstance]) = {
        val ss = ciOpt match {
          // Entry for connected port
          case Some(ci) =>
            val name = ci.qualifiedName
            val ident = getNameAsIdent(name)
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
      val (map, maxNum) = mapAndMaxNum
      wrapInScope(
        "Svc::Health::PingEntry pingEntries[] = {",
        // Loop over all ports in the range 0..maxNum.
        // Entries are positional, so we must generate code 
        // for any unconnected entries in this range.
        List.range(0, maxNum+1).flatMap(n => getEntryLines(map.get(n))),
        "};"
      )
    }

}
