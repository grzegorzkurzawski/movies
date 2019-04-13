package pl.kurzawski.management

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import pl.kurzawski.management.api.ManagementAPI
import pl.kurzawski.management.db.Repository
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContextExecutor

object Main extends App with StrictLogging {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  val config = ConfigFactory.load
  val db: Database = Database.forConfig("db", config)
  val repo = new Repository(db)
  repo.createTablesIfNotExist()

  // Server
  val http = Http()
  val managementApi = new ManagementAPI(repo)
  val bindingFuture = http.bindAndHandle(managementApi.routes, "0.0.0.0", 9000)

  logger.info(s"Starting server at http://0.0.0.0:9000")

}
