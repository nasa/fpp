package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._
import io.circe.syntax._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import scala.util.parsing.input.Position

/** JSON encoder for the location map */
object LocMapJsonEncoder extends JsonEncoder {
  
  implicit val fileEncoder: Encoder[File] = new Encoder[File] {
    override def apply(file: File): Json = Json.fromString(file.toString)
  }

  implicit val positionEncoder: Encoder[Position] = new Encoder[Position] {
    override def apply(position: Position): Json =
      Json.fromString(position.toString)
  }

  // JSON Encoder for locations.
  // In theory, Circe should be able to auto-generate this encoder.
  // However, removing this code causes excessive inlining.
  implicit val locationEncoder: Encoder[Location] = new Encoder[Location] {
    override def apply(location: Location): Json = Json.obj(
      "file" -> location.file.asJson,
      "pos" -> location.pos.asJson,
      "includingLoc" -> location.includingLoc.asJson
    )
  }

  /** Converts the location map to JSON */
  def locMapToJson: Json = {
    val locationsList =
      Locations.getMap.toList.sortWith(_._1 < _._1).map { 
        case (id, location) => id.toString -> location.asJson
      }
    // Convert the list elements to function arguments
    Json.obj(locationsList: _*)
  }

}
