package service

import actors._
import akka.actor.{Actor, ActorLogging, Props}
import cache.Cache
import entities.DbEvent
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._
import org.slf4j.LoggerFactory
import spray.http.StatusCodes._
import spray.routing.{HttpService, _}
import scala.concurrent.ExecutionContext
import scalacache.{ScalaCache, sync}
class StatsAct extends StatsActService with Actor with ActorLogging {
  override def preStart() = {log.info("Actor " +  self.path.name  + " initialized")}
  override def postStop() = {log.info("Actor " +  self.path.name  + " stoped")}
  def receive: Receive =  runRoute(statsRoute)

}

trait StatsActService  extends HttpService with Actor with ActorLogging{
  implicit def actorRefFactory = context
  implicit val formats = DefaultFormats
  implicit val ec: ExecutionContext = actorRefFactory.dispatcher
  val coordinator = actorRefFactory.actorOf(Props(new Coordinator with CacheActorCreator  with EventPersisterActorCreator with StreamActorCreator), classOf[Coordinator].getSimpleName)
  coordinator ! "Go"
  implicit val cache: ScalaCache = Cache.cache
  val logger = LoggerFactory.getLogger(classOf[StatsActService])
  val aroundSpraysRejections = RejectionHandler {
    case Nil => complete(NotFound, "The url path does not match http://<hostname>:8080/stats")
    case MethodRejection(supported) :: _ => complete(MethodNotAllowed,s"The only supported http methods are: $supported")
    case ValidationRejection(msg,None) :: _ => complete(BadRequest, msg)
  }
  //coordinator ! "Go"
  val statsRoute = handleRejections(aroundSpraysRejections) {
    pathSuffix("stats") {
      get{
        anyParams('uid.as[String] ?) { (uid) =>
          logger.info("get request recieved to refresh cache" )
          val seq =sync.get[Seq[DbEvent]]("events").getOrElse(List.empty[String])
          complete(s"current number of events is : ${seq.size} \n events:${write(seq)}")
        }
      }

    }
  }

}


