package pl.kurzawski.approving

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import pl.kurzawski.approving.db.DbUpdater
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContextExecutor


object Main extends App with StrictLogging {

  val config = ConfigFactory.load
  implicit val actorSystem: ActorSystem = ActorSystem("system", config)
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  val db: Database = Database.forConfig("db", config)
  val dbUpdater = new DbUpdater(db)

  logger.info(s"Approving reviews service started")

  val ex = new ScheduledThreadPoolExecutor(2)
  val task = new Runnable {
    def run() = dbUpdater.approveReviews
  }

  ex.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS)
}
