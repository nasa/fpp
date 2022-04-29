package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Utilities for writing C++ topology definitions */
abstract class TopologyCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends CppWriterLineUtils {

  val symbol = Symbol.Topology(aNode)

  val namespaceIdentList = s.getNamespaceIdentList(symbol)

  val name = aNode._2.data.name

  val t = s.a.topologyMap(symbol)

  val instances = t.instanceMap.keys.toList.sorted

  val instancesByBaseId = instances.sortWith {
    case (a, b) => if (a.baseId != b.baseId) a.baseId < b.baseId
    else a < b
  }

  def isActive(ci: ComponentInstance) =
    ci.component.aNode._2.data.kind == Ast.ComponentKind.Active

  def hasCommands(ci: ComponentInstance) =
    ci.component.commandMap.size > 0

  def hasParams(ci: ComponentInstance) =
    ci.component.paramMap.size > 0

  def addBannerComment(comment: String, ll: List[Line]): List[Line] =
    ll match {
      case Nil => Nil
      case _ => CppDocWriter.writeBannerComment(comment) ++ ll
    }

  def addComment(comment: String, ll: List[Line]): List[Line] =
    ll match {
      case Nil => Nil
      case _ => CppDocWriter.writeComment(comment) ++ ll
    }

  def getComponentName(ci: ComponentInstance): Name.Qualified =
    s.a.getQualifiedName(Symbol.Component(ci.component.aNode))

  def getComponentNameAsQualIdent(ci: ComponentInstance): String =
    getNameAsQualIdent(getComponentName(ci))

  def getShortName(name: Name.Qualified) = {
    val ens = s.a.getEnclosingNames(symbol)
    name.shortName(ens)
  }

  def getNameAsIdent(name: Name.Qualified) =
    CppWriter.identFromQualifiedName(getShortName(name))

  def getNameAsQualIdent(name: Name.Qualified) =
    CppWriter.translateQualifiedName(getShortName(name))

  def getSpecifierForPhase (phase: Int) (ci: ComponentInstance): 
    Option[InitSpecifier] = ci.initSpecifierMap.get(phase)

  def getCodeForPhase (phase: Int)(ci: ComponentInstance): Option[String] =
    getSpecifierForPhase(phase)(ci).map(is => is.aNode._2.data.code)

  def getCodeLinesForPhase (phase: Int) (ci: ComponentInstance): Option[List[Line]] = (
    getCodeForPhase (phase) (ci)
  ).map(lines)


}
