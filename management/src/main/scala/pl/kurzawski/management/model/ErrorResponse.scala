package pl.kurzawski.management.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class ErrorResponse(error: String)

object ErrorResponse {
  implicit val jsonFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse.apply)
}
