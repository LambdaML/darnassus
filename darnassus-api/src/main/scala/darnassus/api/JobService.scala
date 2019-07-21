package darnassus.api

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

object JobService  {
  val TOPIC_NAME = "jobs-submitted"
}

trait JobService extends Service {

  def submit: ServiceCall[String, Job]

  def jobSubmittedTopic(): Topic[JobSubmitted]

  override final def descriptor: Descriptor = {
    import Service._
    named("darnassus")
      .withCalls(
        restCall(Method.POST, "/submit", submit _),
      )
      .withTopics(
        topic(JobService.TOPIC_NAME, jobSubmittedTopic _)
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[JobSubmitted](_.job.id)
          )
      )
      .withAutoAcl(true)
  }
}
