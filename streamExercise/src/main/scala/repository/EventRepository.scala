package repository

import com.typesafe.config.ConfigFactory
import entities.{Event, DbEvent}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._
import org.slf4j.LoggerFactory
import persistance.PersistanceManager._
import repository.DbCollections.DbCollection

import scala.util.{Failure, Success, Try}

/**
  * Created by amit-work on 4/24/16.
  */
class EventRepository {
  implicit val formats = DefaultFormats
  val log = LoggerFactory.getLogger(classOf[EventRepository])
  private val config = ConfigFactory.load()
  def dbName = config.getString("persistence.mongo.dbname")
  private val eventsCollection = "events"


  def addEvent(lead: DbEvent, coll: DbCollection = DbCollections.EVENTS) = {
    val json = write(lead)
    create(json, coll.toString.toLowerCase(), dbName, isJournaled = true)
  }

  def archiveEvent(event: Event, coll: DbCollection = DbCollections.ARCHIVE) = {
    val json = write(event)
    create(json, coll.toString.toLowerCase(), dbName, isJournaled = true)
  }
  def createOrUpdateEvent(event: DbEvent, coll: DbCollection = DbCollections.EVENTS) = {

    val query = write(event.getEventIdKey)
    val tuple = ("wordCount",event.wordCount)
    update(query,coll.toString.toLowerCase(), dbName, tuple,addIfAbsent = true)

  }

  def getEvent(event: DbEvent, coll: DbCollection = DbCollections.EVENTS): Option[DbEvent] = {
    getEventByKeys(event.getEventIdKey(), coll)
  }

  def getEventByKeys(findFilter: Map[String, Any], coll: DbCollection = DbCollections.EVENTS): Option[DbEvent] = {
    val lead = retrieve(findFilter, "events", dbName) match {
      case None            => None
      case Some(s: String) => tryRead(s"getEventByKeys $findFilter", s)
    }
    lead
  }

  def getAllEvents(): Seq[DbEvent] = {
    val x =retrieveAll("",eventsCollection, dbName)

      val y = x.map(s => {
        tryRead(s"get all events", s)
      })
      .filter(l => l.isDefined).map(l => l.get)
    println(x)
    y

  }
  def tryRead(callSig: String, jsonData: String): Option[DbEvent] = {
    Try(read[DbEvent](jsonData)) match {
      case Success(n: DbEvent) => Some(n)
      case Failure(ex) =>
        log.error(s"EventRepository SERIALIZATION FAILURE $callSig: ${ex.getMessage}")
        None
    }
  }
}
