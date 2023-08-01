package fpp.compiler.codegen

import io.circe._
import io.circe.syntax._

/** Generic methods for JSON encoding */
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

  /** Transform { "foo" : { } } to "foo" */
  def collapseEmptyObjects(json: Json): Json = {
    def collapseVector(v: Vector[Json]): Vector[Json] =
      v.map(collapseEmptyObjects(_))
    def collapseObject(o: JsonObject): JsonObject =
      o.mapValues(collapseEmptyObjects(_))
    val labelOpt = for {
      list <- json.asObject.map(_.toList)
      pair <- list match { 
        case p :: Nil => Some(p)
        case _ => None
      }
      innerObj <- pair._2.asObject
      string <- if (innerObj.isEmpty) Some(pair._1) else None
    }
    yield string.asJson
    labelOpt.getOrElse(json.mapArray(collapseVector).mapObject(collapseObject))
  }

}
