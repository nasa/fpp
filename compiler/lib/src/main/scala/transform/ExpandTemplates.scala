package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Expand any template expansion specifiers that have not yet been expanded */
object ExpandTemplates extends AstTransformer
{

  type State = Analysis

  type In = State

  type Out = Boolean

  def default(a: Analysis) =
    throw new InternalError("FppExpandTemplates: Transformer not implemented")

  /** Transform a list in sequence, threading state */
  def transformList[A, B](
    s: State,
    list: List[A],
    transform: (State, A) => Result[B]
  ): Result[List[B]] = {
    def helper(res: Boolean, in: List[A], out: List[B]): Result[List[B]] = {
      in match {
        case Nil => Right((res, out))
        case head :: tail => transform(s, head) match {
          case Left(e) => Left(e)
          case Right((newRes, list)) => helper(newRes || res, tail, list :: out)
        }
      }
    }
    for { pair <- helper(false, list, Nil) }
    yield (pair._1, pair._2.reverse)
  }

  override def transUnit(
    a: Analysis,
    tu: Ast.TransUnit
  ): Result[Ast.TransUnit] = {
    for {
      members <- transformList(a, tu.members, matchModuleMember)
    } yield (members._1, Ast.TransUnit(members._2))
  }

  // Duplicate every node inside a template definition
  override def defaultNode[T](
    a: Analysis,
    node: AstNode[T]
  ) =
    a.template match {
      case None => Right(false, node)
      case Some(expansionNode) => {
        val out = AstNode.create(node.data)
        val inLoc = Locations.get(node.id)
        Locations.put(out.id, Location(
          inLoc.file,
          inLoc.pos,
          Some(LocationExpanded(Locations.get(expansionNode)))
        ))
        Right(false, out)
      }
    }

  def defaultNodeOpt[T](
    a: Analysis,
    node: Option[AstNode[T]]
  ): Result[Option[AstNode[T]]] =
    node match {
      case None => Right((false, None))
      case Some(node) =>
        for (result <- defaultNode(a, node))
        yield (result._1, Some(result._2))
    }

  override def defaultAnnotatedNode[T](
    a: In,
    aNode: Ast.Annotated[AstNode[T]]
  ) = {
    a.template match {
      // We are not currently in a template, leave the node alone
      case None => Right(false, aNode)
      case Some(_) =>
        val (pre, node, post) = aNode
        for (inter <- defaultNode(a, node))
          yield (inter._1, (pre, inter._2, post))
    }
  }

  inline def cloneNode[T](
    a: Analysis,
    node: AstNode[T],
    data: T
  ) =
    a.template match {
      case None => node
      case Some(expansionNode) => {
        val out = AstNode.create(data)
        val inLoc = Locations.get(node.id)
        Locations.put(out.id, Location(
          inLoc.file,
          inLoc.pos,
          Some(LocationExpanded(Locations.get(expansionNode)))
        ))
        out
      }
    }

  inline def cloneAnnotatedNode[T](
    a: Analysis,
    aNode: Ast.Annotated[AstNode[T]],
    data: T
  ) =
    a.template match {
      case None => aNode
      case Some(expansionNode) => {
        val out = AstNode.create(data)
        val inLoc = Locations.get(aNode._2.id)
        Locations.put(out.id, Location(
          inLoc.file,
          inLoc.pos,
          Some(LocationExpanded(Locations.get(expansionNode)))
        ))
        (aNode._1, out, aNode._3)
      }
    }

