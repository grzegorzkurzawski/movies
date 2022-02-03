package pl.kurzawski.management.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.kurzawski.management.util.JsonProtocol._
import spray.json._

import java.time.{LocalDateTime, ZoneOffset}

class DateTimeProtocolTest extends AnyFlatSpec with Matchers {

  it should "correctly read date time from string" in {
    DateTimeProtocol.readString("2022-02-01T20:41:18Z") shouldBe LocalDateTime.of(2022, 2, 1, 20, 41, 18).toInstant(ZoneOffset.UTC)
  }

  it should "correctly read date time from epoch" in {
    DateTimeProtocol.readLong(1643870408L) shouldBe LocalDateTime.of(2022, 2, 3, 6, 40, 8).toInstant(ZoneOffset.UTC)
  }

  it should "write date time in ISO format" in {
    val instant = LocalDateTime.of(2022, 2, 1, 20, 41, 18).toInstant(ZoneOffset.UTC)
    instant.toJson shouldBe JsString("2022-02-01T20:41:18Z")
  }

}
