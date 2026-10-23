package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

/** Add dependencies */
object AddDependencies
  extends BasicUseAnalyzer
  with ModuleTemplateDefAnalyzer {

  override def specLocAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) = {
    val specLoc = node._2.data
    if a.includeDictionaryDeps && specLoc.isDictionaryDef
    then
      // We are visiting a dictionary specifier after visiting
      // the first topology. Add the depdendencies for the specifier.
      addDependencies (a) (specLoc)
    else
      // This is not a dictionary specifier, or we haven't seen a topology.
      // Nothing to do.
      Right(a)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    for {
      // Add dependencies based on explicit and implicit uses in the topology
      a <- super.defTopologyAnnotatedNode(a, node)
      // Add dependencies based on dictionary specifiers
      a <- if node._2.data.isDeployment && !a.includeDictionaryDeps
           then
             // This is the first deployment topology we have visited.
             // Set includeDictionaryDeps = true and add all dictionary dependencies
             // discovered so far.
             val a1 = a.copy(includeDictionaryDeps = true)
             val dictionarySpecLocs =
               a1.locationSpecifierMap.values.map(_.data).filter(_.isDictionaryDef)
             Result.foldLeft (dictionarySpecLocs.toList) (a1) {
               case (a, s) => addDependencies (a) (s)
             }
           else
             // This is the second or later deployment topology, or a
             // non-deployment topology; nothing to do
             Right(a)
    } yield a
  }

  override def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.StateMachine, use, node.data.isAbsolute)

  override def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Component, use, node.data.isAbsolute)

  override def templateUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Template, use, node.data.isAbsolute)

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    val isAbsolute = node.data.getIsAbsoluteOpt.get
    for {
      // Analyze as a constant
      a <- analyzeUse(a, Ast.SpecLoc.Constant, use, isAbsolute)
      // If in the form A.B, also analyze as an enumerated constant
      a <- use.qualifier match {
        case Nil => Right(a)
        case q => {
          val enumUse = Name.Qualified.fromIdentList(q)
          analyzeUse(a, Ast.SpecLoc.Type, enumUse, isAbsolute)
        }
      }
    } yield a
  }

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Port, use, node.data.isAbsolute)

  override def interfaceInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Instance, use, node.data.isAbsolute)

  override def interfaceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Interface, use, node.data.isAbsolute)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Type, use, node.data.getIsAbsoluteOpt.get)

  override def templateArgNode(a: Analysis, node: AstNode[Ast.TemplateArg]) =
    node.data match {
      case Ast.TemplateArg.Interface(instance) =>
        qualIdentNode (interfaceInstanceUse) (a, instance)
      case _ => super.templateArgNode(a, node)
    }

  /** Find the location specifier for a use in a scope.
   *  scopeNameList lists the enclosing scope names, with the innermost
   *  name at the head of the list. An absolute use is not resolved
   *  in the enclosing scopes. */
  def findLocation(
    map: Map[(Ast.SpecLoc.Kind, Name.Qualified), AstNode[Ast.SpecLoc]],
    scopeNameList: List[Name.Unqualified],
    kind: Ast.SpecLoc.Kind,
    use: Name.Qualified,
    isAbsolute: Boolean
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
      if isAbsolute then List(use) else helper(scopeNameList, Nil)
    }
    computeNameList.view.flatMap(name => map.get((kind, name)).map(_.data)).headOption
  }

  /** Get the file named in a location specifier */
  def getFile(specLoc: Ast.SpecLoc): File = {
    val loc = Locations.get(specLoc.file.id)
    File.Path(loc.getRelativePath(specLoc.file.data))
  }

  private def analyzeUse(
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    use: Name.Qualified,
    isAbsolute: Boolean
  ): Result =
    findLocation(
      a.locationSpecifierMap,
      a.scopeNameList,
      kind,
      use,
      isAbsolute
    ).map(addDependencies (a)).getOrElse(Right(a))

  private def addDependenciesHelper(a: Analysis, specLoc: Ast.SpecLoc, file: File): Result = {
    val dependencyFileSet = a.dependencyFileSet + file
    val directDependencyFileSet = a.level match {
      case 1 => a.directDependencyFileSet + file
      case _ => a.directDependencyFileSet
    }
    val a1 = a.copy(
      dependencyFileSet = dependencyFileSet,
      directDependencyFileSet = directDependencyFileSet
    )
    val result = for {
      tu <- Parser.parseFile (Parser.transUnit) (None) (file)
      pair <- ResolveSpecInclude.transUnit(a1, tu)
      a2 <- ComputeDependencies.tuList(
        pair._1.copy(
          scopeNameList = Nil,
          dependencyTuMap = pair._1.dependencyTuMap + (file -> pair._2)
        ),
        List(pair._2)
      )
    } yield a2.copy(scopeNameList = a1.scopeNameList)
    result match {
      case Left(FileError.CannotOpen(_, _)) => {
        val a = a1.copy(missingDependencyFileSet = a1.missingDependencyFileSet + file)
        Right(a)
      }
      case _ => result
    }
  }

  private def addDependencies (a: Analysis) (specLoc: Ast.SpecLoc): Result = {
    val file = getFile(specLoc)
    if !a.inputFileSet.contains(file) && !a.dependencyFileSet.contains(file)
    then addDependenciesHelper(a, specLoc, file)
    else Right(a)
  }

}
