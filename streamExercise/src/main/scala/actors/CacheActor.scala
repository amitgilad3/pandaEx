package actors

import akka.actor.{Actor, ActorLogging}
import cache.Cache
import capabilities.actors.AbstractPersistenceCapability
import entities.DbEvent
import messages.{CacheEventStats, GetEvents}

import scalacache._

/**
  * Created by amit-work on 4/24/16.
  */
class CacheActor(implicit cache: ScalaCache = Cache.cache) extends Actor with ActorLogging {
  this: AbstractPersistenceCapability =>
  val eventsKey = "events"
  override def receive: Receive = {
    case msg @ CacheEventStats =>
      val events: Seq[DbEvent] = eventRepository.getAllEvents()
      put(eventsKey)(events)

    case msg @ GetEvents =>
      sync.get[Seq[DbEvent]](eventsKey)
  }

}