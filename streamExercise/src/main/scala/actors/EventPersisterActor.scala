package actors

import akka.actor.{Actor, ActorLogging}
import capabilities.actors.AbstractPersistenceCapability
import entities.DbEvent
import messages.{FinishedPersistingEvent, PersisteEvent}

/**
  * Created by amit-work on 4/24/16.
  */
class EventPersisterActor extends Actor with ActorLogging{
  this: AbstractPersistenceCapability =>
  override def receive: Receive = {
    case msg @ PersisteEvent(event) =>
      val dbEvent = DbEvent(event_type = event.event_type,wordCount = event.data.split(" ").size)
      eventRepository.createOrUpdateEvent(dbEvent)
      eventRepository.archiveEvent(event)
      context.parent ! FinishedPersistingEvent(dbEvent)
  }
}
