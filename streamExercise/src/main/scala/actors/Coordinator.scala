package actors

import akka.actor.{Actor, ActorLogging}
import messages.{DoneGettingEventsFromCache, GetEvents, CacheEventStats, PersisteEvent}
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by amit-work on 4/24/16.
  */
class Coordinator extends Actor with ActorLogging  {
  this: AbstractStreamCoordinatorChildrenCreator =>

  val cacheActor = constructCacheActor(Some(context))
  val eventPersisterActor = constructEventPersisterActor(Some(context))
  val streamActor = constructStreamActor(Some(context))

  context.system.scheduler.schedule(5 seconds,10 second, cacheActor, CacheEventStats)
  override def receive: Receive = {

    //streaming data
    case "Go" => streamActor ! "Go"
    case msg @ PersisteEvent(event) => eventPersisterActor ! PersisteEvent(event)
    case msg @ GetEvents => cacheActor ! GetEvents
    case DoneGettingEventsFromCache(events) => context.parent ! DoneGettingEventsFromCache(events)
  }


}
