package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Expand any template expansion specifiers that have not yet been expanded */
object ExpandTemplates extends AstTransformer
{

  type State = Analysis

  type In = State

  type Out = Unit

  def default(a: Analysis) =
    throw InternalError("ExpandTemplates: Transformer not implemented")

  /** Transform a list in sequence, threading state */
  def transformList[A, B](
    s: State,
    list: List[A],
    transform: (State, A) => Result[B]
  ): Result.Result[List[B]] = {
    def helper(in: List[A], out: List[B]): Result.Result[List[B]] = {
      in match {
        case Nil => Right(out)
        case head :: tail => transform(s, head) match {
          case Left(e) => Left(e)
          case Right((_, list)) => helper(tail, list :: out)
        }
      }
    }
    helper(list, Nil).map(_.reverse)
  }

  /** Transform a list and flatten the result */
  def transformFlatList[A, B](
    s: State,
    list: List[A],
    transform: (State, A) => Result[List[B]]
  ): Result.Result[List[B]] = transformList(s, list, transform).map(_.flatten)

  override def transUnit(
    a: Analysis,
    tu: Ast.TransUnit
  ) = {
    for (members <- transformFlatList(a, tu.members, matchModuleMember))
      yield ((), Ast.TransUnit(members))
  }

  // Duplicate every node inside a template definition
  override def defaultNode[T](
    a: Analysis,
    node: AstNode[T]
  ) =
    a.templateExpansionId match {
      case None => Right((), node)
      case Some(expansionNode) => {
        val out = AstNode.create(node.data)
        val inLoc = Locations.get(node.id)
        Locations.put(
          out.id,
          Location(
            inLoc.file,
            inLoc.pos,
            inLoc.includingLoc,
            Some(Locations.get(expansionNode))
          )
        )
        Right((), out)
      }
    }

  override def defaultAnnotatedNode[T](
    a: In,
    aNode: Ast.Annotated[AstNode[T]]
  ) = {
    a.templateExpansionId match {
      // We are not currently in a template expansion.
      // Leave the node alone.
      case None => Right((), aNode)
      case Some(_) =>
        val (pre, node, post) = aNode
        for ((_, inter) <- defaultNode(a, node))
          yield ((), (pre, inter, post))
    }
  }

  override def exprArrayNode(
    a: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprArray
  ) =
    for (result <- transformList(a, e.elts, matchExprNode))
      yield ((), cloneNode(a, node, Ast.ExprArray(result)))

  override def exprArraySubscriptNode(
    a: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprArraySubscript
  ) =
    for {
      (_, e1) <- matchExprNode(a, e.e1)
      (_, e2) <- matchExprNode(a, e.e2)
    }
    yield ((), cloneNode(a, node, Ast.ExprArraySubscript(e1, e2)))

  override def exprBinopNode(
    a: In, node: AstNode[Ast.Expr],
    e: Ast.ExprBinop
  ) =
    for {
      (_, e1) <- matchExprNode(a, e.e1)
      (_, e2) <- matchExprNode(a, e.e2)
    }
    yield ((), cloneNode(a, node, Ast.ExprBinop(e1, e.op, e2)))

  override def exprDotNode(
    a: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprDot
  ) = 
    for ((_, left) <- matchExprNode(a, e.e))
      yield ((), cloneNode(a, node, Ast.ExprDot(left, cloneNode(a, e.id, e.id.data))))

  override def exprParenNode(
    a: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprParen
  ) =
    for ((_, value) <- matchExprNode(a, e.e))
      yield ((), cloneNode(a, node, Ast.ExprParen(value)))

  override def exprStructNode(
    a: Analysis,
    node: AstNode[Ast.Expr],
    e: Ast.ExprStruct
  ) =
    for (result <- transformList(a, e.members, resultAdapter(structMember)))
      yield ((), cloneNode(a, node, Ast.ExprStruct(result)))

  override def exprUnopNode(
    a: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprUnop
  ) =
    for ((_, ec) <- matchExprNode(a, e.e))
      yield ((), cloneNode(a, node, Ast.ExprUnop(e.op, ec)))

