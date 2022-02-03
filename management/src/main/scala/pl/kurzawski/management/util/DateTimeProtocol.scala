package pl.kurzawski.management.util

import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, deserializationError}

import java.time.Instant

trait DateTimeProtocol extends DefaultJsonProtocol {
  import DateTimeProtocol._

  implicit object DefaultInstantFormat extends JsonFormat[Instant] {

    override def write(instant: Instant): JsValue = JsString(instant.toString)

    override def read(json: JsValue): Instant = {
      json match {
        case JsString(s) => readString(s)
        case JsNumber(num) => readLong(num.toLongExact)
        case _ => deserializationError("Expected ISO DateTime string or Unix timestamp")
      }
    }
  }
}

object DateTimeProtocol {
  def readString(s: String): Instant = Instant.parse(s)
  def readLong(num: Long): Instant = Instant.ofEpochSecond(num)
}
