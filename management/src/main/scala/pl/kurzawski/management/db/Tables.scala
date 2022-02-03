package pl.kurzawski.management.db

import pl.kurzawski.management.db.CustomPostgresProfile.api._
import pl.kurzawski.management.db.model.{MovieRecord, ReviewRecord}
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import java.time.Instant

object Tables {

  val moviesTable = TableQuery[Movies]
  val reviewTable = TableQuery[Review]

  private val moviesTableName = "movies"
  private val reviewTableName = "reviews"

  class Movies(tag: Tag) extends Table[MovieRecord](tag, None, moviesTableName) {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title: Rep[String] = column[String]("title")
    def director: Rep[String] = column[String]("director")
    def actors: Rep[List[String]] = column[List[String]]("actors")
    def createdAt: Rep[Instant] = column[Instant]("created_at")
    def * : ProvenShape[MovieRecord] = (id, title, director, actors, createdAt) <> ((MovieRecord.apply _).tupled, MovieRecord.unapply)
  }

  class Review(tag: Tag) extends Table[ReviewRecord](tag, None, reviewTableName) {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def movieId: Rep[Int] = column[Int]("movie_id")
    def rating: Rep[Int] = column[Int]("rating")
    def accepted: Rep[Boolean] = column[Boolean]("accepted")
    def * : ProvenShape[ReviewRecord] = (id, movieId, rating, accepted) <> ((ReviewRecord.apply _).tupled, ReviewRecord.unapply)

    def movie: ForeignKeyQuery[Movies, MovieRecord] = foreignKey("movie", movieId, moviesTable)(_.id, onDelete=ForeignKeyAction.Cascade)
  }
}
