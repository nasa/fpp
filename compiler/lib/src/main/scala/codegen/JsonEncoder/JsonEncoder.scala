package fpp.compiler.codegen

import io.circe.syntax._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import fpp.compiler.ast._

trait JsonEncoder {
    
  /** Encodes a value of Option type */
  implicit def optionEncoder[A](implicit encoder: Encoder[A]): Encoder[Option[A]] = {
    case Some(value) => Json.obj("Some" -> encoder(value))
    case None => Json.fromString("None")
  }

  /** Adds the type name as an object key */
  def addTypeNameKey[T](obj: T, json: Json): Json =
    Json.obj(getUnqualifiedClassName(obj) -> json)

  /** Gets the unqualified class name corresponding to the class name of a
   *  Scala object */
  def getUnqualifiedClassName[T](obj: T): String =
    obj.getClass.getName
    .replaceAll("\\A.*\\.", "")
    .replaceAll("\\$$", "")
    .replaceAll("\\A.*\\$", "")

}
