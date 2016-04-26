package repository

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import entities.DbEvent
import org.scalatest.FunSuiteLike

import scalacache._


/**
 * Created by amit on 04/08/15.
 */
class eventRepositoryTest  extends FunSuiteLike with IntegrationTestsTempDbCreator {

  test("check when trying to increment event that does not wxist that it is created ") {
    tempEventRepo = new EventRepository { override def dbName = tempDbName }

    val event = DbEvent(event_type = "panda_event",wordCount = 5)
    tempEventRepo.createOrUpdateEvent(event)

    val retrievedData = tempEventRepo.getEvent(event)

    assert(retrievedData.nonEmpty)

  }


  test("check regular insert of event") {
    tempEventRepo = new EventRepository { override def dbName = tempDbName }

    val event = DbEvent(event_type = "panda_event",wordCount = 5)
    tempEventRepo.addEvent(event)

    val retrievedData = tempEventRepo.getEvent(event)

    assert(retrievedData.nonEmpty)

  }

  test("check update of event") {
    tempEventRepo = new EventRepository { override def dbName = tempDbName }

    val event = DbEvent(event_type = "panda_event",wordCount = 5)
    tempEventRepo.addEvent(event)


    tempEventRepo.createOrUpdateEvent(event)

    val retrievedData = tempEventRepo.getEvent(event)

    assert(retrievedData.nonEmpty)
    assert(retrievedData.get.wordCount === 10)

  }

}
