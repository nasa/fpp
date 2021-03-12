package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component instances */
object ComponentInstances {

  /** Creates a component instance from a component instance definition */
  def fromDefComponentInstance(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ): Result.Result[ComponentInstance] = {
    val node = aNode._2
    val data = node.data
    val loc = Locations.get(node.id)
    val componentSymbol = a.useDefMap(data.component.id)
    val component = a.componentMap(componentSymbol)
    val componentKind = component.aNode._2.data.kind
    for {
      baseId <- a.getNonnegativeIntValue(data.baseId.id)
      file <- Result.mapOpt(data.file, getFile)
      queueSize <- getQueueSize(
        a,
        data.name,
        loc,
        componentKind,
        data.queueSize
      )
      stackSize <- getStackSizeOrPriority(
        a,
        data.name,
        loc,
        componentKind
      )("stack size", data.stackSize)
      priority <- getStackSizeOrPriority(
        a,
        data.name,
        loc,
        componentKind,
      )("priority", data.priority)
    }
    yield {
      val maxId = baseId + component.getMaxId
      ComponentInstance(
        aNode,
        component,
        baseId,
        maxId,
        file,
        queueSize,
        stackSize,
        priority
      )
    }
  }

  /** Construct an invalid instance error */
  private def invalid(
    name: String,
    loc: Location,
    msg: String
  ) = Left(
    SemanticError.InvalidDefComponentInstance(name, loc, msg)
  )

  /** Gets the file */
  private def getFile(node: AstNode[String]):
    Result.Result[String] = {
      val loc = Locations.get(node.id)
      for {
        javaPath <- loc.relativePath(node.data)
      }
      yield File.Path(javaPath).toString
    }
  
  /** Gets the queue size */
  private def getQueueSize(
    a: Analysis,
    name: String,
    loc: Location,
    componentKind: Ast.ComponentKind,
    nodeOpt: Option[AstNode[Ast.Expr]]
  ): Result.Result[Option[Int]] = {
    (componentKind, nodeOpt) match {
      case (Ast.ComponentKind.Passive, Some(node)) => invalid(
        name,
        Locations.get(node.id),
        "passive component may not have queue size"
      )
      case (Ast.ComponentKind.Passive, None) =>
        Right(None)
      case (_, Some(_)) => a.getNonnegativeIntValueOpt(nodeOpt)
      case _ => invalid(
        name,
        loc,
        s"$componentKind component requires queue size"
      )
    }
  }

  /** Get stack size or priority */
  private def getStackSizeOrPriority(
    a: Analysis,
    name: String,
    loc: Location,
    componentKind: Ast.ComponentKind
  )
  (kind: String, nodeOpt: Option[AstNode[Ast.Expr]]):
  Result.Result[Option[Int]] =
    (componentKind, nodeOpt) match {
      case (Ast.ComponentKind.Active, None) => invalid(
        name,
        loc,
        s"active component requires $kind"
      )
      case (Ast.ComponentKind.Active, Some(_)) =>
        a.getNonnegativeIntValueOpt(nodeOpt)
      case (_, None) => Right(None)
      case (_, Some(node)) => invalid(
        name,
        Locations.get(node.id),
        s"$componentKind component may not have $kind"
      )
    }

}
