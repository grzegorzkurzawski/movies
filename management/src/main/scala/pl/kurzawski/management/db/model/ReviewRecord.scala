package pl.kurzawski.management.db.model

import pl.kurzawski.management.util.JsonProtocol._
import spray.json.RootJsonFormat

case class ReviewRecord(id: Int, movieId: Int, rating: Int, accepted: Boolean)

object ReviewRecord {
  implicit val jsonFormat: RootJsonFormat[ReviewRecord] = jsonFormat4(ReviewRecord.apply)
}
