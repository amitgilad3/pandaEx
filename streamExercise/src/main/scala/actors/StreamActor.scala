package actors

import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import cache.Cache
import entities.Event
import messages.PersisteEvent

import scala.sys.process.Process
import scala.util.{Failure, Success, Try}
import scalacache.ScalaCache

/**
  * Created by amit-work on 4/24/16.
  */
class StreamActor extends Actor with ActorLogging  {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  override def receive: Receive = {

    case "Go" => {
      val contents = Process("/home/amit-work/Downloads/stream/generator-linux-amd64").lines
      Source.fromIterator(() => contents.iterator).
        runForeach(s => {
          val x = Try(Event(s))
          x match {
            case Success(e) =>  context.parent ! PersisteEvent(e)
            case Failure(f) => log.warning(s"parser error: ${f}")
          }

        })
    }

    case m => log.warning(s"StreamActor has received an unknown message: $m")
  }
}


