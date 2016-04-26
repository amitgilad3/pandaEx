package capabilities.actors

import repository.EventRepository


trait AbstractPersistenceCapability {
  def eventRepository: EventRepository
}

trait PersistenceCapability extends AbstractPersistenceCapability {

  def eventRepository = Instantiator.eventRepository


  object Instantiator {
    val eventRepository = new EventRepository

  }
}
