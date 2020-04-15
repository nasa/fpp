package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

/** Map uses to locations */
object MapUsesToLocs extends UseAnalyzer {

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.ComponentInstance, use)

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

  override def portInstanceUse(
    a: Analysis,
    node: AstNode[Ast.QualIdent],
    componentInstanceUse: Name.Qualified,
    port: Name.Unqualified
  ) =
    analyzeUse(a, Ast.SpecLoc.ComponentInstance, componentInstanceUse)

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Port, use)

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    analyzeUse(a, Ast.SpecLoc.Topology, use)

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
      helper(a.moduleNameList, Nil)
    }
    def findLocation(nameList: List[Name.Qualified]): Option[Ast.SpecLoc] = {
      nameList match {
        case Nil => None
        case head :: tail => a.locationSpecifierMap.get((kind, head)) match {
          case specLoc @ Some(_) => specLoc
          case None => findLocation(tail)
        }
      }
    }
    def getFile(specLoc: Ast.SpecLoc): Result.Result[File] = {
      val loc = Locations.get(specLoc.file.getId)
      for { path <- loc.relativePath(specLoc.file.getData) } 
      yield File.Path(path)
    }
    def addDependency(specLoc: Ast.SpecLoc, file: File): Result = {
      val a1 = a.copy(dependencyFileSet = a.dependencyFileSet + file)
      val result = for {
        tu <- Parser.parseFile (Parser.transUnit) (None) (file)
        pair <- ResolveSpecInclude.transUnit(a1, tu)
        a2 <- ComputeDependencies.tuList(pair._1, List(pair._2))
      } yield a2
      result match {
        case Left(FileError.CannotOpen(_, _)) => {
          val a = a1.copy(missingDependencyFileSet = a1.missingDependencyFileSet + file)
          Right(a)
        }
        case _ => result
      }
    }
    val nameList = computeNameList
    val location = findLocation(nameList)
    location match {
      case Some(specLoc) => 
        for { 
          file <- getFile(specLoc)
          a <- if (!a.inputFileSet.contains(file) && !a.dependencyFileSet.contains(file))
               addDependency(specLoc, file)
               else Right(a)
        } yield a
      case None => Right(a)
    }
  }

}
