package actors

import java.util.Date

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import capabilities.actors.PersistenceCapability
import entities.{DbEvent, Event}
import messages.{FinishedPersistingEvent, PersisteEvent}
import org.scalatest.FunSuiteLike
import org.scalatest.concurrent.ScalaFutures
import repository.IntegrationTestsTempDbCreator
import scala.concurrent.duration.DurationInt
/**
  * Created by amit-work on 4/25/16.
  */
class EventPersisterActorTest extends TestKit(ActorSystem("EventPersisterActorTest"))
  with FunSuiteLike
  with IntegrationTestsTempDbCreator
  with ScalaFutures {
  test("TEST EventPersisterActorTest") {

    val proxy = TestProbe()
    val parent = system.actorOf(Props(new Actor {
      val child = context.actorOf(Props(
        new EventPersisterActor() with PersistenceCapability {
          override def eventRepository = tempEventRepo
        }
      ), "child")

      def receive = {
        case x if sender == child => proxy.ref forward x
        case x => child forward x
      }
    }))

    val time = new Date()
    val event = new Event(event_type = "panda" , data = "big panda" , timeStamp = time.toString)
    val dbEvent = DbEvent(event_type = "panda",wordCount = 2)
    val msgIn =  PersisteEvent(event)
    val msgOut = FinishedPersistingEvent(dbEvent)

    proxy.send(parent, msgIn)
    proxy.expectMsg(10 seconds, msgOut)

  }
}