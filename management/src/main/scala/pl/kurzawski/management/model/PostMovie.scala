package pl.kurzawski.management.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class PostMovie(title: String, director: String, actors: List[String]) {

  require(title.length >= 3 && title.length <= 50, "Title must have at least 3 characters up to 50")
  require(title.matches("^[a-zA-Z]+$"), "Title can contain letters only")
}

object PostMovie {
  implicit val jsonFormat: RootJsonFormat[PostMovie] = jsonFormat3(PostMovie.apply)
}

