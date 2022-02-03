package pl.kurzawski.approving.db

import akka.Done
import com.github.tminglei.slickpg.ExPostgresProfile.api._
import com.typesafe.scalalogging.StrictLogging
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}

class DbUpdater(db: Database)(implicit ex: ExecutionContext) extends StrictLogging {

  private def updateQuery(id: Int): DBIO[Int] = sqlu"UPDATE reviews SET accepted = true WHERE id = $id;"

  def approveReviews(id: Int): Future[Done] = db.run(updateQuery(id)).map(_ => Done)
}