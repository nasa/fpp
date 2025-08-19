package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

/** Map uses to locations */
object MapUsesToLocs extends UseAnalyzer {

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

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) = {
    def nameOpt(e: Ast.Expr, qualifier: List[Name.Unqualified]): Result.Result[Option[Name.Qualified]] = {
      e match {
        case Ast.ExprIdent(id) => {
          val list = id :: qualifier
          val use = Name.Qualified.fromIdentList(list)
          Right(Some(use))
        }
        case Ast.ExprDot(e1, id) => nameOpt(e1.data, id.data :: qualifier)
        case Ast.ExprStruct(_) => Right(None)
        case _ => Left(SemanticError.InvalidExpression(
          Locations.get(node.id),
          "expression does not refer to a definition or struct literal"
        ))
      }
    }

    nameOpt(e, Nil) match {
      // Assume the entire qualified identifier is a use
      case Right(Some(use)) => constantUse(a, node, use)
      // This is some other type of dot expression (not qual ident), evaluate the left side
      case Right(None) => exprNode(a, e.e)
      case Left(err) => Left(err)
    }
  }

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
          case specLoc @ Some(_) => specLoc
          case None => findLocation(tail)
        }
      }
    }
    def getFile(specLoc: Ast.SpecLoc): File = {
      val loc = Locations.get(specLoc.file.id)
      val path = loc.getRelativePath(specLoc.file.data)
      File.Path(path)
    }
    def addDependency(specLoc: Ast.SpecLoc, file: File): Result = {
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
    val nameList = computeNameList
    val location = findLocation(nameList)
    location match {
      case Some(specLoc) => 
        val file = getFile(specLoc)
        if (!a.inputFileSet.contains(file) && !a.dependencyFileSet.contains(file))
          addDependency(specLoc, file)
        else Right(a)
      case None => Right(a)
    }
  }

}
