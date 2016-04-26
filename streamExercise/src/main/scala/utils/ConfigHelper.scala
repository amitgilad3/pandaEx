package utils

import com.typesafe.config.Config

/**
  * Created by amit-work on 4/24/16.
  */
object ConfigHelper {

  def getMongoConnectionString(config: Config): String = {
    val port = "27017"
    val ips = config.getString("persistence.mongo.ips").split(" ")
    val clusterUri = ips mkString ("mongodb://", s":$port,", s":$port/")
    val configParams = s"?maxPoolSize=${config.getString("persistence.mongo.connections")}"
    clusterUri + configParams
  }
}