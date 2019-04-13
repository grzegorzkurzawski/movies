package pl.kurzawski.management.db

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, Matchers}
import pl.kurzawski.management.db.CustomPostgresProfile.api._
import pl.kurzawski.management.db.Tables._
import pl.kurzawski.management.db.model.ReviewRecord
import pl.kurzawski.management.util.DataContext._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContextExecutor, Future}

class RepositoryTest extends AsyncFlatSpec with BeforeAndAfterAll with Matchers {

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  private val dbURL = s"jdbc:postgresql://localhost:6789/movies"
  private val dbUser = "admin"
  private val dbPwd = "admin"

  private val configStr =
    s"""
       |db {
       |url = "$dbURL"
       |user = $dbUser
       |password = $dbPwd
       |}
     """.stripMargin

  private val config = ConfigFactory.parseString(configStr)
  private val db = Database.forConfig("db", config)
  val repo: Repository = new Repository(db)

  override def beforeAll(): Unit = {
    repo.createTablesIfNotExist()
  }

  def truncateDb: Future[Done] =
    for {
      _ <- db.run(moviesTable.delete)
      _ <- db.run(reviewTable.delete)
    } yield Done

  def acceptReview(review: ReviewRecord): Future[Int] =
    db.run(reviewTable.filter(_.id === review.id).update(review.copy(accepted = true)))

  it should "insert a single movie correctly into the database" in {
    for {
      _ <- truncateDb
      movie <- repo.insertMovie(postMovie)
      movies <- db.run(moviesTable.result)
    } yield {
      movies.length shouldBe 1
      movies.head.id shouldBe movie.id
      movies.head.title shouldBe postMovie.title
      movies.head.director shouldBe postMovie.director
      movies.head.actors shouldBe postMovie.actors
    }
  }

  it should "insert a single review correctly into the database" in {
    for {
      _ <- truncateDb
      movie <- repo.insertMovie(postMovie)
      _ <- repo.insertReview(postReview, movie.id)
      review <- db.run(reviewTable.result)
    } yield {
      review.length shouldBe 1
      review.head.movieId shouldBe movie.id
      review.head.rating shouldBe postReview.rating
    }
  }

  it should "delete a single movie and all its reviews" in {
    for {
      _ <- truncateDb
      movie <- repo.insertMovie(postMovie)
      _ <- repo.insertReview(postReview, movie.id)
      _ <- repo.insertReview(postReview2, movie.id)
      _ <- repo.deleteMovie(movie.id)
      movies <- db.run(moviesTable.result)
      reviews <- db.run(reviewTable.result)
    } yield {
      movies.length shouldBe 0
      reviews.length shouldBe 0
    }
  }

  it should "return a list of movies with one movie without reviews" in {
    for {
      _ <- truncateDb
      movie <- repo.insertMovie(postMovie)
      movies <- repo.getMovies
    } yield {
      movies.data.length shouldBe 1
      movies.data.head.id shouldBe movie.id
      movies.data.head.title shouldBe postMovie.title
      movies.data.head.rating shouldBe 0.0
      movies.data.head.director shouldBe postMovie.director
      movies.data.head.actors shouldBe postMovie.actors
    }
  }

  it should "return a list of movies with one movie without accepted reviews" in {
    for {
      _ <- truncateDb
      movie <- repo.insertMovie(postMovie)
      _ <- repo.insertReview(postReview, movie.id)
      _ <- repo.insertReview(postReview2, movie.id)
      movies <- repo.getMovies
    } yield {
      movies.data.length shouldBe 1
      movies.data.head.id shouldBe movie.id
      movies.data.head.title shouldBe postMovie.title
      movies.data.head.rating shouldBe 0.0
      movies.data.head.director shouldBe postMovie.director
      movies.data.head.actors shouldBe postMovie.actors
    }
  }

  it should "return a list of movies with one movie with accepted reviews" in {
    for {
      _ <- truncateDb
      movie <- repo.insertMovie(postMovie)
      review1 <- repo.insertReview(postReview, movie.id)
      _ <- repo.insertReview(postReview2, movie.id)
      review3 <- repo.insertReview(postReview3, movie.id)
      _ <- acceptReview(review1)
      _ <- acceptReview(review3)
      movies <- repo.getMovies
    } yield {
      movies.data.length shouldBe 1
      movies.data.head.id shouldBe movie.id
      movies.data.head.title shouldBe postMovie.title
      movies.data.head.rating shouldBe 2.0
      movies.data.head.director shouldBe postMovie.director
      movies.data.head.actors shouldBe postMovie.actors
    }
  }

  it should "return a list of movies in correct order" in {
    for {
      _ <- truncateDb
      movie1 <- repo.insertMovie(postMovie)
      review1 <- repo.insertReview(postReview, movie1.id)
      movie2 <- repo.insertMovie(postMovie)
      review2 <- repo.insertReview(postReview2, movie2.id)
      movie3 <- repo.insertMovie(postMovie)
      review3 <- repo.insertReview(postReview3, movie3.id)
      _ <- acceptReview(review1)
      _ <- acceptReview(review2)
      _ <- acceptReview(review3)
      movies <- repo.getMovies
    } yield {
      movies.data.length shouldBe 3
      movies.data.head.id shouldBe movie2.id
      movies.data(1).id shouldBe movie3.id
      movies.data(2).id shouldBe movie1.id
    }
  }

  it should "return a list of movies in correct order even if one of movies doesn't have review" in {
    for {
      _ <- truncateDb
      movie1 <- repo.insertMovie(postMovie)
      review1 <- repo.insertReview(postReview, movie1.id)
      movie2 <- repo.insertMovie(postMovie)
      review2 <- repo.insertReview(postReview2, movie2.id)
      movie3 <- repo.insertMovie(postMovie)
      _ <- acceptReview(review1)
      _ <- acceptReview(review2)
      movies <- repo.getMovies
    } yield {
      movies.data.length shouldBe 3
      movies.data.head.id shouldBe movie2.id
      movies.data(1).id shouldBe movie1.id
      movies.data(2).id shouldBe movie3.id
    }
  }

}
