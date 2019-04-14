package pl.kurzawski.management.util

import akka.actor.{ActorRef, ActorSystem}
import com.newmotion.akka.rabbitmq._

object AmqpUtils {
  val Queue = "queue"
  val Exchange = "amq.fanout"

  def createChannelActor(connectionActor: ActorRef)(implicit system: ActorSystem): ActorRef = {
    connectionActor.createChannel(ChannelActor.props(), Some("channel"))

    def setupChannel(channel: Channel, self: ActorRef) = {
      channel.queueDeclare(Queue, false, false, false, null)
      channel.queueBind(Queue, Exchange, "")
    }
    connectionActor.createChannel(ChannelActor.props(setupChannel))
  }

  def publishInt(channel: Channel, id: Int): Unit =
    channel.basicPublish(Exchange, Queue, null, id.toString.getBytes)

}
