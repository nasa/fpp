package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.language.implicitConversions

/** Write out FPP source */
object FppWriter extends AstVisitor with LineUtils {

  private case class JoinOps(ls: List[Line]) {

    def addSuffix(suffix: String) = Line.addSuffix(ls, suffix)

    def join (sep: String) (ls1: List[Line]) = Line.joinLists (Line.Indent) (ls) (sep) (ls1)

    def joinNoIndent (sep: String) (ls1: List[Line]) = Line.joinLists (Line.NoIndent) (ls) (sep) (ls1)

    def joinWithBreak[T] (sep: String) (ls1: List[Line]) =
      Line.addSuffix(ls, " \\") ++ Line.addPrefix(sep, ls1).map(indentIn)

    def joinOpt[T] (opt: Option[T]) (sep: String) (f: T => List[Line]) =
      opt match {
        case Some(t) => join (sep) (f(t))
        case None => ls
      }

    def joinOptWithBreak[T] (opt: Option[T]) (sep: String) (f: T => List[Line]) =
      opt match {
        case Some(t) => joinWithBreak (sep) (f(t))
        case None => ls
      }

  }

  private implicit def lift(ls: List[Line]) = JoinOps(ls)

  def componentMember(member: Ast.ComponentMember) = {
    val (a1, _, a2) = member.node
    val l = matchComponentMember((), member)
    annotate(a1, l, a2)
  }

  def moduleMember(member: Ast.ModuleMember) = {
    val (a1, _, a2) = member.node
    val l = matchModuleMember((), member)
    annotate(a1, l, a2)
  }

  def transUnit(tu: Ast.TransUnit): Out = transUnit((), tu)

  def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  def tuMemberList(tuml: List[Ast.TUMember]) = Line.blankSeparated (tuMember) (tuml)

