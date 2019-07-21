package darnassus.persistence

import java.time.LocalDateTime

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import darnassus.api.Job
import darnassus.enums.JobStatus._
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class JobEntity extends PersistentEntity {

  override type Command = JobCommand[_]
  override type Event = JobEvent
  override type State = JobState

  override def initialState: JobState = JobState("", Submitted, None, None)

  override def behavior: Behavior = {
    case JobState(id, status, submitTs, lastUpdateTs) => Actions()
      .onCommand[SubmitJob, Job] {
        case (SubmitJob(job), ctx, state) =>
          ctx.thenPersist(JobSubmittedEvent(job)) { _ =>
            state.copy()
            ctx.reply(job)
          }
      }
      .onEvent {
        case (JobSubmittedEvent(job), _) =>
          val currentTs = LocalDateTime.now()
          JobState(job.id, Submitted, submitTimestamp = Some(currentTs), lastUpdateTimestamp = Some(currentTs))

      }
    //      .onReadOnlyCommand[Hello, String] {
    //
    //      // Command handler for the Hello command
    //      case (Hello(name), ctx, state) =>
    //        // Reply with a message built from the current message, and the name of
    //        // the person we're meant to say hello to.
    //        ctx.reply(s"$message, $name!")
    //
    //    }
  }
}

case class JobState(id: String, status: JobStatus, submitTimestamp: Option[LocalDateTime],
                    lastUpdateTimestamp: Option[LocalDateTime])

object JobState {
  implicit val format: Format[JobState] = Json.format
}

// Events
sealed trait JobEvent extends AggregateEvent[JobEvent] {
  def aggregateTag: AggregateEventTag[JobEvent] = JobEvent.Tag
}

object JobEvent {
  val Tag: AggregateEventTag[JobEvent] = AggregateEventTag[JobEvent]
}

case class JobSubmittedEvent(job: Job) extends JobEvent

object JobSubmittedEvent {
  implicit val format: Format[JobSubmittedEvent] = Json.format
}

// Commands
sealed trait JobCommand[R] extends ReplyType[R]

case class SubmitJob(job: Job) extends JobCommand[Job]

object SubmitJob {
  implicit val format: Format[SubmitJob] = Json.format
}
