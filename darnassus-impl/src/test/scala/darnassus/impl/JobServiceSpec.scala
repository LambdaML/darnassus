package darnassus.impl

import akka.stream.testkit.scaladsl.TestSink
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.{ServiceTest, TestTopicComponents}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import darnassus.api._

class JobServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(ServiceTest.defaultSetup.withCassandra()) { ctx =>
    new Application(ctx) with LocalServiceLocator with TestTopicComponents
  }

  implicit val system = server.actorSystem
  implicit val mat = server.materializer

  val client: JobService = server.serviceClient.implement[JobService]

  override protected def afterAll(): Unit = server.stop()

  "The Darnassus service" should {

    "validate a dsl" in {
      JobServiceImpl.validateAndParse("dsl").map(_.dsl shouldBe "dsl")
    }

    "show current local status for jobs" in {
      val jobsTup = for {
        job1 <- client.submit().invoke("job1")
        job2 <- client.submit().invoke("job2")
        jobs <- client.getTrackedJobs.invoke()
      } yield (jobs, job1, job2)

      jobsTup.map {
        case (jobs, job1, job2) =>
          jobs should contain(job1, job2)
      }
    }

    // TODO create separate server for this test
//    "publish events on the topic" in {
//      val source = client.jobSubmittedTopic().subscribe.atMostOnceSource
//
//      client.submit().invoke("dsl").map { publishedJob =>
//        source
//          .runWith(TestSink.probe[JobSubmitted])
//          .request(1)
//          .expectNext
//          .job shouldEqual publishedJob
//      }
//    }
  }
}
