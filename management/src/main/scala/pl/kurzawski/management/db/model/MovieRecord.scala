package pl.kurzawski.management.db.model

import pl.kurzawski.management.util.JsonProtocol._
import spray.json.RootJsonFormat

import java.time.Instant

case class MovieRecord(id: Int, title: String, director: String, actors: List[String], createdAt: Instant)

object MovieRecord {
  implicit val jsonFormat: RootJsonFormat[MovieRecord] = jsonFormat5(MovieRecord.apply)
}
