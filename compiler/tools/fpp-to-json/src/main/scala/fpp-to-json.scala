package fpp.compiler

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import io.circe.syntax._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import scopt.OParser

object FPPtoJson {
  case class Options(
    ast: Boolean = false,
    include: Boolean = false,
    files: List[File] = List()
  )

  def command(options: Options) = {
    fpp.compiler.util.Error.setTool(Tool(name))
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    val result = Result.seq(
      Result.map(files, Parser.parseFile (Parser.transUnit) (None) _),
      List(resolveIncludes (options) _, printAst (options) _)
    )
    result match {
      case Left(error) => {
        error.print
        System.exit(1)
      }
      case Right(_) => ()
    }
  }

  def main(args: Array[String]) = {
    val options = OParser.parse(oparser, args, Options())
    options match {
      case Some(options) => command(options)
      case None => ()
    }
  }
  

  implicit val binopEncoder: Encoder[Ast.Binop] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val unopEncoder: Encoder[Ast.Unop] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val componentKindEncoder: Encoder[Ast.ComponentKind] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val typeIntEncoder: Encoder[Ast.TypeInt] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val typeFloatEncoder: Encoder[Ast.TypeFloat] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  //implicit val typeQualEncoder: Encoder[Ast.QualIdent] = Encoder.encodeString.contramap(getUnqualifiedClassName(_)) //Needs to be refactored becuase this wont print out full list
  //implicit val specCommandEncoder: Encoder[Ast.SpecCommand] = Encoder.encodeString.contramap(getUnqualifiedClassName(_)) 
  //implicit val specEventEncoder: Encoder[Ast.SpecEvent] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val queueFullEncoder: Encoder[Ast.QueueFull] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  //implicit val formalParamEncoder: Encoder[Ast.Annotated[AstNode[Ast.FormalParam]]] = Encoder.encodeString.contramap(addAnnotationJson(_: (List[String], AstNode[Ast.FormalParam], List[String]))._1, addTypeName(_: (List[String], AstNode[Ast.FormalParam], List[String]))._2.asJson, _._3)
  implicit val updateEncoder: Encoder[Ast.SpecTlmChannel.Update] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val limitKindEncoder: Encoder[Ast.SpecTlmChannel.LimitKind] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val generalKindEncoder: Encoder[Ast.SpecPortInstance.GeneralKind] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val specialInputKindEncoder: Encoder[Ast.SpecPortInstance.SpecialInputKind] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val specialKindEncoder: Encoder[Ast.SpecPortInstance.SpecialKind] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val visibilityEncoder: Encoder[Ast.Visibility] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val patternEncoder: Encoder[Ast.SpecConnectionGraph.Pattern] = Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  
  implicit def optionEncoder[A](implicit encoder: Encoder[A]): Encoder[Option[A]] = new Encoder[Option[A]] {
  def apply(option: Option[A]): Json = option match {
    case Some(value) => Json.obj("Option" -> Json.obj("Some" -> encoder(value)))
    case None => Json.obj("Option" -> Json.fromString("None"))
  }}

  implicit val formalParamEncoder: Encoder[Ast.Annotated[AstNode[Ast.FormalParam]]] = Encoder.instance (
    (aNode: Ast.Annotated[AstNode[Ast.FormalParam]]) => aNode match {
        case aNode: Ast.Annotated[AstNode[Ast.FormalParam]] => addAnnotationJson(aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3)
    })
    
  implicit val qualIdentEncoder: Encoder[Ast.QualIdent] = Encoder.instance (
    (q: Ast.QualIdent) => q match {
        case ident: Ast.QualIdent.Unqualified => addTypeName(ident, ident.asJson)
        case ident: Ast.QualIdent.Qualified =>addTypeName(ident, ident.asJson)
    })

  implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] = Encoder.instance (
    (m: Ast.ModuleMember) => m.node._2 match {
        case aNode: Ast.ModuleMember.DefAbsType => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefArray => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefComponent => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefComponentInstance => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefConstant => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefEnum => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefModule => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefPort => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefStruct => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.DefTopology => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.SpecInclude => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
        case aNode: Ast.ModuleMember.SpecLoc => addAnnotationJson(m.node._1, addTypeName(aNode, aNode.asJson), m.node._3)
    })
  
  implicit val exprEncoder: Encoder[Ast.Expr] = Encoder.instance (
    (e: Ast.Expr) => e match {
        case expr: Ast.ExprArray => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprBinop => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprDot => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprParen => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprIdent => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprStruct => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprUnop => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralInt => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralBool => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralFloat => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralString => addTypeName(expr, expr.asJson)
    })

  implicit val componentMemberEncoder: Encoder[Ast.ComponentMember] = Encoder.instance (
    (c: Ast.ComponentMember) => c.node._2 match {
        case aNode: Ast.ComponentMember.DefAbsType => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.DefArray => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.DefConstant => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.DefEnum => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.DefStruct => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecCommand => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecEvent => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecInclude => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecInternalPort => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecParam => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecPortInstance => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecPortMatching => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
        case aNode: Ast.ComponentMember.SpecTlmChannel => addAnnotationJson(c.node._1, addTypeName(aNode, aNode.asJson), c.node._3)
    })


  implicit val typeNameEncoder: Encoder[Ast.TypeName] = Encoder.instance (
    (t: Ast.TypeName) => t match {
        case t: Ast.TypeNameFloat => addTypeName(t, t.asJson)
        case t: Ast.TypeNameInt => addTypeName(t, t.asJson)
        case t: Ast.TypeNameQualIdent => addTypeName(t, t.asJson)
        case t: Ast.TypeNameString => addTypeName(t, t.asJson)
        case t => addTypeName(t, t.asJson)
    })

  implicit val topologyMemberEncoder: Encoder[Ast.TopologyMember] = Encoder.instance (
    (t: Ast.TopologyMember) => t.node._2 match {
        case aNode: Ast.TopologyMember.SpecCompInstance => addAnnotationJson(t.node._1, addTypeName(aNode, aNode.asJson), t.node._3)
        case aNode: Ast.TopologyMember.SpecConnectionGraph => addAnnotationJson(t.node._1, addTypeName(aNode, aNode.asJson), t.node._3)
        case aNode: Ast.TopologyMember.SpecInclude => addAnnotationJson(t.node._1, addTypeName(aNode, aNode.asJson), t.node._3)
        case aNode: Ast.TopologyMember.SpecTopImport => addAnnotationJson(t.node._1, addTypeName(aNode, aNode.asJson), t.node._3)
    })

  def printAst(options: Options)(tul: List[Ast.TransUnit]): Result.Result[List[Ast.TransUnit]] = {
    options.ast match {
      case true => {
        
        val lines = tul.map(AstWriter.transUnit).flatten
        println("__________")
        println(tul)
        println("__________")
        println(tul.asJson)
        println("__________")
        lines.map(Line.write(Line.stdout) _)
      }
      case false => ()
    }
    Right(tul)
  }

  def resolveIncludes(options: Options)(tul: List[Ast.TransUnit]): Result.Result[List[Ast.TransUnit]] = {
    options.include match {
      case true => for { 
        result <- ResolveSpecInclude.transformList(
          Analysis(),
          tul, 
          ResolveSpecInclude.transUnit
        )
      } yield result._2
      case false => Right(tul)
    }
  }

  val builder = OParser.builder[Options]

  val name = "fpp-syntax"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      opt[Unit]('a', "ast")
        .action((_, c) => c.copy(ast = true))
        .text("print the abstract syntax tree (ast)"),
      opt[Unit]('i', "include")
        .action((_, c) => c.copy(include = true))
        .text("resolve include specifiers"),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

  def addTypeName[T](x: T, json: Json): Json = Json.obj(getUnqualifiedClassName(x) -> json)

  //Strip off longest prefix with a . then $ then remove any additional $'s
  //Use 
  val regex1 = "\\A[^.]*\\.".r

  def getUnqualifiedClassName[T] (x: T): String = x.getClass.getName.replaceAll("\\A.*\\.", "").replaceAll("\\$$", "").replaceAll("\\A.*\\$", "")
  
  def addAnnotationJson(pre: List[String], data: Json, post: List[String]): Json = Json.obj("preAnnotation" -> pre.asJson, "data" -> data, "postAnnotation" -> post.asJson)

}

