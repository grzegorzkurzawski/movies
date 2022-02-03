package pl.kurzawski.management.api

import akka.Done
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pl.kurzawski.management.db.Repository
import pl.kurzawski.management.db.model.{MovieRecord, ReviewRecord}
import pl.kurzawski.management.model.{Movie, Movies}
import pl.kurzawski.management.util.DataContext._

import java.time.Instant
import scala.concurrent.Future

class ManagementAPITest extends AnyWordSpec with ScalatestRouteTest with MockitoSugar with Matchers {

  val repo: Repository = mock[Repository]
  val api = new ManagementAPI(repo)

  val movie = Movie(1, "title", 1.0, "Hopkins", List("Pitt", "Jolie"), Instant.now)
  val movieRecord = MovieRecord(1, "title", "Hopkins", List("Pitt", "Jolie"), Instant.now)
  val movies = Movies(Seq(movie))

  "GET /movies" should {
    when(repo.getMovies) thenReturn Future.successful(movies)

    "return valid movies list" in {
      Get("/movies") ~> api.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Movies] shouldBe movies
      }
    }

    "ignore trailing slash" in {
      Get("/movies/") ~> api.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Movies] shouldBe movies
      }
    }
  }


  "POST /movies" should {
    when(repo.insertMovie(postMovie)) thenReturn Future.successful(movieRecord)

    "return added movie" in {
      Post("/movies", postMovie) ~> api.routes ~> check {
        status shouldBe StatusCodes.Created
        responseAs[MovieRecord] shouldBe movieRecord
      }
    }

    "ignore trailing slash" in {
      Post("/movies/", postMovie) ~> api.routes ~> check {
        status shouldBe StatusCodes.Created
        responseAs[MovieRecord] shouldBe movieRecord
      }
    }

    "return BadRequest if title is too small" in {
      val bodyJson = ByteString(
        s"""
           |{
           |    "id":"abc",
           |    "title":"ab",
           |    "director":"Hopkins",
           |    "actors":["Pitt", "Jolie"]
           |}
        """.stripMargin)

      Post("/movies/", HttpEntity(MediaTypes.`application/json`, bodyJson)) ~> Route.seal(api.routes) ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    "return BadRequest if title is too long" in {
      val bodyJson = ByteString(
        s"""
           |{
           |    "id":"abc",
           |    "title":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
           |    "director":"Hopkins",
           |    "actors":["Pitt", "Jolie"]
           |}
        """.stripMargin)

      Post("/movies/", HttpEntity(MediaTypes.`application/json`, bodyJson)) ~> Route.seal(api.routes) ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    "return BadRequest if title contains disallowed sign" in {
      val bodyJson = ByteString(
        s"""
           |{
           |    "id":"abc",
           |    "title":"bb3a",
           |    "director":"Hopkins",
           |    "actors":["Pitt", "Jolie"]
           |}
        """.stripMargin)

      Post("/movies/", HttpEntity(MediaTypes.`application/json`, bodyJson)) ~> Route.seal(api.routes) ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }
  }

  "DELETE /movies/{id}" should {
    when(repo.deleteMovie(id)) thenReturn Future.successful(Done)

    "return valid movies list" in {
      Delete(s"/movies/$id") ~> api.routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }

    "ignore trailing slash" in {
      Delete(s"/movies/$id/") ~> api.routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }
  }

  "POST /movies/{id}/review" should {
    val reviewRecord = ReviewRecord(1, id, 1, false)
    when(repo.insertReview(postReview, id)) thenReturn Future.successful(reviewRecord)
    when(repo.checkIfExists(id)) thenReturn Future.successful(true)


    "return added movie" in {
      Post(s"/movies/$id/review", postReview) ~> api.routes ~> check {
        status shouldBe StatusCodes.Created
        responseAs[ReviewRecord] shouldBe reviewRecord
      }
    }

    "ignore trailing slash" in {
      Post(s"/movies/$id/review/", postReview) ~> api.routes ~> check {
        status shouldBe StatusCodes.Created
        responseAs[ReviewRecord] shouldBe reviewRecord
      }
    }

    "return BadRequest if rating is lower than 1" in {
      val bodyJson = ByteString("{rating:0}")

      Post(s"/movies/$id/review/", HttpEntity(MediaTypes.`application/json`, bodyJson)) ~> Route.seal(api.routes) ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    "return BadRequest if rating is greater than 5" in {
      val bodyJson = ByteString("{rating:6}")

      Post(s"/movies/$id/review/", HttpEntity(MediaTypes.`application/json`, bodyJson)) ~> Route.seal(api.routes) ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    "return NotFound if movie not exists" in {
      val id2 = id + 1
      when(repo.checkIfExists(id2)) thenReturn Future.successful(false)

      Post(s"/movies/$id2/review/", postReview) ~> Route.seal(api.routes) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }

}
