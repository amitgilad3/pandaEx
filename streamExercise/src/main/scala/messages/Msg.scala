package messages

import entities.{DbEvent, Event}

/**
  * Created by amit-work on 4/25/16.
  */
trait Msg {}


case class FinishedPersistingEvent(event:DbEvent)
case object CacheEventStats
case object GetEvents
case object GetAllCachedInfo
case class FinishedGettingAllCachedInfo(seqOfOBJECTS:Seq[DbEvent])
case class PersisteEvent(e:Event)
case class DoneGettingEventsFromCache(events:Seq[DbEvent])
