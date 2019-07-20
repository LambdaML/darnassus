package darnassus.impl

import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import darnassus.api._

class DarnassusServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new DarnassusApplication(ctx) with LocalServiceLocator
  }

  val client: DarnassusService = server.serviceClient.implement[DarnassusService]

  override protected def afterAll(): Unit = server.stop()

  "The darnassus service" should {

    "validate a dsl" in {
      DarnassusServiceImpl.validate("""{add-dsl-here}""") shouldBe Job("id", "testarooni")
    }

//    "allow responding with a custom message" in {
//      for {
//        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
//        answer <- client.hello("Bob").invoke()
//      } yield {
//        answer should ===("Hi, Bob!")
//      }
//    }
  }
}
