package pl.kurzawski.management.model

import org.joda.time.DateTime
import pl.kurzawski.management.util.JsonProtocol._
import spray.json.RootJsonFormat

case class Movie(id: Int, title: String, rating: Double, director: String, actors: List[String], createdAt: DateTime)

case class Movies(data: Seq[Movie])

object Movies {
  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat6(Movie.apply)
  implicit val moviesFormat: RootJsonFormat[Movies] = jsonFormat1(Movies.apply)
}
