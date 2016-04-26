package persistance


import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports
import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.{MongoClient, MongoClientURI, MongoCollection, MongoDB}
import com.mongodb.util.JSON
import com.mongodb.{DBObject, WriteConcern}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import utils.ConfigHelper

import scala.collection.immutable.Map
import scala.util.Try

/**
  * Created by amit-work on 4/24/16.
  */
class MongoConnectionHandler (val client: MongoClient) {
  def getDb(dbName: String): MongoDB = client(dbName)

  def getCollection(db: MongoDB, collection: String): MongoCollection = {
    db(collection)
  }
  def closeConnection() = client.close
}

object MongoConnectionHandler {
  val logger = LoggerFactory.getLogger(classOf[MongoConnectionHandler])
  private val config: Config = ConfigFactory.load()
  val uri = ConfigHelper.getMongoConnectionString(config)


  def apply() = {
    logger.info("Mongo URI is: " + uri)
    new MongoConnectionHandler(MongoClient(MongoClientURI(uri)))

  }

}
class PersistanceManager
object PersistanceManager {
  val logger = LoggerFactory.getLogger(classOf[PersistanceManager])

  val connection = MongoConnectionHandler()


  def dropDb(dbName: String) = {
    val persistenceHandler = MongoConnectionHandler()
    val db: MongoDB = persistenceHandler getDb (dbName)
    db.dropDatabase()
  }

  def dbExists(dbName: String) = {
    connection.client.dbNames().contains(dbName)
  }

  private def touchDataStore[T](indexJson: Option[String] = None, collectionName: String, dbName: String)(op: (MongoCollection, DBObject) => T): T = {
    val mongoCollection= connection getCollection (connection getDb (dbName), collectionName)

    val mongoIndex: DBObject = indexJson match {
      case None            => null
      case s: Some[String] => JSON.parse(s.get).asInstanceOf[DBObject]
    }
    val retval = op(mongoCollection, mongoIndex)
    retval
  }



  def create(json: String, collectionName: String, dbName: String, isJournaled: Boolean = false) = {
    val concern: WriteConcern = if (!isJournaled) WriteConcern.ACKNOWLEDGED else WriteConcern.JOURNALED
    touchDataStore(Some(json), collectionName, dbName = dbName)(
      (mongoCollection, mongoJson) => Try(Option(mongoCollection.insert(mongoJson, concern))).recover { case ex: Exception => logger.error(ex.getMessage, ex) }
    )
  }
  def update(query: String, collectionName: String, dbName: String, tuple:Tuple2[String,Int] ,
             many: Boolean = false, addIfAbsent: Boolean = false) = {

    val inc: Imports.DBObject = $inc(tuple)
    touchDataStore(Some(query), collectionName, dbName = dbName)(
      (mongoCollection, mongoJson) =>
        Try(Option(mongoCollection.update(mongoJson, inc, multi = many, upsert = addIfAbsent)))).recover {
      case ex: Exception =>
        logger.error(ex.getMessage, ex)
    }
  }

  def retrieve(findFields: Map[String, Any], collectionName: String, dbName: String) = {
    val json = MongoDBObject(findFields.map(kvp => kvp._1 -> kvp._2).toArray: _*).toString
    touchDataStore(Some(json), collectionName, dbName = dbName)(
      (mongoCollection, mongoJson) => mongoCollection.findOne(mongoJson) match {
        case dbo: Some[DBObject] => Some(dbo.get.toString)
        case _                   => None
      })
  }

  def retrieveAll(json: String, collectionName: String, dbName: String): Seq[JSFunction] = {
    touchDataStore(Some(json), collectionName, dbName = dbName)(
      op = (mongoCollection, mongoJson) =>
        mongoCollection.find(mongoJson).toSeq.map(_.toString))
  }
  def retrieveCollectionData(collectionName: String, dbName: String): Seq[JSFunction] = {
    touchDataStore(Some(""), collectionName, dbName = dbName)(
      op = (mongoCollection, mongoJson) =>
        mongoCollection.find().toSeq.map(_.toString))
  }
}