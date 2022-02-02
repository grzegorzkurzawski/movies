package pl.kurzawski.approving.amqp

import akka.actor.ActorRef
import com.newmotion.akka.rabbitmq.{BasicProperties, Channel, ChannelActor, CreateChannel, DefaultConsumer, Envelope}

object AmqpUtils {

  def setupConsumer(connectionActor: ActorRef, queue: String, exchange: String, f: Int => Unit): Unit =
    connectionActor ! CreateChannel(ChannelActor.props(setupSubscriber(queue, exchange, f, _, _)), Some("subscriber"))

  private def setupSubscriber(queue: String, exchange: String, f: Int => Unit, channel: Channel, self: ActorRef): String = {
    channel.queueDeclare(queue, false, false, false, null)
    channel.queueBind(queue, exchange, "")
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]): Unit = {
        val number: Int = new String(body).toInt
        f(number)
      }
    }
    channel.basicConsume(queue, true, consumer)
  }
}
