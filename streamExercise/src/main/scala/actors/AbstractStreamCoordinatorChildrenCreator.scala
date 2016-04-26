package actors

import akka.actor.{ActorRef, ActorRefFactory, Props}
import capabilities.actors.PersistenceCapability


trait AbstractStreamCoordinatorChildrenCreator {
  def constructCacheActor(ctx: Option[ActorRefFactory]): ActorRef
  def constructEventPersisterActor(ctx: Option[ActorRefFactory]): ActorRef
  def constructStreamActor(ctx: Option[ActorRefFactory]): ActorRef
}

trait CacheActorCreator extends AbstractStreamCoordinatorChildrenCreator {
  def constructCacheActor(ctx: Option[ActorRefFactory]) = ctx.get actorOf (Props(new CacheActor with PersistenceCapability), classOf[CacheActor].getSimpleName)
}

trait EventPersisterActorCreator extends AbstractStreamCoordinatorChildrenCreator {
  def constructEventPersisterActor(ctx: Option[ActorRefFactory]) = ctx.get actorOf (Props(new EventPersisterActor with PersistenceCapability), classOf[EventPersisterActor].getSimpleName)
}


  trait StreamActorCreator extends AbstractStreamCoordinatorChildrenCreator {
  def constructStreamActor(ctx: Option[ActorRefFactory]) = ctx.get actorOf (Props(new StreamActor ), classOf[StreamActor].getSimpleName)
}
