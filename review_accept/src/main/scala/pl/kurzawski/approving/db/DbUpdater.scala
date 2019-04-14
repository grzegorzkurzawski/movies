package pl.kurzawski.approving.db

import akka.Done
import akka.actor.ActorSystem
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import com.github.tminglei.slickpg.ExPostgresProfile.api._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}


class DbUpdater(db: Database)(implicit actorSystem: ActorSystem, ex: ExecutionContext) extends StrictLogging {

  val UpdateQuery: DBIO[Int] = sqlu"UPDATE reviews SET accepted = true WHERE accepted = false;"

  def approveReviews: Future[Done] = {
    db.run(UpdateQuery).map {
      updated =>
        logger.info("Updated {} rows", updated)
        Done
    }
  }
}