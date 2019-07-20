package darnassus.enums

import play.api.libs.json.{Format, JsResult, JsValue, Json}

object JobStatus extends Enumeration {
  type JobStatus = Value
  val Submitted, Running, Stopped, Errored = Value

  implicit val format = new Format[JobStatus.Value] {
    override def writes(status: JobStatus.Value): JsValue = Json.toJson(status.toString)
    override def reads(json: JsValue): JsResult[JobStatus.Value] =
      json.validate[String].map(JobStatus.withName)
  }
}
