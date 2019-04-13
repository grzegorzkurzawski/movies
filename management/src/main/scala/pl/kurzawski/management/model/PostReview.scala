package pl.kurzawski.management.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class PostReview(rating: Int) {
  require(rating > 0 && rating < 6, "Rating needs to be greater than 0 and lower than 6")
}

object PostReview {
  implicit val jsonFormat: RootJsonFormat[PostReview] = jsonFormat1(PostReview.apply)
}
