package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP component instance */
final case class ComponentInstance(
  aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]],
  qualifiedName: Name.Qualified,
  component: Component,
  baseId: Int,
  maxId: Int,
  file: Option[String],
  queueSize: Option[Int],
  stackSize: Option[Int],
  priority: Option[Int],
  cpu: Option[Int],
  initSpecifierMap: Map[Int, InitSpecifier] = Map()
) extends Ordered[ComponentInstance] {

  override def toString = qualifiedName.toString

  /** Adds an init specifier */
  def addInitSpecifier(initSpecifier: InitSpecifier):
  Result.Result[ComponentInstance] = {
    val phase = initSpecifier.phase
    initSpecifierMap.get(initSpecifier.phase) match {
      case Some(prevSpec) =>
        val loc = initSpecifier.getLoc
        val prevLoc = prevSpec.getLoc
        Left(SemanticError.DuplicateInitSpecifier(phase, loc, prevLoc))
      case None =>
        val map = initSpecifierMap + (phase -> initSpecifier)
        Right(this.copy(initSpecifierMap = map))
    }
  }

  /** Gets the unqualified name of the component instance */
  def getUnqualifiedName = aNode._2.data.name

  /** Gets the location of the component instance */
  def getLoc: Location = Locations.get(aNode._2.id)

  override def compare(that: ComponentInstance) =
    this.qualifiedName.toString.compare(that.qualifiedName.toString)

}

object ComponentInstance {

  /** Creates a component instance from a component instance definition */
  def fromDefComponentInstance(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ): Result.Result[ComponentInstance] = {
    val node = aNode._2
    val data = node.data
    for {
      component <- a.getComponent(data.component.id)
      componentKind <- Right(component.aNode._2.data.kind)
      baseId <- a.getNonnegativeIntValue(data.baseId.id)
      file <- Right(data.file.map(getFile))
      queueSize <- getQueueSize(
        a,
        data.name,
        componentKind,
        data.queueSize
      )
      stackSize <- getActiveAttribute(
        data.name,
        componentKind
      )(
        "stack size",
        a.getNonnegativeIntValueOpt,
        data.stackSize
      )
      priority <- getActiveAttribute(
        data.name,
        componentKind,
      )(
        "priority",
        a.getIntValueOpt,
        data.priority
      )
      cpu <- getActiveAttribute(
        data.name,
        componentKind,
      )(
        "CPU affinity",
        a.getIntValueOpt,
        data.cpu
      )
    }
    yield {
      val maxId = baseId + component.getMaxId
      val symbol = Symbol.ComponentInstance(aNode)
      val qualifiedName = a.getQualifiedName(symbol)
      ComponentInstance(
        aNode,
        qualifiedName,
        component,
        baseId,
        maxId,
        file,
        queueSize,
        stackSize,
        priority,
        cpu
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
  private def getFile(node: AstNode[String]): String = {
    val loc = Locations.get(node.id)
    val javaPath = loc.getRelativePath(node.data)
    File.Path(javaPath).toString
  }

  /** Gets the queue size */
  private def getQueueSize(
    a: Analysis,
    name: String,
    componentKind: Ast.ComponentKind,
    nodeOpt: Option[AstNode[Ast.Expr]]
  ): Result.Result[Option[Int]] = {
    (componentKind, nodeOpt) match {
      case (Ast.ComponentKind.Passive, Some(node)) => invalid(
        name,
        Locations.get(node.id),
        "passive component may not have queue size"
      )
      case (_, Some(_)) => a.getNonnegativeIntValueOpt(nodeOpt)
      case (_, None) =>
        Right(None)
    }
  }

  /** Get an attribute for an active component */
  private def getActiveAttribute(
    name: String,
    componentKind: Ast.ComponentKind
  )
  (
    kind: String,
    getValue: Option[AstNode[Ast.Expr]] => Result.Result[Option[Int]],
    nodeOpt: Option[AstNode[Ast.Expr]]
  ): Result.Result[Option[Int]] =
    (componentKind, nodeOpt) match {
      case (Ast.ComponentKind.Active, Some(_)) => getValue(nodeOpt)
      case (_, Some(node)) => invalid(
        name,
        Locations.get(node.id),
        s"$componentKind component may not have $kind"
      )
      case (_, None) => Right(None)
    }

}
