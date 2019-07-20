package darnassus.impl

import darnassus.api
import darnassus.api.{DarnassusService, Job, JobSubmitted}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the DarnassusService.
  */
class DarnassusServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
                          (implicit ec: ExecutionContext) extends DarnassusService {

  import DarnassusServiceImpl._

  override def submit(id: String): ServiceCall[String, Job] = ServiceCall { dsl =>
    validate(dsl)
  }

  override def jobSubmittedTopic(): Topic[JobSubmitted] = ???

//  override def hello(id: String) = ServiceCall { _ =>
//    // Look up the darnassus entity for the given ID.
//    val ref = persistentEntityRegistry.refFor[DarnassusEntity](id)
//
//    // Ask the entity the Hello command.
//    ref.ask(Hello(id))
//  }
//
//  override def useGreeting(id: String) = ServiceCall { request =>
//    // Look up the darnassus entity for the given ID.
//    val ref = persistentEntityRegistry.refFor[DarnassusEntity](id)
//
//    // Tell the entity to use the greeting message specified.
//    ref.ask(UseGreetingMessage(request.message))
//  }
//
//
//  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
//    TopicProducer.singleStreamWithOffset {
//      fromOffset =>
//        persistentEntityRegistry.eventStream(DarnassusEvent.Tag, fromOffset)
//          .map(ev => (convertEvent(ev), ev.offset))
//    }
//
//  private def convertEvent(helloEvent: EventStreamElement[DarnassusEvent]): api.GreetingMessageChanged = {
//    helloEvent.event match {
//      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
//    }
//  }
}

object DarnassusServiceImpl {
  def validate(dsl: String)(implicit ec: ExecutionContext): Future[Job] = {
    Future.successful(Job("id", "dsl"))
  }
}
