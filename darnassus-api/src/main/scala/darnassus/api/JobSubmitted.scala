package darnassus.api

import play.api.libs.json.{Format, Json}

case class JobSubmitted(job: Job)

object JobSubmitted {
  implicit val format: Format[JobSubmitted] = Json.format
}
