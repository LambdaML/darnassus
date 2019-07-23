package actors

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{AsyncWordSpecLike, BeforeAndAfterAll, Matchers}

class JobCoordinatorActorSpec extends TestKit(ActorSystem("job-coordinator-actor-spec"))
  with AsyncWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A JobCoordinatorActor" should {
    "do something" in {
      fail()
    }

    "do something else" in {
      fail()
    }
  }
}