  override def exprArrayNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprArray): ResultNode[Ast.Expr] =
    for { result <- transformList(a, e.elts, matchExprNode) }
    yield {
      val (a1, elts1) = result
      (a1, cloneNode(a, node, Ast.ExprArray(elts1)))
    }

  override def exprArraySubscriptNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprArraySubscript): ResultNode[Ast.Expr] =
    for {
      e1 <- matchExprNode(a, e.e1)
      e2 <- matchExprNode(a, e.e2)
    }
    yield {
      val (a1, e1_) = e1
      val (a2, e2_) = e2
      (a1 || a2, cloneNode(a, node, Ast.ExprArraySubscript(e1_, e2_)))
    }

  override def exprBinopNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprBinop): ResultNode[Ast.Expr] =
    for {
      e1 <- matchExprNode(a, e.e1)
      e2 <- matchExprNode(a, e.e2)
    }
    yield {
      val (a1, e1_) = e1
      val (a2, e2_) = e2
      (a1 || a2, cloneNode(a, node, Ast.ExprBinop(e1_, e.op, e2_)))
    }

  override def exprDotNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprDot): ResultNode[Ast.Expr] =
    for (result <- matchExprNode(a, e.e))
    yield {
      val (a1, left) = result
      (a1, cloneNode(a, node, Ast.ExprDot(left, cloneNode(a, e.id, e.id.data))))
    }

  override def exprParenNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprParen): ResultNode[Ast.Expr] =
    for (result <- matchExprNode(a, e.e))
    yield {
      val (a1, value) = result
      (a1, cloneNode(a, node, Ast.ExprParen(value)))
    }

  def structMember(
    a: Analysis,
    node: AstNode[Ast.StructMember]
  ) = {
    for (result <- matchExprNode(a, node.data.value))
    yield {
      val (a1, value) = result
      (a1, cloneNode(a, node, Ast.StructMember(
        node.data.name,
        value
      )))
    }
  }

  def cloneQualIdentNode(
    a: In,
    node: AstNode[Ast.QualIdent]
  ): AstNode[Ast.QualIdent] = {
    node.data match {
      case Ast.QualIdent.Qualified(qualifier, name) =>
        cloneNode(a, node, Ast.QualIdent.Qualified(
          cloneQualIdentNode(a, qualifier),
          cloneNode(a, name, name.data)
        ))
      case Ast.QualIdent.Unqualified(name) =>
        cloneNode(a, node, Ast.QualIdent.Unqualified(name))
    }
  }

  def qualIdentNode(
    a: In,
    node: AstNode[Ast.QualIdent]
  ): ResultNode[Ast.QualIdent] =
    Right((false, cloneQualIdentNode(a, node)))

  override def exprStructNode(
    a: Analysis,
    node: AstNode[Ast.Expr],
    e: Ast.ExprStruct
  ) =
    for { result <- transformList(a, e.members, structMember) }
    yield {
      val (a1, members1) = result
      (a1, cloneNode(a, node, Ast.ExprStruct(members1)))
    }

  override def exprUnopNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprUnop) =
    for (ec <- matchExprNode(a, e.e))
    yield (ec._1, cloneNode(a, node, Ast.ExprUnop(e.op, ec._2)))

  override def typeNameQualIdentNode(
    a: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameQualIdent
  ): ResultNode[Ast.TypeName] = {
    Right((false, cloneNode(a, node, Ast.TypeNameQualIdent(
      cloneQualIdentNode(a, tn.name)
    ))))
  }

  def exprNodeOpt(
    a: In,
    node: Option[AstNode[Ast.Expr]]
  ): Result[Option[AstNode[Ast.Expr]]] =
    node match {
      case None => Right((false, None))
      case Some(value) =>
        for (result <- matchExprNode(a, value))
        yield {
          val (a1, v1) = result
          (a1, Some(v1))
        }
    }

  override def typeNameStringNode(
    a: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameString
  ): ResultNode[Ast.TypeName] = {
    tn.size match {
      case None =>
        Right((false, cloneNode(a, node, Ast.TypeNameString(None))))
      case Some(size) =>
        for (result <- matchExprNode(a, size))
        yield {
          val (a1, value) = result
          (a1, cloneNode(a, node, Ast.TypeNameString(Some(value))))
        }
    }
  }

  def typeNameNodeOpt(
    a: In,
    node: Option[AstNode[Ast.TypeName]]
  ): Result[Option[AstNode[Ast.TypeName]]] =
    node match {
      case None => Right((false, None))
      case Some(tn) =>
        for (result <- matchTypeName(a, tn))
        yield (false, Some(result._2))
    }

  override def defAliasTypeAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ): ResultAnnotatedNode[Ast.DefAliasType] =
    val data = aNode._2.data
    for (result <- matchTypeName(a, data.typeName))
    yield {
      val (a1, ty) = result
      (a1, cloneAnnotatedNode(
        a, aNode, data.copy(typeName = ty)
      ))
    }

  def cloneStringNodeOpt(
    a: In,
    node: Option[AstNode[String]]
  ): Option[AstNode[String]] =
    node match {
      case None => None
      case Some(value) => Some(cloneNode(a, value, value.data))
    }

  override def defArrayAnnotatedNode(a: In, aNode: Ast.Annotated[AstNode[Ast.DefArray]]): ResultAnnotatedNode[Ast.DefArray] =
    val data = aNode._2.data
    for {
      size <- matchExprNode(a, data.size)
      eltType <- matchTypeName(a, data.eltType)
      default <- exprNodeOpt(a, data.default)
    } yield {
      (
        false,
        cloneAnnotatedNode(
          a, aNode,
          data.copy(
            size = size._2,
            eltType = eltType._2,
            default = default._2,
            format = cloneStringNodeOpt(a, data.format)
          )
        )
      )
    }

  override def defComponentAnnotatedNode(a: In, aNode: Ast.Annotated[AstNode[Ast.DefComponent]]): ResultAnnotatedNode[Ast.DefComponent] =
    val data = aNode._2.data
    for (result <- transformList(a, data.members, matchComponentMember))
    yield {
      val (a1, members1) = result
      (a1, cloneAnnotatedNode(a, aNode, data.copy(members = members1)))
    }

  override def defComponentInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ): ResultAnnotatedNode[Ast.DefComponentInstance] =
    val data = aNode._2.data
    for {
      component <- Right(cloneQualIdentNode(a, data.component))
      baseId <- matchExprNode(a, data.baseId)
      queueSize <- exprNodeOpt(a, data.queueSize)
      stackSize <- exprNodeOpt(a, data.stackSize)
      priority <- exprNodeOpt(a, data.priority)
      cpu <- exprNodeOpt(a, data.cpu)
      initSpecs <- transformList(a, data.initSpecs, specInitAnnotatedNode)
    } yield {
      (
        false,
        cloneAnnotatedNode(
          a, aNode,
          data.copy(
            component=component,
            baseId=baseId._2,
            implType=cloneStringNodeOpt(a, data.implType),
            file=cloneStringNodeOpt(a, data.file),
            queueSize=queueSize._2,
            stackSize=stackSize._2,
            priority=priority._2,
            cpu=cpu._2,
            initSpecs=initSpecs._2
          )
        )
      )
    }

  override def defInterfaceAnnotatedNode(a: In, aNode: Ast.Annotated[AstNode[Ast.DefInterface]]): ResultAnnotatedNode[Ast.DefInterface] =
    val data = aNode._2.data
    for (result <- transformList(a, data.members, matchInterfaceMember))
    yield {
      val (a1, members1) = result
      (a1, cloneAnnotatedNode(a, aNode, data.copy(members=members1)))
    }

  override def defConstantAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ): ResultAnnotatedNode[Ast.DefConstant] =
    val data = aNode._2.data
    for (value <- matchExprNode(a, data.value))
    yield {
      (
        false,
        cloneAnnotatedNode(
          a, aNode,
          data.copy(value = value._2)
        )
      )
    }

  def defEnumConstantAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]
  ): ResultAnnotatedNode[Ast.DefEnumConstant] =
    val data = aNode._2.data
    for (value <- exprNodeOpt(a, data.value))
    yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(value = value._2)))
    }

  override def defEnumAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ): ResultAnnotatedNode[Ast.DefEnum] =
    val data = aNode._2.data
    for {
      typeName <- typeNameNodeOpt(a, data.typeName)
      constants <- transformList(a, data.constants, defEnumConstantAnnotatedNode)
      default <- exprNodeOpt(a, data.default)
    } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
      typeName = typeName._2,
      constants = constants._2,
      default = default._2
    )))

  def formalAnnotatatedParam(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.FormalParam]]
  ) =
    val data = aNode._2.data
    for (ty <- matchTypeName(a, data.typeName))
    yield (false, cloneAnnotatedNode(a, aNode, data.copy(typeName = ty._2)))

  def formalParamList(
    a: In,
    aNode: Ast.FormalParamList
  ) = transformList(a, aNode, formalAnnotatatedParam)

  override def defPortAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ): ResultAnnotatedNode[Ast.DefPort] =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      returnType <- typeNameNodeOpt(a, data.returnType)
    } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
      params = params._2,
      returnType = returnType._2
    )))

  override def defStateMachineAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): ResultAnnotatedNode[Ast.DefStateMachine] =
    val data = aNode._2.data
    data.members match {
      case None => defaultAnnotatedNode(a, aNode)
      case Some(members) =>
        for (result <- transformList(a, members, matchStateMachineMember))
        yield (result._1, cloneAnnotatedNode(a, aNode, data.copy(members = Some(result._2))))
    }

  override def defActionAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefAction]]
  ): ResultAnnotatedNode[Ast.DefAction] =
    val data = aNode._2.data
    for {
      typeName <- typeNameNodeOpt(a, data.typeName)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(typeName = typeName._2)))
    }

  def transitionExpr(
    a: In,
    node: AstNode[Ast.TransitionExpr]
  ): ResultNode[Ast.TransitionExpr] =
    for {
      actions <- transformList(a, node.data.actions, defaultNode)
      target <- defaultNode(a, node.data.target)
    } yield (false, cloneNode(a, node, node.data.copy(
      actions = actions._2,
      target = target._2,
    )))

  override def defChoiceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ): ResultAnnotatedNode[Ast.DefChoice] =
    val data = aNode._2.data
    for {
      guard <- defaultNode(a, data.guard)
      ifTransition <- transitionExpr(a, data.ifTransition)
      elseTransition <- transitionExpr(a, data.elseTransition)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        guard = guard._2,
        ifTransition = ifTransition._2,
        elseTransition = elseTransition._2,
      )))
    }

  override def defGuardAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefGuard]]
  ): ResultAnnotatedNode[Ast.DefGuard] =
    val data = aNode._2.data
    for {
      typeName <- typeNameNodeOpt(a, data.typeName)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(typeName = typeName._2)))
    }

  override def defSignalAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefSignal]]
  ): ResultAnnotatedNode[Ast.DefSignal] =
    val data = aNode._2.data
    for {
      typeName <- typeNameNodeOpt(a, data.typeName)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(typeName = typeName._2)))
    }

  override def defStateAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ): ResultAnnotatedNode[Ast.DefState] =
    val data = aNode._2.data
    for (members <- transformList(a, data.members, matchStateMember))
    yield (false, cloneAnnotatedNode(a, aNode, data.copy(members = members._2)))

  override def specInitialTransitionAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ): ResultAnnotatedNode[Ast.SpecInitialTransition] =
    val data = aNode._2.data
    for {
      transition <- transitionExpr(a, data.transition)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(transition = transition._2)))
    }

  override def specStateEntryAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateEntry]]
  ): ResultAnnotatedNode[Ast.SpecStateEntry] =
    val data = aNode._2.data
    for {
      actions <- transformList(a, data.actions, defaultNode)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(actions = actions._2)))
    }

  override def specStateExitAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateExit]]
  ): ResultAnnotatedNode[Ast.SpecStateExit] =
    val data = aNode._2.data
    for {
      actions <- transformList(a, data.actions, defaultNode)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(actions = actions._2)))
    }

  def transitionOrDo(
    a: In,
    transitionOrDo: Ast.TransitionOrDo
  ) =
    transitionOrDo match
      case Ast.TransitionOrDo.Transition(transition) =>
        for (result <- transitionExpr(a, transition))
        yield (result._1, Ast.TransitionOrDo.Transition(result._2))
      case Ast.TransitionOrDo.Do(actions) =>
        for (result <- transformList(a, actions, defaultNode))
        yield (result._1, Ast.TransitionOrDo.Do(result._2))
    

  override def specStateTransitionAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ): ResultAnnotatedNode[Ast.SpecStateTransition] =
    val data = aNode._2.data
    for {
      signal <- defaultNode(a, data.signal)
      guard <- defaultNodeOpt(a, data.guard)
      transitionOrDo <- transitionOrDo(a, data.transitionOrDo)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        signal = signal._2,
        guard = guard._2,
        transitionOrDo = transitionOrDo._2
      )))
    }

  def structTypeMemberAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ) =
    val data = aNode._2.data
    for {
      size <- exprNodeOpt(a, data.size)
      typeName <- matchTypeName(a, data.typeName)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        size = size._2,
        typeName = typeName._2,
        format = cloneStringNodeOpt(a, data.format)
      )))
    }

  override def defStructAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ): ResultAnnotatedNode[Ast.DefStruct] =
    val data = aNode._2.data
    for {
      members <- transformList(a, data.members, structTypeMemberAnnotatedNode)
      default <- exprNodeOpt(a, data.default)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        members = members._2,
        default = default._2
      )))
    }

  override def defTopologyAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ): ResultAnnotatedNode[Ast.DefTopology] =
    val data = aNode._2.data
    for {
      members <- transformList(a, data.members, matchTopologyMember)
      implements <- transformList(a, data.implements, (a, q) => Right((false, cloneQualIdentNode(a, q))))
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        members = members._2,
        implements = implements._2
      )))
    }

  override def specCommandAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ): ResultAnnotatedNode[Ast.SpecCommand] =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      opcode <- exprNodeOpt(a, data.opcode)
      priority <- exprNodeOpt(a, data.priority)
      queueFull <- defaultNodeOpt(a, data.queueFull)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        params = params._2,
        opcode = opcode._2,
        priority = priority._2,
        queueFull = queueFull._2,
      )))
    }

  override def specInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInstance]]
  ): ResultAnnotatedNode[Ast.SpecInstance] =
    val data = aNode._2.data
    for {
      instance <- qualIdentNode(a, data.instance)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        instance = instance._2,
      )))
    }

  def portInstanceIdentifierNode(
    a: In,
    pii: AstNode[Ast.PortInstanceIdentifier]
  ): ResultNode[Ast.PortInstanceIdentifier] = {
    for {
      interfaceInstance <- qualIdentNode(a, pii.data.interfaceInstance)
      portName <- defaultNode(a, pii.data.portName)
    } yield (false, cloneNode(a, pii, pii.data.copy(
      interfaceInstance = interfaceInstance._2,
      portName = portName._2,
    )))
  }

  override def specConnectionGraphAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]
  ): ResultAnnotatedNode[Ast.SpecConnectionGraph] =
    val data = aNode._2.data

    def connection(
      a: In,
      connection: Ast.SpecConnectionGraph.Connection
    ): Result[Ast.SpecConnectionGraph.Connection] = {
      for {
        fromPort <- portInstanceIdentifierNode(a, connection.fromPort)
        fromIndex <- exprNodeOpt(a, connection.fromIndex)
        toPort <- portInstanceIdentifierNode(a, connection.toPort)
        toIndex <- exprNodeOpt(a, connection.toIndex)
      } yield {
        (false, connection.copy(
          fromPort = fromPort._2,
          fromIndex = fromIndex._2,
          toPort = toPort._2,
          toIndex = toIndex._2,
        ))
      }
    }

    data match {
      case data @ Ast.SpecConnectionGraph.Direct(_, connections) =>
        for {
          connections <- transformList(a, connections, connection)
        } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
          connections = connections._2
        )))
      case data @ Ast.SpecConnectionGraph.Pattern(_, source, targets) =>
        for {
          source <- qualIdentNode(a, source)
          targets <- transformList(a, targets, qualIdentNode)
        } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
          source = source._2,
          targets = targets._2
        )))
    }

  override def specContainerAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecContainer]]
  ): ResultAnnotatedNode[Ast.SpecContainer] =
    val data = aNode._2.data
    for {
      id <- exprNodeOpt(a, data.id)
      defaultPriority <- exprNodeOpt(a, data.defaultPriority)
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        id = id._2,
        defaultPriority = defaultPriority._2,
      )))
    }

  override def specEventAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ): ResultAnnotatedNode[Ast.SpecEvent] =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      id <- exprNodeOpt(a, data.id)
      format <- defaultNode(a, data.format)
      throttle <- {
        data.throttle match {
          case None => Right((false, None))
          case Some(throttle) =>
            for {
              count <- matchExprNode(a, throttle.data.count)
              every <- exprNodeOpt(a, throttle.data.every)
            } yield (false, Some(cloneNode(
              a, throttle, throttle.data.copy(
                every = every._2,
                count = count._2,
              )
            )))
        }
      }
    } yield {
      (false, cloneAnnotatedNode(a, aNode, data.copy(
        params = params._2,
        id = id._2,
        format = format._2,
        throttle = throttle._2,
      )))
    }

  override def specIncludeAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInclude]]
  ): ResultAnnotatedNode[Ast.SpecInclude] =
    val data = aNode._2.data
    for {
      file <- defaultNode(a, data.file)
    } yield (file._1, cloneAnnotatedNode(a, aNode, data.copy(
      file = file._2,
    )))

  override def specInitAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ): ResultAnnotatedNode[Ast.SpecInit] =
    val data = aNode._2.data
    for (result <- matchExprNode(a, data.phase))
    yield {
      val (a1, phase1) = result
      (a1, cloneAnnotatedNode(a, aNode, data.copy(phase=phase1)))
    }

  override def specInternalPortAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]
  ): ResultAnnotatedNode[Ast.SpecInternalPort] =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      priority <- exprNodeOpt(a, data.priority)
    } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
      params = params._2,
      priority = priority._2,
    )))

  override def specLocAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ): ResultAnnotatedNode[Ast.SpecLoc] =
    val data = aNode._2.data
    for {
      symbol <- qualIdentNode(a, data.symbol)
      file <- defaultNode(a, data.file)
    } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
      symbol = symbol._2,
      file = file._2,
    )))

  override def specParamAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecParam]]
  ): ResultAnnotatedNode[Ast.SpecParam] =
    val data = aNode._2.data
    for {
      typeName <- matchTypeName(a, data.typeName)
      default <- exprNodeOpt(a, data.default)
      id <- exprNodeOpt(a, data.id)
      setOpcode <- exprNodeOpt(a, data.setOpcode)
      saveOpcode <- exprNodeOpt(a, data.saveOpcode)
    } yield (false, cloneAnnotatedNode(a, aNode, data.copy(
      typeName = typeName._2,
      default = default._2,
      id = id._2,
      setOpcode = setOpcode._2,
      saveOpcode = saveOpcode._2,
    )))

  def qualIdentNodeOpt(
    a: In,
    qi: Option[AstNode[Ast.QualIdent]]
  ): Result[Option[AstNode[Ast.QualIdent]]] = {
    qi match {
      case None => Right((false, None))
      case Some(value) =>
        for (result <- qualIdentNode(a, value))
        yield ((false, Some(result._2)))
    }
  }

  override def specPortInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ): ResultAnnotatedNode[Ast.SpecPortInstance] =
    val data = aNode._2.data
    data match {
      case data @ Ast.SpecPortInstance.General(_, _, _, _, _, _) =>
        for {
          size <- exprNodeOpt(a, data.size)
          port <- qualIdentNodeOpt(a, data.port)
          priority <- exprNodeOpt(a, data.priority)
          queueFull <- defaultNodeOpt(a, data.queueFull)
        } yield {
          (false, cloneAnnotatedNode(a, aNode, data.copy(
            size = size._2,
            port = port._2,
            priority = priority._2,
            queueFull = queueFull._2
          )))
        }
      case data @ Ast.SpecPortInstance.Special(_, _, _, _, _) =>
        for {
          priority <- exprNodeOpt(a, data.priority)
          queueFull <- defaultNodeOpt(a, data.queueFull)
        } yield {
          (false, cloneAnnotatedNode(a, aNode, data.copy(
            priority = priority._2,
            queueFull = queueFull._2
          )))
        }
    }

  override def specPortMatchingAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortMatching]]
  ): ResultAnnotatedNode[Ast.SpecPortMatching] =
    val data = aNode._2.data
    for {
      port1 <- defaultNode(a, data.port1)
      port2 <- defaultNode(a, data.port2)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      port1 = port1._2,
      port2 = port2._2
    ))))

  override def specRecordAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecRecord]]
  ): ResultAnnotatedNode[Ast.SpecRecord] =
    val data = aNode._2.data
    for {
      recordType <- matchTypeName(a, data.recordType)
      id <- exprNodeOpt(a, data.id)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      recordType = recordType._2,
      id = id._2
    ))))

  override def specStateMachineInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ): ResultAnnotatedNode[Ast.SpecStateMachineInstance] =
    val data = aNode._2.data
    for {
      stateMachine <- qualIdentNode(a, data.stateMachine)
      priority <- exprNodeOpt(a, data.priority)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      stateMachine = stateMachine._2,
      priority = priority._2
    ))))

  override def specTlmChannelAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ): ResultAnnotatedNode[Ast.SpecTlmChannel] =
    val data = aNode._2.data
    def limit(
      a: In,
      limit: Ast.SpecTlmChannel.Limit
    ): Result[Ast.SpecTlmChannel.Limit] = {
      val (kind, value) = limit
      for {
        kind <- defaultNode(a, kind)
        value <- matchExprNode(a, value)
      } yield ((false, (kind._2, value._2)))
    }

    for {
      typeName <- matchTypeName(a, data.typeName)
      id <- exprNodeOpt(a, data.id)
      low <- transformList(a, data.low, limit)
      high <- transformList(a, data.high, limit)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      typeName = typeName._2,
      id = id._2,
      format = cloneStringNodeOpt(a, data.format),
      low = low._2,
      high = high._2
    ))))

  override def specTlmPacketAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]
  ): ResultAnnotatedNode[Ast.SpecTlmPacket] =
    val data = aNode._2.data
    def tlmPacketMember(
      a: In,
      member: Ast.TlmPacketMember
    ): Result[Ast.TlmPacketMember] = {
      member match {
        case Ast.TlmPacketMember.SpecInclude(node) =>
          for (file <- defaultNode(a, node.data.file))
          yield (false, Ast.TlmPacketMember.SpecInclude(
            cloneNode(a, node, node.data.copy(file = file._2))
          ))
        case Ast.TlmPacketMember.TlmChannelIdentifier(node) =>
          for {
            componentInstance <- qualIdentNode(a, node.data.componentInstance)
            channelName <- defaultNode(a, node.data.channelName)
          } yield (false, Ast.TlmPacketMember.TlmChannelIdentifier(
            cloneNode(a, node, node.data.copy(
              componentInstance = componentInstance._2,
              channelName = channelName._2
            ))
          ))
      }
    }

    for {
      id <- exprNodeOpt(a, data.id)
      group <- matchExprNode(a, data.group)
      members <- transformList(a, data.members, tlmPacketMember)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      id = id._2,
      group = group._2,
      members = members._2,
    ))))

  override def specTopPortAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTopPort]]
  ): ResultAnnotatedNode[Ast.SpecTopPort] =
    val data = aNode._2.data
    for {
      underlyingPort <- portInstanceIdentifierNode(a, data.underlyingPort)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      underlyingPort = underlyingPort._2,
    ))))

  override def specTlmPacketSetAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ): ResultAnnotatedNode[Ast.SpecTlmPacketSet] =
    val data = aNode._2.data
    def tlmChannelIdentifier(
      a: In,
      node: AstNode[Ast.TlmChannelIdentifier]
    ): ResultNode[Ast.TlmChannelIdentifier] =
      val data = node.data
      for {
        componentInstance <- qualIdentNode(a, data.componentInstance)
        channelName <- defaultNode(a, data.channelName)
      } yield (false, cloneNode(a, node, data.copy(
          componentInstance = componentInstance._2,
          channelName = channelName._2
        )))

    for {
      members <- transformList(a, data.members, matchTlmPacketSetMember)
      omitted <- transformList(a, data.omitted, tlmChannelIdentifier)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      members = members._2,
      omitted = omitted._2,
    ))))

  override def specInterfaceImportAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecImport]]
  ): ResultAnnotatedNode[Ast.SpecImport] =
    val data = aNode._2.data
    for {
      sym <- qualIdentNode(a, data.sym)
    } yield ((false, cloneAnnotatedNode(a, aNode, data.copy(
      sym = sym._2,
    ))))

  override def defModuleAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.DefModule(name, members) = node.data
    for { result <- transformList(a, members, matchModuleMember) }
    yield {
      val (a1, members1) = result
      val defModule = Ast.DefModule(name, members1)
      val node2 = AstNode.create(defModule, node.id)
      (a1, (pre, node2, post))
    }
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ): ResultAnnotatedNode[Ast.SpecTemplateExpand] = {
    val parentTemplate = a.template
    val (pre, node, post) = aNode
    val data = node.data

    // We are not quite ready to expand the template yet
    // We may _already_ be in the middle of a template expansion in which
    // case we should not be looking in the analysis for symbols since it's probably
    // out of date and we need to enter symbols again.
    // We may have already expanded this template as well in which case we should just walk
    // the child nodes to make sure what we expanded last time is fully expanded now.

    // Check if we are currently expanding a template
    a.template match {
      case None => {
        // Not currently inside a template expansion, we can expand
        for {
          // Look up the template def
          tmpl <- a.getTemplateSymbol(data.template.id)

          // Make sure attempting to expand this won't cause a cycle
          // i.e. check that we are not in the process of expanding this template
          _ <- {
            a.templateStack.find(t => tmpl == t) match {
              case Some(_) => Left(TemplateExpansionError.Cycle(
                Locations.get(node.id),
                "template expansion cycle"
              ))
              case None => Right(())
            }
          }

          members <- {
            data.members match {
              // This template expansion has already been expanded
              // We should pass over the inner nodes to make sure those are recursively expanded
              case Some(members) => transformList(
                a.copy(templateStack=tmpl :: a.templateStack),
                members,
                matchModuleMember
              )
              case None => transformList(
                a.copy(
                  template=Some(node.id),
                  templateStack=tmpl :: a.templateStack
                ),
                tmpl.node._2.data.members,
                matchModuleMember
              )
            }
          }
        } yield {
          // Paste the expanded template expansion specifier
          (members._1, (pre, AstNode.create(data.copy(members=Some(members._2)), node.id), post))
        }
      }
      case Some(value) => {
        // We are currently expanding a parent template
        // Tell the compiler to re-run this pass once it's done with this run
        Right((true, aNode))
      }
    }
  }
}
