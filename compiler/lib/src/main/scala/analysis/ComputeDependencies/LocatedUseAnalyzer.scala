package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze the uses that resolve to location specifiers */
trait LocatedUseAnalyzer extends BasicUseAnalyzer {

  /** A use that resolves to a location specifier */
  def locatedUse(a: Analysis, specLoc: Ast.SpecLoc): Result

  // The uses in a template body are uses of the file that defines the
  // template, whether or not the template is expanded
  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) =
    for {
      a <- super.defModuleTemplateAnnotatedNode(a, aNode)
      a <- visitList(a, aNode._2.data.members, matchModuleMember)
    } yield a

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    for {
      a <- super.specTemplateExpandAnnotatedNode(a, aNode)
      a <- Result.foldLeft (node.data.args) (a) ((a, arg) => arg.data match {
        case Ast.TemplateArg.Constant(value) => exprNode(a, value)
        case Ast.TemplateArg.Type(typeName) => typeNameNode(a, typeName)
        case Ast.TemplateArg.Interface(instance) =>
          qualIdentNode (interfaceInstanceUse) (a, instance)
      })
    } yield a
  }

  override def templateUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Template, use)

  override def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.StateMachine, use)

  override def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Component, use)

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    for {
      // Analyze as a constant
      a <- analyzeUse(a, Ast.SpecLoc.Constant, use)
      // If in the form A.B, also analyze as an enumerated constant
      a <- use.qualifier match {
        case Nil => Right(a)
        case q => {
          val enumUse = Name.Qualified.fromIdentList(q)
          analyzeUse(a, Ast.SpecLoc.Type, enumUse)
        }
      }
    } yield a

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Port, use)

  override def interfaceInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Instance, use)

  override def interfaceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Interface, use)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Type, use)

  def getFile(specLoc: Ast.SpecLoc): File = LocatedUseAnalyzer.getFile(specLoc)

  private def analyzeUse(a: Analysis, kind: Ast.SpecLoc.Kind, use: Name.Qualified): Result =
    LocatedUseAnalyzer.findLocation(a.locationSpecifierMap, a.scopeNameList, kind, use).
      map(locatedUse(a, _)).getOrElse(Right(a))

}

object LocatedUseAnalyzer {

  /** Find the location specifier for a use in a scope.
   *  scopeNameList lists the enclosing scope names, with the innermost
   *  name at the head of the list. */
  def findLocation(
    map: Map[(Ast.SpecLoc.Kind, Name.Qualified), AstNode[Ast.SpecLoc]],
    scopeNameList: List[Name.Unqualified],
    kind: Ast.SpecLoc.Kind,
    use: Name.Qualified
  ): Option[Ast.SpecLoc] = {
    def computeNameList: List[Name.Qualified] = {
      def helper(prefix: List[Name.Unqualified], result: List[Name.Qualified]): List[Name.Qualified] = {
        prefix match {
          case Nil => (use :: result).reverse
          case head :: tail => {
            val name = Name.Qualified.fromIdentList(prefix.reverse ++ use.toIdentList)
            helper(tail, name :: result)
          }
        }
      }
      helper(scopeNameList, Nil)
    }
    computeNameList.view.flatMap(name => map.get((kind, name)).map(_.data)).headOption
  }

  /** Get the file named in a location specifier */
  def getFile(specLoc: Ast.SpecLoc): File = {
    val loc = Locations.get(specLoc.file.id)
    File.Path(loc.getRelativePath(specLoc.file.data))
  }

}
