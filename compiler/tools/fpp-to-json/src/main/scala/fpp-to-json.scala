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
  
  //implicit val moduleEncoder: Encoder[Ast.ModuleMember.DefModule] = deriveEncoder[Ast.ModuleMember.DefModule]
  //implicit val annotatedDefConstantEncoder: Encoder[Ast.Annotated[Ast.ModuleMember.DefConstant]] = deriveEncoder[Ast.Annotated[Ast.ModuleMember.DefConstant]]
  //implicit val defConstantEncoder: Encoder[Ast.DefConstant] = deriveEncoder[Ast.DefConstant]
  //implicit val moduleMemberefConstantEncoder: Encoder[Ast.ModuleMember.DefConstant] = deriveEncoder[Ast.ModuleMember.DefConstant]
  //implicit val transUnitEncoder: Encoder[Ast.TransUnit] = deriveEncoder[Ast.TransUnit]
  //implicit val astNodeEncoder: Encoder[AstNode[Ast.Expr]] = deriveEncoder[AstNode[Ast.Expr]]
  implicit val binopEncoder: Encoder[Ast.Binop] = Encoder.encodeString.contramap(_.toString)
  implicit val unopEncoder: Encoder[Ast.Unop] = Encoder.encodeString.contramap(_.toString)

  implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] = Encoder.instance (
    (m: Ast.ModuleMember) => m.node._2 match {
        case defModule: Ast.ModuleMember.DefModule => Json.Null
        case defConstant: Ast.ModuleMember.DefConstant => defConstant.asJson
        case moduleMember => Json.Null
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

  def addTypeName[T](x: T, json: Json): Json = {
        val name: String = x.getClass.getName.substring(21)
        Json.obj(name -> json)
  }
  
}
