package darnassus.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object DarnassusService  {
  val TOPIC_NAME = "jobs-submitted"
}

trait DarnassusService extends Service {

  def submit(id: String): ServiceCall[String, Job]

  def jobSubmittedTopic(): Topic[JobSubmitted]

  override final def descriptor: Descriptor = {
    import Service._
    named("darnassus")
      .withCalls(
        restCall(Method.POST, "/submit", submit _),
      )
      .withTopics(
        topic(DarnassusService.TOPIC_NAME, jobSubmittedTopic _)
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[JobSubmitted](_.job.id)
          )
      )
      .withAutoAcl(true)
  }
}

case class Job(id: String, dsl: String)

object Job {
  implicit val format: Format[Job] = Json.format
}


case class JobSubmitted(job: Job)

object JobSubmitted {
  implicit val format: Format[JobSubmitted] = Json.format
}