  override def exprSizeOfNode(
    a: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprSizeOf
  ) =
    for ((_, result) <- matchTypeName(a, e.typeName))
      yield ((), cloneNode(a, node, Ast.ExprSizeOf(result)))

  override def typeNameQualIdentNode(
    a: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameQualIdent
  ) =
    Right(
      (),
      cloneNode(a, node, Ast.TypeNameQualIdent(cloneQualIdentNode(a, tn.name)))
    )

  override def typeNameStringNode(
    a: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameString
  ) =
    for {
      valueOpt <- tn.size match {
        case None => Right(None)
        case Some(size) => matchExprNode(a, size).map(_._2).map(Some(_))
      }
    }
    yield ((), cloneNode(a, node, Ast.TypeNameString(valueOpt)))

  override def defAliasTypeAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ): ResultAnnotatedNode[Ast.DefAliasType] =
    val data = aNode._2.data
    for ((_, ty) <- matchTypeName(a, data.typeName))
      yield (
        (),
        cloneAnnotatedNode(a, aNode, data.copy(typeName = ty))
      )

  override def defArrayAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val data = aNode._2.data
    for {
      (_, size) <- matchExprNode(a, data.size)
      (_, eltType) <- matchTypeName(a, data.eltType)
      default <- exprNodeOpt(a, data.default)
    } yield {
      val dataCopy =  data.copy(
        size = size,
        eltType = eltType,
        default = default,
        format = cloneStringNodeOpt(a, data.format)
      )
      val node = cloneAnnotatedNode(a, aNode, dataCopy)
      ((), node)
    }
  }

  override def defComponentAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val data = aNode._2.data
    for (members <- transformFlatList(a, data.members, matchComponentMember))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(members = members)))
  }

  override def defComponentInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = {
    val data = aNode._2.data
    for {
      component <- Right(cloneQualIdentNode(a, data.component))
      (_, baseId) <- matchExprNode(a, data.baseId)
      queueSize <- exprNodeOpt(a, data.queueSize)
      stackSize <- exprNodeOpt(a, data.stackSize)
      priority <- exprNodeOpt(a, data.priority)
      cpu <- exprNodeOpt(a, data.cpu)
      initSpecs <- transformList(a, data.initSpecs, specInitAnnotatedNode)
    } yield (
      (),
      cloneAnnotatedNode(
        a, aNode,
        data.copy(
          component=component,
          baseId=baseId,
          implType=cloneStringNodeOpt(a, data.implType),
          file=cloneStringNodeOpt(a, data.file),
          queueSize=queueSize,
          stackSize=stackSize,
          priority=priority,
          cpu=cpu,
          initSpecs=initSpecs
        )
      )
    )
  }

  override def defInterfaceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefInterface]]
  ) = {
    val data = aNode._2.data
    for (result <- transformList(a, data.members, matchInterfaceMember))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(members=result)))
  }

  override def defConstantAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) =
    val data = aNode._2.data
    for ((_, value) <- matchExprNode(a, data.value))
      yield (
        (),
        cloneAnnotatedNode(
          a, aNode,
          data.copy(value = value)
        )
      )

  override def defEnumAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) =
    val data = aNode._2.data
    for {
      typeName <- typeNameNodeOpt(a, data.typeName)
      constants <- transformList(
        a,
        data.constants,
        resultAdapter(defEnumConstantAnnotatedNode)
      )
      default <- exprNodeOpt(a, data.default)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          typeName = typeName,
          constants = constants,
          default = default
        )
      )
    )

  override def defPortAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      returnType <- typeNameNodeOpt(a, data.returnType)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(params = params, returnType = returnType)
      )
    )

  override def defStateMachineAnnotatedNodeExternal(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) =
    defaultAnnotatedNode(in, node)

  override def defStateMachineAnnotatedNodeInternal(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) =
    val data = aNode._2.data
    for (members <- transformFlatList(a, members, matchStateMachineMember))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(members = Some(members))))

  override def defStateMachineAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) =
    val data = aNode._2.data
    data.members match {
      case None => defStateMachineAnnotatedNodeExternal(a, aNode)
      case Some(members) => defStateMachineAnnotatedNodeInternal(a, aNode, members)
    }

  override def defActionAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefAction]]
  ) =
    val data = aNode._2.data
    for {
      typeName <- typeNameNodeOpt(a, data.typeName)
    } yield ((), cloneAnnotatedNode(a, aNode, data.copy(typeName = typeName)))

  override def defChoiceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ) =
    val data = aNode._2.data
    for {
      (_, guard) <- defaultNode(a, data.guard)
      ifTransition <- transitionExpr(a, data.ifTransition)
      elseTransition <- transitionExpr(a, data.elseTransition)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          guard = guard,
          ifTransition = ifTransition,
          elseTransition = elseTransition
        )
      )
    )

  override def defGuardAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefGuard]]
  ) =
    val data = aNode._2.data
    for (typeName <- typeNameNodeOpt(a, data.typeName))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(typeName = typeName)))

  override def defSignalAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefSignal]]
  ) =
    val data = aNode._2.data
    for (typeName <- typeNameNodeOpt(a, data.typeName))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(typeName = typeName)))

  override def defStateAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) =
    val data = aNode._2.data
    for (members <- transformFlatList(a, data.members, matchStateMember))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(members = members)))

  override def specInitialTransitionAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) =
    val data = aNode._2.data
    for (transition <- transitionExpr(a, data.transition))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(transition = transition)))

  override def specStateEntryAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateEntry]]
  ) =
    val data = aNode._2.data
    for (actions <- transformList(a, data.actions, defaultNode))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(actions = actions)))

  override def specStateExitAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateExit]]
  ) =
    val data = aNode._2.data
    for (actions <- transformList(a, data.actions, defaultNode))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(actions = actions)))

  override def specStateTransitionAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) =
    val data = aNode._2.data
    for {
      (_, signal) <- defaultNode(a, data.signal)
      guard <- defaultNodeOpt(a, data.guard)
      transitionOrDo <- transitionOrDo(a, data.transitionOrDo)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          signal = signal,
          guard = guard,
          transitionOrDo = transitionOrDo
        )
      )
    )

  override def defStructAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) =
    val data = aNode._2.data
    for {
      members <- transformList(a, data.members, resultAdapter(structTypeMemberAnnotatedNode))
      default <- exprNodeOpt(a, data.default)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(members = members, default = default)
      )
    )

  override def defTopologyAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) =
    val data = aNode._2.data
    for {
      members <- transformFlatList(a, data.members, matchTopologyMember)
      implements <- transformList(a, data.implements, (a, q) => Right(((), cloneQualIdentNode(a, q))))
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(members = members, implements = implements)
      )
    )

  override def defSystemAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
  ) =
    val data = aNode._2.data
    for (topology <- qualIdentNode(a, data.topology))
      yield (
        (),
        cloneAnnotatedNode(
          a,
          aNode,
          data.copy(topology = topology)
        )
      )

  override def specCommandAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ) =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      opcode <- exprNodeOpt(a, data.opcode)
      priority <- exprNodeOpt(a, data.priority)
      queueFull <- defaultNodeOpt(a, data.queueFull)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          params = params,
          opcode = opcode,
          priority = priority,
          queueFull = queueFull,
        )
      )
    )

  override def specInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInstance]]
  ) =
    val data = aNode._2.data
    for {
      instance <- qualIdentNode(a, data.instance)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(instance = instance)
      )
    )

  override def specConnectionGraphAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]
  ) =
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
        val connectionCopy = connection.copy(
          fromPort = fromPort,
          fromIndex = fromIndex,
          toPort = toPort,
          toIndex = toIndex
        )
        ((), connectionCopy)
      }
    }

    data match {
      case data @ Ast.SpecConnectionGraph.Direct(_, connections) =>
        for (connections <- transformList(a, connections, connection))
          yield {
            val dataCopy = data.copy(connections = connections)
            val node = cloneAnnotatedNode(a, aNode, dataCopy)
            ((), node)
          }
      case data @ Ast.SpecConnectionGraph.Pattern(_, source, targets) =>
        for {
          source <- qualIdentNode(a, source)
          targets <- transformList(a, targets, resultAdapter(qualIdentNode))
        } yield {
          val dataCopy = data.copy(source = source, targets = targets)
          val node = cloneAnnotatedNode(a, aNode, dataCopy)
          ((), node)
        }
    }


  override def specContainerAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecContainer]]
  ) =
    val data = aNode._2.data
    for {
      id <- exprNodeOpt(a, data.id)
      defaultPriority <- exprNodeOpt(a, data.defaultPriority)
    } yield {
      val dataCopy = data.copy(id = id, defaultPriority = defaultPriority)
      val node = cloneAnnotatedNode(a, aNode, dataCopy)
      ((), node)
    }

  override def specEventAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ) =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      id <- exprNodeOpt(a, data.id)
      (_, format) <- defaultNode(a, data.format)
      throttle <- data.throttle match {
        case None => Right(None)
        case Some(throttle) =>
          for {
            (_, count) <- matchExprNode(a, throttle.data.count)
            every <- exprNodeOpt(a, throttle.data.every)
          } yield Some(
            cloneNode(
              a,
              throttle,
              throttle.data.copy(every = every, count = count)
            )
          )
      }
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          params = params,
          id = id,
          format = format,
          throttle = throttle
        )
      )
    )

  override def specIncludeAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInclude]]
  ) =
    val data = aNode._2.data
    for {
      (_, file) <- defaultNode(a, data.file)
    } yield ((), cloneAnnotatedNode(a, aNode, data.copy(file = file)))

  override def specInitAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ) =
    val data = aNode._2.data
    for ((_, phase) <- matchExprNode(a, data.phase))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(phase=phase)))

  override def specInternalPortAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]
  ) =
    val data = aNode._2.data
    for {
      params <- formalParamList(a, data.params)
      priority <- exprNodeOpt(a, data.priority)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(params = params, priority = priority)
      )
    )

  override def specLocAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) =
    val data = aNode._2.data
    for {
      symbol <- qualIdentNode(a, data.symbol)
      (_, file) <- defaultNode(a, data.file)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(symbol = symbol, file = file)
      )
    )

  override def specParamAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecParam]]
  ) =
    val data = aNode._2.data
    for {
      (_, typeName) <- matchTypeName(a, data.typeName)
      default <- exprNodeOpt(a, data.default)
      id <- exprNodeOpt(a, data.id)
      setOpcode <- exprNodeOpt(a, data.setOpcode)
      saveOpcode <- exprNodeOpt(a, data.saveOpcode)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          typeName = typeName,
          default = default,
          id = id,
          setOpcode = setOpcode,
          saveOpcode = saveOpcode
        )
      )
    )

  override def specPortInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) =
    val data = aNode._2.data
    data match {
      case data @ Ast.SpecPortInstance.General(_, _, _, _, _, _) =>
        for {
          size <- exprNodeOpt(a, data.size)
          port <- qualIdentNodeOpt(a, data.port)
          priority <- exprNodeOpt(a, data.priority)
          queueFull <- defaultNodeOpt(a, data.queueFull)
        } yield (
          (),
          cloneAnnotatedNode(
            a,
            aNode,
            data.copy(
              size = size,
              port = port,
              priority = priority,
              queueFull = queueFull
            )
          )
        )
      case data @ Ast.SpecPortInstance.Special(_, _, _, _, _) =>
        for {
          priority <- exprNodeOpt(a, data.priority)
          queueFull <- defaultNodeOpt(a, data.queueFull)
        } yield {
          (
            (),
            cloneAnnotatedNode(
              a,
              aNode,
              data.copy(
                priority = priority,
                queueFull = queueFull
              )
            )
          )
        }
    }

  override def specPortMatchingAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortMatching]]
  ) =
    val data = aNode._2.data
    for {
      (_, port1) <- defaultNode(a, data.port1)
      (_, port2) <- defaultNode(a, data.port2)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(port1 = port1, port2 = port2)
      )
    )

  override def specRecordAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecRecord]]
  ) =
    val data = aNode._2.data
    for {
      (_, recordType) <- matchTypeName(a, data.recordType)
      id <- exprNodeOpt(a, data.id)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(recordType = recordType, id = id)
      )
    )

  override def specStateMachineInstanceAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ) =
    val data = aNode._2.data
    for {
      stateMachine <- qualIdentNode(a, data.stateMachine)
      priority <- exprNodeOpt(a, data.priority)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(stateMachine = stateMachine, priority = priority)
      )
    )

  override def specTlmChannelAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ) =
    val data = aNode._2.data
    def limit(
      a: In,
      limit: Ast.SpecTlmChannel.Limit
    ): Result[Ast.SpecTlmChannel.Limit] = {
      val (kind, value) = limit
      for {
        (_, kind) <- defaultNode(a, kind)
        (_, value) <- matchExprNode(a, value)
      } yield ((), (kind, value))
    }

    for {
      (_, typeName) <- matchTypeName(a, data.typeName)
      id <- exprNodeOpt(a, data.id)
      low <- transformList(a, data.low, limit)
      high <- transformList(a, data.high, limit)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          typeName = typeName,
          id = id,
          format = cloneStringNodeOpt(a, data.format),
          low = low,
          high = high
        )
      )
    )

  override def specTlmPacketAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]
  ) =
    val data = aNode._2.data
    def tlmPacketMember(
      a: In,
      member: Ast.TlmPacketMember
    ): Result[Ast.TlmPacketMember] = {
      member match {
        case Ast.TlmPacketMember.SpecInclude(node) =>
          for ((_, file) <- defaultNode(a, node.data.file))
            yield (
              (),
              Ast.TlmPacketMember.SpecInclude(
                cloneNode(a, node, node.data.copy(file = file))
              )
            )
        case Ast.TlmPacketMember.TlmChannelIdentifier(node) =>
          for {
            componentInstance <- qualIdentNode(a, node.data.componentInstance)
            (_, channelName) <- defaultNode(a, node.data.channelName)
          } yield (
            (),
            Ast.TlmPacketMember.TlmChannelIdentifier(
              cloneNode(
                a,
                node,
                node.data.copy(
                  componentInstance = componentInstance,
                  channelName = channelName
                )
              )
            )
          )
      }
    }

    for {
      id <- exprNodeOpt(a, data.id)
      (_, group) <- matchExprNode(a, data.group)
      members <- transformList(a, data.members, tlmPacketMember)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          id = id,
          group = group,
          members = members
        )
      )
    )

  override def specTopPortAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTopPort]]
  ) =
    val data = aNode._2.data
    for (underlyingPort <- portInstanceIdentifierNode(a, data.underlyingPort))
      yield (
        (),
        cloneAnnotatedNode(
          a,
          aNode,
          data.copy(underlyingPort = underlyingPort)
        )
      )

  override def specTlmPacketSetAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ) =
    val data = aNode._2.data
    def tlmChannelIdentifier(
      a: In,
      node: AstNode[Ast.TlmChannelIdentifier]
    ): ResultNode[Ast.TlmChannelIdentifier] =
      val data = node.data
      for {
        componentInstance <- qualIdentNode(a, data.componentInstance)
        (_, channelName) <- defaultNode(a, data.channelName)
      } yield (
        (),
        cloneNode(
          a,
          node,
          data.copy(
            componentInstance = componentInstance,
            channelName = channelName
          )
        )
      )

    for {
      members <- transformFlatList(a, data.members, matchTlmPacketSetMember)
      omitted <- transformList(a, data.omitted, tlmChannelIdentifier)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(members = members, omitted = omitted)
      )
    )

  override def specInterfaceImportAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecImport]]
  ) =
    val data = aNode._2.data
    for {
      sym <- qualIdentNode(a, data.sym)
    } yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(sym = sym)
      )
    )

  override def defModuleTemplateAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) =
    val (_, node, _) = aNode
    val data = node.data

    def templateParam(
      a: In,
      param: Ast.Annotated[AstNode[Ast.TemplateParam]]
    ): ResultAnnotatedNode[Ast.TemplateParam] = {
      val (_, node, _) = param
      for {
        data <- node.data match {
          case data @ Ast.TemplateParam.Constant(_, tn) =>
            for ((_, tn) <- matchTypeName(a, tn))
              yield data.copy(typeName = tn)
          case data @ Ast.TemplateParam.Type(_) => Right(data)
          case data @ Ast.TemplateParam.Interface(_, interface) =>
            for (interface <- qualIdentNode(a, interface))
              yield data.copy(interface = interface)
        }
      } yield ((), cloneAnnotatedNode(a, param, data))
    }

    for {
      a <- Right(a.copy(templateDefId = Some(node.id)))
      params <- transformList(a, data.params, templateParam)
      members <- transformFlatList(a, data.members, matchModuleMember)
      a <- Right(a.copy(templateDefId = None))
    }
    yield (
      (),
      cloneAnnotatedNode(
        a,
        aNode,
        data.copy(params = params, members = members)
      )
    )

  override def defModuleAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for (members <- transformFlatList(a, data.members, matchModuleMember))
      yield ((), cloneAnnotatedNode(a, aNode, data.copy(members = members)))
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    data.members match {
      // Members are already there; nothing to do
      case Some(members) => Right((), aNode) 
      // Members are not there; expand
      case None => (resultAdapter(expandTemplate))(a, aNode)
    }
  }

  private inline def cloneNode[T](
    a: Analysis,
    node: AstNode[T],
    data: T
  ) =
    a.templateExpansionId match {
      case None => AstNode.create(data, node.id)
      case Some(expansionNode) => {
        val out = AstNode.create(data)
        val inLoc = Locations.get(node.id)
        Locations.put(
          out.id,
          Location(
            inLoc.file,
            inLoc.pos,
            inLoc.includingLoc,
            Some(Locations.get(expansionNode))
          )
        )
        out
      }
    }

  private inline def cloneAnnotatedNode[T](
    a: Analysis,
    aNode: Ast.Annotated[AstNode[T]],
    data: T
  ) = {
    val (pre, node, post) = aNode
    (pre, cloneNode(a, node, data), post)
  }

  private def cloneQualIdentNode(
    a: In,
    node: AstNode[Ast.QualIdent]
  ): AstNode[Ast.QualIdent] = {
    node.data match {
      case Ast.QualIdent.Qualified(qualifier, name) =>
        cloneNode(
          a,
          node,
          Ast.QualIdent.Qualified(
            cloneQualIdentNode(a, qualifier),
            cloneNode(a, name, name.data)
          )
        )
      case Ast.QualIdent.Unqualified(name, isAbsolute) =>
        cloneNode(a, node, Ast.QualIdent.Unqualified(name, isAbsolute))
    }
  }

  private def cloneStringNodeOpt(
    a: In,
    node: Option[AstNode[String]]
  ): Option[AstNode[String]] =
    node match {
      case None => None
      case Some(value) => Some(cloneNode(a, value, value.data))
    }

  private def defaultNodeOpt[T](
    a: Analysis,
    node: Option[AstNode[T]]
  ): Result.Result[Option[AstNode[T]]] =
    node match {
      case None => Right(None)
      case Some(node) =>
        for ((_, node) <- defaultNode(a, node))
          yield Some(node)
    }

  private def defEnumConstantAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]
  ): Result.Result[Ast.Annotated[AstNode[Ast.DefEnumConstant]]] =
    val data = aNode._2.data
    for (value <- exprNodeOpt(a, data.value))
      yield cloneAnnotatedNode(a, aNode, data.copy(value = value))

  /** Expand a template */
  private def expandTemplate(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ): Result.Result[Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]] = {
    val (pre, node, post) = aNode
    val data = node.data
    for {
      // Look up the template def
      tmpl <- a.getTemplateSymbol(data.template.id)
      // Clone the template body, recording the expansion node so that 
      // cloned nodes point back at the expansion site.
      // Record the template definition too for diagnostics.
      members <- transformFlatList(
        a.copy(
          templateExpansionId = Some(node.id),
          templateDefId = Some(tmpl.node._2.id)
        ),
        tmpl.node._2.data.members,
        matchModuleMember
      )
    } yield {
      // Paste the expanded template body into the expansion specifier
      (pre, AstNode.create(data.copy(members = Some(members)), node.id), post)
    }
  }

  private def exprNodeOpt(
    a: In,
    node: Option[AstNode[Ast.Expr]]
  ): Result.Result[Option[AstNode[Ast.Expr]]] =
    node match {
      case None => Right(None)
      case Some(value) =>
        for ((_, result) <- matchExprNode(a, value))
          yield Some(result)
    }

  private def formalAnnotatatedParam(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.FormalParam]]
  ) =
    val data = aNode._2.data
    for ((_, ty) <- matchTypeName(a, data.typeName))
      yield cloneAnnotatedNode(a, aNode, data.copy(typeName = ty))

  private def formalParamList(
    a: In,
    aNode: Ast.FormalParamList
  ) = for (node <- transformList(a, aNode, resultAdapter(formalAnnotatatedParam)))
        yield node

  private def portInstanceIdentifierNode(
    a: In,
    pii: AstNode[Ast.PortInstanceIdentifier]
  ): Result.Result[AstNode[Ast.PortInstanceIdentifier]] = {
    for {
      interfaceInstance <- qualIdentNode(a, pii.data.interfaceInstance)
      (_, portName) <- defaultNode(a, pii.data.portName)
    } yield cloneNode(
      a,
      pii,
      pii.data.copy(
        interfaceInstance = interfaceInstance,
        portName = portName
      )
    )
  }

  private def qualIdentNode(
    a: In,
    node: AstNode[Ast.QualIdent]
  ): Result.Result[AstNode[Ast.QualIdent]] =
    Right(cloneQualIdentNode(a, node))

  private def qualIdentNodeOpt(
    a: In,
    qi: Option[AstNode[Ast.QualIdent]]
  ): Result.Result[Option[AstNode[Ast.QualIdent]]] = {
    qi match {
      case None => Right(None)
      case Some(value) =>
        for (result <- qualIdentNode(a, value))
          yield Some(result)
    }
  }

  /** Adapt a simple result function to the form required by the visitor */
  private def resultAdapter[A,B](f: (A, B) => Result.Result[B]) =
    (a: A, b: B) => for (c <- f(a, b)) yield ((), c)

  private def structMember(
    a: Analysis,
    node: AstNode[Ast.StructMember]
  ) = {
    for ((_, value) <- matchExprNode(a, node.data.value))
      yield cloneNode(
        a,
        node,
        Ast.StructMember(node.data.name, value)
      )
  }

  private def structTypeMemberAnnotatedNode(
    a: In,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ) =
    val data = aNode._2.data
    for {
      size <- exprNodeOpt(a, data.size)
      (_, typeName) <- matchTypeName(a, data.typeName)
    } yield cloneAnnotatedNode(
        a,
        aNode,
        data.copy(
          size = size,
          typeName = typeName,
          format = cloneStringNodeOpt(a, data.format)
        )
    )

  private def transitionExpr(
    a: In,
    node: AstNode[Ast.TransitionExpr]
  ): Result.Result[AstNode[Ast.TransitionExpr]] =
    for {
      actions <- transformList(a, node.data.actions, defaultNode)
      target <- qualIdentNode(a, node.data.target)
    } yield cloneNode(
      a,
      node,
      node.data.copy(actions = actions, target = target)
    )

  private def transitionOrDo(
    a: In,
    transitionOrDo: Ast.TransitionOrDo
  ) =
    transitionOrDo match
      case Ast.TransitionOrDo.Transition(transition) =>
        for (result <- transitionExpr(a, transition))
          yield Ast.TransitionOrDo.Transition(result)
      case Ast.TransitionOrDo.Do(actions) =>
        for (result <- transformList(a, actions, defaultNode))
          yield Ast.TransitionOrDo.Do(result)
    
  private def typeNameNodeOpt(
    a: In,
    node: Option[AstNode[Ast.TypeName]]
  ): Result.Result[Option[AstNode[Ast.TypeName]]] =
    node match {
      case None => Right(None)
      case Some(tn) =>
        for ((_, tn) <- matchTypeName(a, tn))
          yield Some(tn)
    }

}
