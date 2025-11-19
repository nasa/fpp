package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

/** Add dependencies */
object AddDependencies extends BasicUseAnalyzer {

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
      a <- if !a.includeDictionaryDeps
           then
             // This is the first topology we have visited.
             // Set includeDictionaryDeps = true and add all dictionary dependencies
             // discovered so far.
             val a1 = a.copy(includeDictionaryDeps = true)
             val dictionarySpecLocs =
               a1.locationSpecifierMap.values.map(_.data).filter(_.isDictionaryDef)
             Result.foldLeft (dictionarySpecLocs.toList) (a1) {
               case (a, s) => addDependencies (a) (s)
             }
           else
             // This is the second or later topology; nothing to do
             Right(a)
    } yield a
  }

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.ComponentInstance, use)

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

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Topology, use)

  override def interfaceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Interface, use)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Type, use)

  private def analyzeUse(a: Analysis, kind: Ast.SpecLoc.Kind, use: Name.Qualified): Result = {
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
      helper(a.scopeNameList, Nil)
    }
    def findLocation(nameList: List[Name.Qualified]): Option[Ast.SpecLoc] = {
      nameList match {
        case Nil => None
        case head :: tail => a.locationSpecifierMap.get((kind, head)) match {
          case opt @ Some(_) => opt.map(_.data)
          case None => findLocation(tail)
        }
      }
    }
    val nameList = computeNameList
    val location = findLocation(nameList)
    location.map(addDependencies (a)).getOrElse(Right(a))
  }

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
        pair._1.copy(scopeNameList = Nil),
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
    val loc = Locations.get(specLoc.file.id)
    val path = loc.getRelativePath(specLoc.file.data)
    val file = File.Path(path)
    if !a.inputFileSet.contains(file) && !a.dependencyFileSet.contains(file)
    then addDependenciesHelper(a, specLoc, file)
    else Right(a)
  }

}
