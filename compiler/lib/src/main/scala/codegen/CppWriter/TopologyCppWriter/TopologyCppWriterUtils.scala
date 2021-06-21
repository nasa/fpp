package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Utilities for writing C++ topology definitions */
abstract class TopologyCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends LineUtils {

  val symbol = Symbol.Topology(aNode)

  val namespace = s.getNamespace(symbol)

  val name = aNode._2.data.name

  val t = s.a.topologyMap(symbol)

  val instances = t.instanceMap.keys.toList.sorted

  def addBannerComment(comment: String, ll: List[Line]): List[Line] =
    ll match {
      case Nil => Nil
      case _ => CppDocWriter.writeBannerComment(comment) ++ ll
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

  def wrapInScope(
    s1: String,
    ll: List[Line],
    s2: String
  ): List[Line] = ll match {
    case Nil => Nil
    case _ => List(lines(s1), ll.map(indentIn), lines(s2)).flatten
  }

  def wrapInAnonymousNamespace(ll: List[Line]): List[Line] =
    wrapInScope("namespace {", ll, "}")

  def wrapInNamespace(namespace: String, ll: List[Line]): List[Line] =
    wrapInScope(s"namespace $namespace {", ll, "}")

  def wrapInEnum(ll: List[Line]): List[Line] =
    wrapInScope("enum {", ll, "}")

  def getSpecifierForPhase (phase: Int) (ci: ComponentInstance): 
    Option[InitSpecifier] = s.a.initSpecifierMap.getOrElse(ci, Map()).
      get(phase)

  def getCodeForPhase (phase: Int)(ci: ComponentInstance): Option[String] =
    getSpecifierForPhase(phase)(ci).map(is => is.aNode._2.data.code)

  def getCodeLinesForPhase (phase: Int) (ci: ComponentInstance): Option[List[Line]] = (
    getCodeForPhase (CppWriter.Phases.instances) (ci)
  ).map(lines)


}
