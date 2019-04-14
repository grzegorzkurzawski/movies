package pl.kurzawski.approving

import akka.actor.{ActorRef, ActorSystem}
import com.newmotion.akka.rabbitmq._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import pl.kurzawski.approving.amqp.AmqpUtils
import pl.kurzawski.approving.db.DbUpdater
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContextExecutor


object Main extends App with StrictLogging {

  val config = ConfigFactory.load
  implicit val system: ActorSystem = ActorSystem("system", config)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val factory = new ConnectionFactory()
  factory.setHost("movies_rmq")
  val connectionActor: ActorRef = system.actorOf(ConnectionActor.props(factory), "akka-rabbitmq")

  val db: Database = Database.forConfig("db", config)
  val dbUpdater = new DbUpdater(db)
  AmqpUtils.setupConsumer(connectionActor, "queue", "amq.fanout", dbUpdater.approveReviews)

  logger.info(s"Approving reviews service started")
}
