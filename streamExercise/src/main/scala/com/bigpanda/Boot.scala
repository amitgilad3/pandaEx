package com.bigpanda

import java.io.{BufferedReader, InputStreamReader}

import actors.{StreamActorCreator, EventPersisterActorCreator, CacheActorCreator, Coordinator}
import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.pattern.ask
import service.StatsAct
import spray.can.Http
import scala.concurrent.duration._
import akka.util.Timeout
/**
  * Created by amit-work on 4/24/16.
  */
object Boot extends App{




  implicit val timeout = Timeout(5.seconds)

  // ActorSystem to host our application in
  implicit val actorSystem = ActorSystem("eventData-system")

  val statsService = actorSystem.actorOf(Props[StatsAct] , "StatsAct")
  IO(Http) ? Http.Bind(statsService, interface = "localhost", port = 8080)


}
