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
  
  implicit val fileEncoder: Encoder[File] =
    Encoder.encodeString.contramap(_.toString)

  implicit val positionEncoder: Encoder[Position] =
    Encoder.encodeString.contramap(_.toString)

  // JSON Encoder for locations
  // We use semiautomatic derivation here to avoid excessive inlining.
  implicit val locationEncoder: Encoder[Location] = 
      io.circe.generic.semiauto.deriveEncoder[Location]

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
