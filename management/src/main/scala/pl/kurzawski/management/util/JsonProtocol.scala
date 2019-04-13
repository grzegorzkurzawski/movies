package pl.kurzawski.management.util

import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol with JodaTimeProtocol
