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

  override def specCommandAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ) =
    for {
      a <- super.specCommandAnnotatedNode(a, node)
      a <- frameworkBufferUses(a, CheckFrameworkBufferSizes.cmdArgBuffer)
    } yield a

  override def specEventAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ) =
    for {
      a <- super.specEventAnnotatedNode(a, node)
      a <- frameworkBufferUses(a, CheckFrameworkBufferSizes.logBuffer)
    } yield a

  override def specParamAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecParam]]
  ) =
    for {
      a <- super.specParamAnnotatedNode(a, node)
      a <- frameworkBufferUses(a, CheckFrameworkBufferSizes.paramBuffer)
    } yield a

  override def specTlmChannelAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ) =
    for {
      a <- super.specTlmChannelAnnotatedNode(a, node)
      a <- frameworkBufferUses(a, CheckFrameworkBufferSizes.tlmBuffer)
    } yield a

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

  /** Add a dependency for the framework constant that specifies the size
   *  of a framework buffer. The constant is optional: if it has no
   *  location specifier, then there is no dependency. */
  private def frameworkBufferUses(
    a: Analysis,
    buffer: CheckFrameworkBufferSizes.Buffer
  ): Result =
    analyzeUse(
      a,
      Ast.SpecLoc.Constant,
      Name.Qualified.fromIdentList(List(buffer.sizeName)),
      true
    )

  private def addDependenciesForFile(a: Analysis, file: File): Result =
    val scopeNameList = a.scopeNameList
    val result = for {
      tu <- Parser.parseFile (Parser.transUnit) (None) (file)
      (a, tu) <- ResolveSpecInclude.transUnit(a, tu)
      a <- ComputeDependencies.tuList(
        a.copy(
          scopeNameList = Nil,
          dependencyTuMap = a.dependencyTuMap + (file -> tu)
        ),
        List(tu)
      )
    } yield a.copy(scopeNameList = scopeNameList)
    result match {
      case Left(FileError.CannotOpen(_, _)) => {
        val missingDependencyFileSet = a.missingDependencyFileSet + file
        Right(a.copy(missingDependencyFileSet = missingDependencyFileSet))
      }
      case _ => result
    }

  private def updateDirectDependencyFileSet(a: Analysis, file: File) =
    val directDependencyFileSet = a.directDependencyFileSet + file
    a.copy(directDependencyFileSet = directDependencyFileSet)

  private def updateDependencyFileSet(a: Analysis, file: File) =
    val dependencyFileSet = a.dependencyFileSet + file
    a.copy(dependencyFileSet = dependencyFileSet)

  private def addDependencies (a: Analysis) (specLoc: Ast.SpecLoc): Result = {
    val file = getFile(specLoc)
    if !a.inputFileSet.contains(file)
    then
      val a1 = if a.level == 1
               then updateDirectDependencyFileSet(a, file)
               else a
      if !a1.dependencyFileSet.contains(file)
      then
        val a2 = updateDependencyFileSet(a1, file)
        addDependenciesForFile(a2, file)
      else Right(a1)
    else Right(a)
  }

}
