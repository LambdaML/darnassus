package darnassus.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import darnassus.api.{JobService, Job, JobSubmitted}
import darnassus.persistence.{JobEntity, JobEvent, JobSubmittedEvent, SubmitJob}

import scala.concurrent.{ExecutionContext, Future}

class JobServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
                    (implicit ec: ExecutionContext) extends JobService {

  import JobServiceImpl._

  override def submit: ServiceCall[String, Job] = ServiceCall { dsl =>
    validateAndParse(dsl).map { job =>
      val ref = persistentEntityRegistry.refFor[JobEntity](job.id)
      ref.ask(SubmitJob(job))
      job
    }
  }

  override def jobSubmittedTopic(): Topic[JobSubmitted] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(JobEvent.Tag, fromOffset)
          .map(event => (convertEvent(event), event.offset))
    }

  private def convertEvent(jobEvent: EventStreamElement[JobEvent]): JobSubmitted = {
    jobEvent.event match {
      case JobSubmittedEvent(job) => JobSubmitted(job)
    }
  }
}

object JobServiceImpl {
  def validateAndParse(dsl: String)(implicit ec: ExecutionContext): Future[Job] = {
    val id = UUID.randomUUID().toString
    Future.successful(Job(id, dsl))
  }
}
