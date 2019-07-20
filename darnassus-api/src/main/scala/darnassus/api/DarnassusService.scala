package darnassus.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object DarnassusService  {
  val TOPIC_NAME = "greetings"
}

trait DarnassusService extends Service {

  def submit(id: String): ServiceCall[String, String]

  /**
    * This gets published to Kafka.
    */
  def jobSubmittedTopic(): Topic[JobSubmitted]

  override final def descriptor: Descriptor = {
    import Service._
    named("darnassus")
      .withCalls(
        restCall(Method.POST, "/submit", submit _),
      )
      .withTopics(
        topic(DarnassusService.TOPIC_NAME, jobSubmittedTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
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
  implicit val format: Format[Job] = Json.format[Job]
}

case class JobSubmitted(job: Job)

object JobSubmitted {
  implicit val format: Format[JobSubmitted] = Json.format[JobSubmitted]
}
