package utils

import com.typesafe.config.Config

/**
  * Created by amit-work on 4/24/16.
  */
object ConfigHelper {

  def getMongoConnectionString(config: Config): String = {
    val port = "27017"
    val ips = config.getString("persistence.mongo.ips").split(" ")
    //mongodb://192.1.21.108:27017,127.0.0.1:27017/
    val clusterUri = ips mkString ("mongodb://", s":$port,", s":$port/")
    val configParams = s"?maxPoolSize=${config.getString("persistence.mongo.connections")}"
    clusterUri + configParams
  }
}