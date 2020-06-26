package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.language.implicitConversions

/** Writes out the locations of definitions */
object LocateDefsFppWriter extends AstVisitor with LineUtils {

  case class Config(val baseDir: Option[String])

  override def default(in: Config) = Nil

  type In = Config

  type Out = List[Line]

}
