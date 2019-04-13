package pl.kurzawski.management.db.model

import org.joda.time.DateTime
import pl.kurzawski.management.util.JsonProtocol._
import spray.json.RootJsonFormat

case class MovieRecord(id: Int, title: String, director: String, actors: List[String], createdAt: DateTime)

object MovieRecord {
  implicit val jsonFormat: RootJsonFormat[MovieRecord] = jsonFormat5(MovieRecord.apply)
}
