package darnassus.persistence

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import darnassus.api.Job
import darnassus.enums.JobStatus
import darnassus.enums.JobStatus.JobStatus
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

/**
  * This is an event sourced entity. It has a state, [[JobState]], which
  * stores what the greeting should be (eg, "Hello").
  *
  * Event sourced entities are interacted with by sending them commands. This
  * entity supports two commands, a [[UseGreetingMessage]] command, which is
  * used to change the greeting, and a [[Hello]] command, which is a read
  * only command which returns a greeting to the name specified by the command.
  *
  * Commands get translated to events, and it's the events that get persisted by
  * the entity. Each event will have an event handler registered for it, and an
  * event handler simply applies an event to the current state. This will be done
  * when the event is first created, and it will also be done when the entity is
  * loaded from the database - each event will be replayed to recreate the state
  * of the entity.
  *
  * This entity defines one event, the [[GreetingMessageChanged]] event,
  * which is emitted when a [[UseGreetingMessage]] command is received.
  */
class JobEntity extends PersistentEntity {

  override type Command = JobCommand[_]
  override type Event = JobEvent
  override type State = JobState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: JobState = JobState("", JobStatus.Loading, None, None)

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case JobState(id, status, submitTs, lastUpdateTs) => Actions().onCommand[SubmitJob, Job] {

      case (SubmitJob(job), ctx, state) =>
        // In response to this command, we want to first persist it as a
        // JobSubmitted event
        ctx.thenPersist(JobSubmittedEvent(job)) { _ =>
          // Then once the event is successfully persisted, we respond with done.
          ctx.reply(job)
        }
    }
//      .onReadOnlyCommand[Hello, String] {
//
//      // Command handler for the Hello command
//      case (Hello(name), ctx, state) =>
//        // Reply with a message built from the current message, and the name of
//        // the person we're meant to say hello to.
//        ctx.reply(s"$message, $name!")
//
//    }.onEvent {
//
//      // Event handler for the GreetingMessageChanged event
//      case (GreetingMessageChanged(newMessage), state) =>
//        // We simply update the current state to use the greeting message from
//        // the event.
//        DarnassusState(newMessage, LocalDateTime.now().toString)
//
//    }
  }
}

// TODO split out events and commands into separate files
// TODO write custom serde for enum and datetime types

/**
  * The current state held by the persistent entity.
  */
case class JobState(id: String, status: JobStatus, submitTimestamp: Option[DateTime], lastUpdateTimestamp: Option[DateTime])

object JobState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[JobState] = Json.format
}

/**
  * This interface defines all the events that the DarnassusEntity supports.
  */
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

/**
  * An event that represents a change in greeting message.
  */
case class GreetingMessageChanged(message: String) extends JobEvent

object GreetingMessageChanged {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format
}

/**
  * This interface defines all the commands that the DarnassusEntity supports.
  */
sealed trait JobCommand[R] extends ReplyType[R]

case class SubmitJob(job: Job) extends JobCommand[Job]

object SubmitJob {
  implicit val format: Format[SubmitJob] = Json.format
}

/**
  * A command to switch the greeting message.
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class UseGreetingMessage(message: String) extends JobCommand[Done]

object UseGreetingMessage {

  /**
    * Format for the use greeting message command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[UseGreetingMessage] = Json.format
}

/**
  * A command to say hello to someone using the current greeting message.
  *
  * The reply type is String, and will contain the message to say to that
  * person.
  */
case class Hello(name: String) extends JobCommand[String]

object Hello {

  /**
    * Format for the hello command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[Hello] = Json.format
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object DarnassusSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[SubmitJob],
    JsonSerializer[JobSubmittedEvent],
    JsonSerializer[UseGreetingMessage],
    JsonSerializer[Hello],
    JsonSerializer[GreetingMessageChanged],
    JsonSerializer[JobState]
  )
}
