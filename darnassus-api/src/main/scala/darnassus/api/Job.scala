package darnassus.api

import play.api.libs.json.{Format, Json}

case class Job(id: String, dsl: String)

object Job {
  implicit val format: Format[Job] = Json.format
}
