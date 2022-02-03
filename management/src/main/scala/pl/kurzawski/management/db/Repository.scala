package pl.kurzawski.management.db

import akka.Done
import akka.actor.ActorRef
import com.newmotion.akka.rabbitmq.ChannelMessage
import com.typesafe.scalalogging.StrictLogging
import pl.kurzawski.management.db.CustomPostgresProfile.api._
import pl.kurzawski.management.db.Tables._
import pl.kurzawski.management.db.model.{MovieRecord, ReviewRecord}
import pl.kurzawski.management.model.{Movie, Movies, PostMovie, PostReview}
import pl.kurzawski.management.util.AmqpUtils
import slick.jdbc.GetResult
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.meta.MTable

import java.time.Instant
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class Repository(val db: Database, channelActor: ActorRef)(implicit ex: ExecutionContext) extends StrictLogging {

  implicit val getMovieResult: GetResult[Movie] =
    GetResult { result =>
      Movie(
        result.nextInt(),
        result.nextString(),
        result.nextDouble(),
        result.nextString(),
        result.nextArray().toList,
        Instant.ofEpochMilli(result.nextTimestamp().getTime)
      )
    }

  private val selectMovies: DBIO[Seq[Movie]] =
    sql"""
      SELECT m.id, m.title, COALESCE(AVG(r.rating), 0.0) avg_rating, m.director, m.actors, m.created_at
      FROM movies m LEFT JOIN reviews r ON m.id = r.movie_id AND r.accepted = true
      GROUP BY m.id
      ORDER BY avg_rating DESC;
    """.as[Movie]


  def createTablesIfNotExist(): Unit = {
    val tables = Seq(moviesTable, reviewTable)
    val existingTables = db.run(MTable.getTables)
    Await.result(
      existingTables.flatMap { t =>
        val names = t.map(_.name.name)
        val createTables = tables.filter(table => !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
        db.run(DBIO.sequence(createTables))
    }, 1 minute)
  }

  def checkIfExists(id: Int): Future[Boolean] =
    db.run(moviesTable.filter(_.id === id).result).map(_.nonEmpty)

  def insertMovie(movie: PostMovie): Future[MovieRecord] = {
    val moviesRecord = MovieRecord(0, movie.title, movie.director, movie.actors, Instant.now)
    db.run(moviesTable.returning(moviesTable) += moviesRecord)
  }

  def insertReview(review: PostReview, movieId: Int): Future[ReviewRecord] =
    db.run(reviewTable.returning(reviewTable) += ReviewRecord(0, movieId, review.rating, false)).map { review =>
      channelActor ! ChannelMessage(AmqpUtils.publishInt(_, review.id))
      review
    }

  def deleteMovie(movieId: Int): Future[Done] =
    db.run(moviesTable.filter(_.id === movieId).delete).map(_ => Done)

  def getMovies: Future[Movies] =
    db.run(selectMovies).map(Movies(_))
}