  override def defAbsTypeAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node, _) = aNode
    val data = node.data
    lines(s"type ${ident(data.name)}")
  }

  override def defArrayAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"array ${ident(data.name)} = [").
      join ("") (exprNode(data.size)).
      join ("] ") (typeNameNode(data.eltType)).
      joinOpt (data.default) (" default ") (exprNode).
      joinOpt (data.format) (" format ") (applyToData(string))
  }

  override def defComponentAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefComponent]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val kind = data.kind match {
      case Ast.ComponentKind.Active => "active"
      case Ast.ComponentKind.Passive => "passive"
      case Ast.ComponentKind.Queued => "queued"
    }
    List(line(s"$kind component ${ident(data.name)} {"), Line.blank) ++
    (Line.blankSeparated (componentMember) (data.members)).map(indentIn) ++
    List(Line.blank, line("}"))
  }

  override def defConstantAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"constant ${ident(data.name)}").join (" = ") (exprNode(data.value))
  }

  override def defEnumAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"enum ${ident(data.name)}").
      joinOpt (data.typeName) (": ") (typeNameNode).
      addSuffix(" {") ++
    data.constants.flatMap(annotateNode(defEnumConstant)).map(indentIn) ++
    lines("}")
  }

  override def defModuleAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefModule]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    List(line(s"module ${ident(data.name)} {"), Line.blank) ++
    (Line.blankSeparated (moduleMember) (data.members)).map(indentIn) ++
    List(Line.blank, line("}"))
  }

  override def defPortAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"port ${ident(data.name)}").
      join ("") (formalParamList(data.params)).
      joinOpt (data.returnType) (" -> ") (typeNameNode)
  }

  override def defStructAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"struct ${ident(data.name)} {") ++
    data.members.flatMap(annotateNode(structTypeMember)).map(indentIn) ++ 
    lines("}").joinOpt (data.default) (" default ") (exprNode)
  }

  override def default(in: Unit) = Nil

  override def exprArrayNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    (line("[") :: e.elts.flatMap(exprNode).map(indentIn)) :+ line("]")

  override def exprBinopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprBinop) =
    exprNode(e.e1).join (binop(e.op)) (exprNode(e.e2))
  
  override def exprDotNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    exprNode(e.e).join (".") (lines(e.id.getData))

  override def exprIdentNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprIdent) = 
    lines(e.value)

  override def exprLiteralBoolNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) = {
    val s = e.value match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines(s)
  }

  override def exprLiteralFloatNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    lines(e.value)
  
  override def exprLiteralIntNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    lines(e.value)

  override def exprLiteralStringNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    string(e.value)

  override def exprParenNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprParen) =
    Line.addPrefixAndSuffix("(", exprNode(e.e), ")")

  override def exprStructNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    lines("{") ++
    e.members.flatMap(applyToData(structMember)).map(indentIn) ++
    lines("}")

  override def exprUnopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprUnop) =
    lines(unop(e.op)).join ("") (exprNode(e.e))

  override def specCommandAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val kind = data.kind match {
      case Ast.SpecCommand.Async => "async"
      case Ast.SpecCommand.Guarded => "guarded"
      case Ast.SpecCommand.Sync => "sync"
    }
    lines(s"$kind command ${ident(data.name)}").
      join ("") (formalParamList(data.params)).
      joinOptWithBreak (data.opcode) ("opcode ") (exprNode).
      joinOptWithBreak (data.priority) ("priority ") (exprNode).
      joinOptWithBreak (data.queueFull) ("") (applyToData(queueFull))
  }

  override def specEventAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val severity = data.severity match {
      case Ast.SpecEvent.ActivityHigh => "activity high"
      case Ast.SpecEvent.ActivityLow => "activity low"
      case Ast.SpecEvent.Command => "command"
      case Ast.SpecEvent.Diagnostic => "diagnostic"
      case Ast.SpecEvent.Fatal => "fatal"
      case Ast.SpecEvent.WarningHigh => "warning high"
      case Ast.SpecEvent.WarningLow => "warning low"
    }
    lines(s"event ${ident(data.name)}").
      join ("") (formalParamList(data.params)).
      joinWithBreak ("severity ") (lines(severity)).
      joinOptWithBreak (data.id) ("id ") (exprNode).
      joinOptWithBreak (data.format) ("format ") (applyToData(string)).
      joinOptWithBreak (data.throttle) ("throttle ") (exprNode)
  }

  override def specIncludeAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecInclude]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines("include").join (" ") (string(data.file.getData))
  }

  override def specInternalPortAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"internal port ${ident(data.name)}").
      join ("") (formalParamList(data.params)).
      joinOptWithBreak (data.priority) ("priority ") (exprNode).
      joinOptWithBreak (data.queueFull) ("") (queueFull)
  }

  override def specLocAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecLoc]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val kind = data.kind match {
      case Ast.SpecLoc.Component => "component"
      case Ast.SpecLoc.ComponentInstance => "component instance"
      case Ast.SpecLoc.Constant => "constant"
      case Ast.SpecLoc.Port => "port"
      case Ast.SpecLoc.Topology => "topology"
      case Ast.SpecLoc.Type => "type"
    }
    lines(s"locate ${kind}").
      join (" ") (qualIdent(data.symbol.getData)).
      join (" at ") (string(data.file.getData))
  }

  override def specParamAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecParam]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"param ${ident(data.name)}").
      join (": ") (typeNameNode(data.typeName)).
      joinOpt (data.default) (" default ") (exprNode).
      joinOpt (data.id) (" id ") (exprNode).
      joinOptWithBreak (data.setOpcode) ("set opcode ") (exprNode).
      joinOptWithBreak (data.saveOpcode) ("save opcode ") (exprNode)
  }

  override def specPortInstanceAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node, _) = aNode
    def general(i: Ast.SpecPortInstance.General) = {
      val kind = i.kind match {
        case Ast.SpecPortInstance.AsyncInput => "async input"
        case Ast.SpecPortInstance.GuardedInput => "guarded input"
        case Ast.SpecPortInstance.Output => "output"
        case Ast.SpecPortInstance.SyncInput => "sync input"
      }
      def brackets(en: AstNode[Ast.Expr]) = lines("").
        join ("[") (exprNode(en)).addSuffix ("]")
      def port(portOpt: Option[AstNode[Ast.QualIdent]]) =
        portOpt match {
          case Some(qidNode) => qualIdent(qidNode.data)
          case None => lines("serial")
        }
      lines(s"$kind port ${ident(i.name)}:").
        joinOpt (i.size) (" ") (brackets).
        join (" ") (port(i.port)).
        joinOptWithBreak (i.priority) ("priority ") (exprNode).
        joinOptWithBreak (i.queueFull) ("") (queueFull)
    }
    def special(i: Ast.SpecPortInstance.Special) = {
      val kind = i.kind match {
        case Ast.SpecPortInstance.CommandRecv => "command recv"
        case Ast.SpecPortInstance.CommandReg => "command reg"
        case Ast.SpecPortInstance.CommandResp => "command resp"
        case Ast.SpecPortInstance.Event => "event"
        case Ast.SpecPortInstance.ParamGet => "param get"
        case Ast.SpecPortInstance.ParamSet => "param set"
        case Ast.SpecPortInstance.Telemetry => "telemetry"
        case Ast.SpecPortInstance.TextEvent => "text event"
        case Ast.SpecPortInstance.TimeGet => "time get"
      }
      lines(s"$kind port ${ident(i.name)}")
    }
    node.getData match {
      case i : Ast.SpecPortInstance.General => general(i)
      case i : Ast.SpecPortInstance.Special => special(i)
    }
  }

  override def specTlmChannelAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    def update(u: Ast.SpecTlmChannel.Update) = {
      val s = u match {
        case Ast.SpecTlmChannel.Always => "always"
        case Ast.SpecTlmChannel.OnChange => "on change"
      }
      lines(s)
    }
    def limit(l: Ast.SpecTlmChannel.Limit) = {
      val (k, en) = l
      val ks = k match {
        case Ast.SpecTlmChannel.Red => "red"
        case Ast.SpecTlmChannel.Orange => "orange"
        case Ast.SpecTlmChannel.Yellow => "yellow"
      }
      lines(ks).join (" ") (exprNode(en))
    }
    def optList[T](l: T) = l match {
      case Nil => None
      case _ => Some(l)
    }
    def limitSeq (ls: List[Ast.SpecTlmChannel.Limit]) =
      line("{") :: (ls.flatMap(limit).map(indentIn) :+ line("}"))
    lines(s"telemetry ${ident(data.name)}").
      join (": ") (typeNameNode(data.typeName)).
      joinOpt (data.id) (" id ") (exprNode).
      joinOpt (data.update) (" update ") (update).
      joinOptWithBreak (data.format) ("format ") (applyToData(string)).
      joinOptWithBreak (optList(data.low)) ("low ") (limitSeq).
      joinOptWithBreak (optList(data.high)) ("high ") (limitSeq)
  }

  override def transUnit(in: Unit, tu: Ast.TransUnit) = tuMemberList(tu.members)

  override def typeNameBoolNode(in: Unit, node: AstNode[Ast.TypeName]) = lines("bool")

  override def typeNameFloatNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat) = {
    val s = tn.name match {
      case _: Ast.F32 => "F32"
      case _: Ast.F64 => "F64"
    }
    lines(s)
  }

  override def typeNameIntNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt) = {
    val s = tn.name match {
      case _: Ast.I8 => "I8"
      case _: Ast.I16 => "I16"
      case _: Ast.I32 => "I32"
      case _: Ast.I64 => "I64"
      case _: Ast.U8 => "U8"
      case _: Ast.U16 => "U16"
      case _: Ast.U32 => "U32"
      case _: Ast.U64 => "U64"
    }
    lines(s)
  }

  override def typeNameQualIdentNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = 
    qualIdent(tn.name.getData)

  override def typeNameStringNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameString) =
    lines("string").joinOpt (tn.size) (" size ") (exprNode)

  private def annotate(pre: List[String], lines: List[Line], post: List[String]) = {
    val pre1 = pre.map((s: String) => line("@ " ++ s))
    val post1 = post.map((s: String) => line("@< " ++ s))
    (pre1 ++ lines).join (" ") (post1)
  }

  private def annotateNode[T](f: T => List[Line]): Ast.Annotated[AstNode[T]] => List[Line] =
    (aNode: Ast.Annotated[AstNode[T]]) => {
      val (a1, node, a2) = aNode
      annotate(a1, f(node.getData), a2)
    }

  private def applyToData[A,B](f: A => B): AstNode[A] => B = 
    (a: AstNode[A]) => f(a.getData)

  private def binop(op: Ast.Binop) = op match {
    case Ast.Binop.Add => " + "
    case Ast.Binop.Div => " / "
    case Ast.Binop.Mul => " * "
    case Ast.Binop.Sub => " - "
  }

  private def defEnumConstant(dec: Ast.DefEnumConstant) =
    lines(ident(dec.name)).joinOpt (dec.value) (" = ") (exprNode)

  private def exprNode(node: AstNode[Ast.Expr]): List[Line] = matchExprNode((), node)

  private def ident(id: Ast.Ident) = if (keywords.contains(id)) "$" ++ id else id

  private def formalParam(fp: Ast.FormalParam) = {
    val prefix = fp.kind match {
      case Ast.FormalParam.Ref => "ref "
      case Ast.FormalParam.Value => ""
    }
    val name = prefix ++ ident(fp.name)
    lines(name).join (": ") (typeNameNode(fp.typeName))
  }

  private def formalParamList(fpl: Ast.FormalParamList) =
    fpl match {
      case Nil => Nil
      case _ => 
        lines("(") ++
        fpl.flatMap(annotateNode(formalParam)).map(indentIn) ++
        lines(")")
    }

  private def qualIdent(qid: Ast.QualIdent): List[Line] =
    lines(qualIdentString(qid))

  private def qualIdentString(qid: Ast.QualIdent): String =
    qid match {
      case Ast.QualIdent.Unqualified(name) => ident(name)
      case Ast.QualIdent.Qualified(qualifier, name) => 
        qualIdentString(qualifier.getData) ++ "." ++ ident(name.getData)
    }

  private def queueFull(qf: Ast.QueueFull) = {
    val s = qf match {
      case Ast.QueueFull.Assert => "assert"
      case Ast.QueueFull.Block => "block"
      case Ast.QueueFull.Drop => "drop"
    }
    lines(s)
  }

  private def string(s: String) = s.split("\n").toList match {
    case Nil => lines("\"\"")
    case s :: Nil => lines("\"" ++ s.replaceAll("\"", "\\\"") ++ "\"")
    case ss => {
      lines("\"\"\"") ++
      ss.map((s: String) => line(s.replaceAll("\"\"\"", "\\\"\"\""))) ++
      lines("\"\"\"")
    }
  }

  private def structMember(member: Ast.StructMember) =
    lines(ident(member.name)).join (" = ") (exprNode(member.value))

  private def structTypeMember(member: Ast.StructTypeMember) =
    lines(ident(member.name)).
      join (": ") (typeNameNode(member.typeName)).
      joinOpt (member.format) (" format ") (applyToData(string))


  private def typeNameNode(node: AstNode[Ast.TypeName]) = matchTypeNameNode((), node)

  private def unop(op: Ast.Unop) =
    op match {
      case Ast.Unop.Minus => "-"
    }

  type In = Unit

  type Out = List[Line]

  val keywords = Set(
    "F32",
    "F64",
    "I16",
    "I32",
    "I64",
    "I8",
    "U16",
    "U32",
    "U64",
    "U8",
    "active",
    "activity",
    "always",
    "array",
    "assert",
    "async",
    "at",
    "base",
    "block",
    "bool",
    "change",
    "command",
    "component",
    "connections",
    "constant",
    "default",
    "diagnostic",
    "drop",
    "enum",
    "event",
    "false",
    "fatal",
    "format",
    "get",
    "guarded",
    "high",
    "id",
    "import",
    "include",
    "init",
    "input",
    "instance",
    "internal",
    "locate",
    "low",
    "module",
    "on",
    "opcode",
    "orange",
    "output",
    "param",
    "passive",
    "pattern",
    "phase",
    "port",
    "priority",
    "private",
    "queue",
    "queued",
    "recv",
    "red",
    "ref",
    "reg",
    "resp",
    "save",
    "serial",
    "set",
    "severity",
    "size",
    "stack",
    "string",
    "struct",
    "sync",
    "telemetry",
    "text",
    "throttle",
    "time",
    "topology",
    "true",
    "type",
    "unused",
    "update",
    "warning",
    "yellow",
  )

}
