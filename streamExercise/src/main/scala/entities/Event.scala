package entities

import java.util.Date

import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
  * Created by amit-work on 4/25/16.
  */
case class Event(event_type:String,data:String,timeStamp:String){}
case class DbEvent(event_type:String,wordCount:Int){
  def getEventIdKey() = Map("event_type" -> event_type)
}

object Event {
  def apply(json: String): Event = {
    implicit val formats = org.json4s.DefaultFormats
    val parsed: JValue = parse(json)
    val event_type = (parsed \ "event_type").extract[String]
    val data = (parsed \ "data").extract[String]
    Event(event_type,data,new Date().toString)
  }
}
