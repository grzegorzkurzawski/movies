package pl.kurzawski.management.util

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, deserializationError}

trait JodaTimeProtocol extends DefaultJsonProtocol {
  import JodaTimeProtocol._

  implicit object DefaultJodaTimeFormat extends JsonFormat[DateTime] {
    override def write(dateTime: DateTime): JsValue = {
      JsString(dateTime.toString(ISODateTimeFormat.dateTime.withZoneUTC()))
    }

    override def read(json: JsValue): DateTime = {
      json match {
        case JsString(s) => readString(s)
        case JsNumber(num) => readLong(num.toLongExact)
        case _ => deserializationError("Expected ISO DateTime string or Unix timestamp")
      }
    }
  }
}

object JodaTimeProtocol {
  def readString(s: String): DateTime =
    ISODateTimeFormat.dateTimeParser.withZoneUTC.parseDateTime(s)

  def readLong(num: Long): DateTime =
    new DateTime(num, DateTimeZone.UTC)
}
