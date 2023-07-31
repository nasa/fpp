package fpp.compiler.codegen

import io.circe.syntax._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import fpp.compiler.ast._

trait JsonEncoder {
    
    implicit def optionEncoder[A](implicit encoder: Encoder[A]): Encoder[Option[A]] = {
        case Some(value) => Json.obj("Option" -> Json.obj("Some" -> encoder(value)))
        case None        => Json.obj("Option" -> Json.fromString("None"))
    }

    def addTypeName[T](x: T, json: Json): Json =
    Json.obj(getUnqualifiedClassName(x) -> json)

    def getUnqualifiedClassName[T](x: T): String =
        x.getClass.getName
        .replaceAll("\\A.*\\.", "")
        .replaceAll("\\$$", "")
        .replaceAll("\\A.*\\$", "")

}
