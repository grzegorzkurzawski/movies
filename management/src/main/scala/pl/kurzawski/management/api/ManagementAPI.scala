package pl.kurzawski.management.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MalformedRequestContentRejection, RejectionHandler, Route, ValidationRejection}
import pl.kurzawski.management.db.Repository
import pl.kurzawski.management.model.{ErrorResponse, PostMovie, PostReview}

import scala.concurrent.ExecutionContext

class ManagementAPI(repo: Repository)(implicit ex: ExecutionContext) {

  val routes: Route = handleRejections(rejectionHandler) {
    ignoreTrailingSlash {
      path("movies") {
        get {
          complete(StatusCodes.OK, repo.getMovies)
        } ~
          post {
            entity(as[PostMovie]) { movie =>
              complete(StatusCodes.Created, repo.insertMovie(movie))
            }
          }
      } ~
        path("movies" / IntNumber) { id =>
          delete {
            onSuccess(repo.deleteMovie(id)) & complete(StatusCodes.NoContent)
          }
        } ~
        path("movies" / IntNumber / "review") { movieId =>
          post {
            entity(as[PostReview]) { review =>
              onSuccess(repo.checkIfExists(movieId)) { exists =>
                if (exists) complete(StatusCodes.Created, repo.insertReview(review, movieId))
                else complete(StatusCodes.NotFound, ErrorResponse("The movie was not found"))
              }
            }
          }
        }
    }
  }

  def rejectionHandler: RejectionHandler = {
    RejectionHandler
      .newBuilder()
      .handle {
        case MalformedRequestContentRejection(message, _) =>
          complete(StatusCodes.BadRequest, ErrorResponse(message))
        case ValidationRejection(message, _) =>
          complete(StatusCodes.BadRequest, ErrorResponse(message))
      }
      .result()
      .withFallback(RejectionHandler.default)
  }

}
