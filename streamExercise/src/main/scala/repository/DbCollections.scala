package repository

/**
  * Created by amit-work on 4/24/16.
  */
object DbCollections extends Enumeration {
  type DbCollection = Value

  val EVENTS, ARCHIVE , JUNK = Value

  class CollectionName(cl: DbCollection) {

    def collectionName = cl match {
      case EVENTS         => "events"
      case ARCHIVE       => "archive"
      case _             => "junk"

    }
  }

  class StringToCollection(collName: String) {
    def toDbCollection = collName match {
      case "LEADS"         => EVENTS
      case "LEADS_ARCHIVE" => ARCHIVE
      case _               => JUNK
    }
  }
}
