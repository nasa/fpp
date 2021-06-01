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
      pingOutPort <- {
        def isPingOutPort(pi: PortInstance) =
          s.a.isGeneralPort(pi, PortInstance.Direction.Output, "Svc.Ping")
        ci.component.portMap.values.filter(isPingOutPort) match {
          case p :: _ => Some(p)
          case _ => None
        }
      }
      cis <- {
        val pii = PortInstanceIdentifier(ci, pingOutPort)
        val cs = t.getConnectionsFrom(pii).toList
        cs match {
          case c :: _ => Some(cs.map(_.to.port.componentInstance))
          case _ => None
        }
      }
    }
    yield {
      def getEntryLines(ci: ComponentInstance) = {
        val name = ci.qualifiedName
        val ident = getNameAsIdent(name)
        val qualIdent = getNameAsQualIdent(name)
        wrapInScope(
          "{",
          List(
            s"PingEntries::$ident::WARN,",
            s"PingEntries::$ident::FATAL,",
            s"$qualIdent.getObjName()"
          ).map(line),
          "},"
        )
      }
      val entryLines = Nil
      wrapInScope(
        "Svc::HealthImpl::PingEntry pingEntries[] = {",
        cis.flatMap(getEntryLines),
        "}"
      )
    }

}
